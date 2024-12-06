package com.nodove.WSD_Assignment_03.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nodove.WSD_Assignment_03.configuration.token.components.tokenDto;
import com.nodove.WSD_Assignment_03.configuration.token.jwtUtilities;
import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.users.Redis_Refresh;
import com.nodove.WSD_Assignment_03.dto.users.UserLoginRequest;
import com.nodove.WSD_Assignment_03.service.redisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class authenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final jwtUtilities jwtUtilities;
    private final ObjectMapper objectMapper;
    private final redisService redisService;

    public authenticationFilter(AuthenticationManager authenticationManager, jwtUtilities jwtUtilities, ObjectMapper objectMapper, redisService redisService) {
        super.setFilterProcessesUrl("/auth/login"); // login url  변경하기
        this.authenticationManager = authenticationManager;
        this.jwtUtilities = jwtUtilities;
        this.objectMapper = objectMapper;
        this.redisService = redisService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║                       Authentication Filter                   ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        try {
            UserLoginRequest userLoginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginRequest.getUserId(), userLoginRequest.getPassword());
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) throws  IOException{
        log.info("Authentication Success");
        principalDetails principalDetails = (principalDetails) authentication.getPrincipal();
        tokenDto newToken = jwtUtilities.generateToken(authentication);

        // redis 에 refresh token 저장
        Redis_Refresh newRedis_refresh = Redis_Refresh.builder()
                .userId(principalDetails.getUserId())
                .provider("LOCAL")
                .deviceId(UUID.randomUUID().toString())
                .build();
        redisService.saveRefreshToken(newRedis_refresh, newToken.getRefreshToken());

        // Access Token 을 Header 에 추가
        jwtUtilities.loginResponse(response, newToken);
    }
}
