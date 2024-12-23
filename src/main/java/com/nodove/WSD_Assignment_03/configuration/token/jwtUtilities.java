package com.nodove.WSD_Assignment_03.configuration.token;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.configuration.token.components.tokenDto;
import com.nodove.WSD_Assignment_03.constants.securityConstants;
import com.nodove.WSD_Assignment_03.domain.Role;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.repository.usersRepository;
import com.nodove.WSD_Assignment_03.service.redisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
public class jwtUtilities {

    private final com.nodove.WSD_Assignment_03.repository.usersRepository usersRepository;
    @Value("${site.domain}")
    private String domain;
    @Value("${site.cookie.domain}")
    private String cookieDomain;

    private final Key key;
    private final Key key2;
    private final Long ACCESS_TOKEN_VALIDITY;
    private final Long REFRESH_TOKEN_VALIDITY;

    private final redisService redisService;

    public jwtUtilities(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.secret-key2}") String secretKey2,
            @Value("${ACCESS_TOKEN_VALIDITY}") Long ACCESS_TOKEN_VALIDITY,
            @Value("${REFRESH_TOKEN_VALIDITY}") Long REFRESH_TOKEN_VALIDITY,
            usersRepository usersRepository, redisService redisService){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.key2 = Keys.hmacShaKeyFor(secretKey2.getBytes());
        this.ACCESS_TOKEN_VALIDITY = ACCESS_TOKEN_VALIDITY;
        this.REFRESH_TOKEN_VALIDITY = REFRESH_TOKEN_VALIDITY;
        this.usersRepository = usersRepository;
        this.redisService = redisService;
    }

    public tokenDto generateToken(Authentication authentication)
    {
        principalDetails principalDetails = (principalDetails) authentication.getPrincipal();
        String userId = principalDetails.getUserId();
        String username = principalDetails.getUsername();
        String nickname = principalDetails.getNickname();
        Collection<? extends GrantedAuthority> role = principalDetails.getAuthorities();

        return new tokenDto(
                generateAccessToken(userId, nickname, role),
                generateRefreshToken(userId)
        );
    }

    public String generateAccessToken(String userId)
    {
        // userId 기반으로 사용자 조회, 사용자 존재 시 로그인 기록 등록
        users user = usersRepository.findByUserId(userId).map(action -> {
        this.redisService.saveUserLogin(userId);
        return action;
    }).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return generateAccessToken(user.getUserId(), user.getNickname(), user.getAuthorities());
    }

    private String generateAccessToken(String userId, String nickname, Collection<? extends GrantedAuthority> role)
    {
        List<String> roles = role.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(userId)
                .claim("nickname", nickname)
                .claim("userId", userId)
                .claim("role", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY * 1000))
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken(String userId)
    {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY * 1000))
                .signWith(key2)
                .compact();
    }



    public UsernamePasswordAuthenticationToken getAuthentication(String token)
    {
        try {
            // parse the token (access token).
            Jws<Claims> parsedToken = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            // get the user information from the token.
            String userId = (String) parsedToken.getBody().get("userId", String.class);
            String nickname = (String) parsedToken.getBody().get("nickname", String.class);
            List<String> roles = parsedToken.getBody().get("role", List.class);

            List<Role> roleEnums = roles.stream()
                    .map(role -> {
                        String subRole = role.substring(5); // "ROLE_" 제거
                        try {
                            log.info("role: {}", subRole);
                            return Role.valueOf(subRole); // 정확히 매핑되는 경우
                        } catch (IllegalArgumentException e) {
                            log.warn("Invalid role: {}. Skipping...", subRole);
                            return null; // 매핑되지 않는 경우 null 반환
                        }
                    })
                    .filter(Objects::nonNull) // null 제거
                    .toList();

            users user = users.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .role(roleEnums.get(0)) // 주어진 상황에서는 첫 번째 역할을 선택
                    .build();


            UserDetails userDetails = new principalDetails(user);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } catch (Exception e){
            log.error("authentication 과정에서 Exception 발생! {}", e.getMessage());
            return null;
        }
    }

    // token 전달 시, response에 token을 담아서 전달.
    public void loginResponse(HttpServletResponse response, tokenDto tokenDto, String deviceId) throws IOException {
        response.reset(); // response 초기화
        Cookie newCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        newCookie.setHttpOnly(false); // TODO : Http-only 으로 수정 | secure 설정
        newCookie.setDomain(null); // TODO : domain 설정  | test 하느라 null 설정함
        newCookie.setPath("/");
        response.addCookie(newCookie);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(ApiResponseDto.builder()
                .code("LOGIN_SUCCESS")
                .status("success")
                .message("로그인 성공")
                .build().toString());
        response.addHeader(securityConstants.TOKEN_HEADER,
                securityConstants.TOKEN_PREFIX + tokenDto.getAccessToken());
        response.addHeader(securityConstants.DEVICE_ID, deviceId);
        response.addHeader("refreshToken", tokenDto.getRefreshToken());
    }


    // parsing token
    // type 0: access token, type 1: refresh token
    public Map<String, Object> parseToken(String token, int type)
    {
        Key key = (type == 0) ? this.key : this.key2;

        Jws<Claims> parsedToken = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        Map<String, Object> result = new HashMap<>();
        if (type == 0) {
            result.put("userId", parsedToken.getBody().get("userId"));
            result.put("nickname", parsedToken.getBody().get("nickname"));
            result.put("role", parsedToken.getBody().get("role"));
        } else {
            result.put("userId", parsedToken.getBody().getSubject());
        }
        return result;
    }


    // type 0: access token, type 1: refresh token
    public boolean isTokenExpired(String token, int type) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(type == 0 ? key : key2)
                    .setAllowedClockSkewSeconds(60) // 60초 허용
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return true; // 토큰 만료
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("지원되지 않는 JWT 토큰 형식입니다.", e);
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 JWT 토큰 형식입니다.", e);
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new IllegalArgumentException("JWT 서명이 유효하지 않습니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument for JWT parsing: {}", e.getMessage());
            throw new IllegalArgumentException("JWT 처리 중 잘못된 인수가 전달되었습니다.", e);
        } catch (Exception e) {
            log.error("Unknown error during JWT parsing: {}", e.getMessage());
            throw new RuntimeException("JWT 처리 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }


    public String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                log.info("cookie name: {}", cookie.getName());
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // 쿠키에서 refreshToken을 찾지 못한 경우
    }

}
