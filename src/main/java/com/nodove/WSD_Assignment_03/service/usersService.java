package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.jwtUtilities;
import com.nodove.WSD_Assignment_03.configuration.utility.password.Base64PasswordEncoder;
import com.nodove.WSD_Assignment_03.constants.securityConstants;
import com.nodove.WSD_Assignment_03.domain.Role;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.users.UserRegisterRequest;
import com.nodove.WSD_Assignment_03.repository.usersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class usersService {

    private final redisService redisService;
    private final usersRepository usersRepository;
    private final jwtUtilities jwtUtilities;

    public boolean isTokenBlackListed(String token) {

        if (redisService.checkBlackList(token)) {
            log.info("Token is blacklisted");
            return true;
        }

        return usersRepository.findByUserId(jwtUtilities.parseToken(token, 0).get("userId").toString())
                .map(users::isBlocked)
                .orElse(false);
    }


    public ResponseEntity<?> refreshAcessToken(HttpServletRequest request, HttpServletResponse response){

        try {
            String refreshToken = jwtUtilities.getRefreshToken(request);
            if (refreshToken == null || jwtUtilities.isTokenExpired(refreshToken, 1)) {
                log.error("Refresh Token is invalid or expired");
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body("Refresh Token is invalid or expired. Please log in again.");
            }

            String userId = jwtUtilities.parseToken(refreshToken, 1).get("userId").toString();
            String newAccessToken = jwtUtilities.generateAccessToken(userId);

            log.info("Access Token reissued successfully for userId: {}", userId);
            response.setHeader(securityConstants.TOKEN_HEADER, securityConstants.TOKEN_PREFIX + newAccessToken);
            return ResponseEntity.ok()
                    .header(securityConstants.TOKEN_HEADER, securityConstants.TOKEN_PREFIX + newAccessToken)
                    .body("Access Token refreshed successfully.");
        } catch (Exception e) {
            log.error("Error refreshing Access Token: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Failed to refresh Access Token.");
        }
    }


    public ResponseEntity<?> registerUser(UserRegisterRequest request) {
        log.info("회원가입 요청: {}", request.getEmail());

        // 중복 사용자 체크
        if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("이미 존재하는 이메일: {}", request.getEmail());
            return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
        }

        if (usersRepository.findByUserId(request.getUserId()).isPresent()) {
            log.error("이미 존재하는 사용자 ID: {}", request.getUserId());
            return ResponseEntity.badRequest().body("이미 존재하는 사용자 ID입니다.");
        }

        // 비밀번호 암?호?화 (Base64)
        String encodedPassword = Base64PasswordEncoder.encode(request.getPassword());

        // 사용자 생성 및 저장
        users newUser = users.builder()
                .userId(request.getUserId())
                .password(encodedPassword)
                .email(request.getEmail())
                .username(request.getUsername())
                .nickname(request.getNickname())
                .role(Role.valueOf("USER"))
                .build();

        usersRepository.save(newUser);

        log.info("회원가입 성공: {}", newUser.getUserId());
        return ResponseEntity.ok("회원가입 성공");
    }

}
