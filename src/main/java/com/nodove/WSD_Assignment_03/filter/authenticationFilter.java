package com.nodove.WSD_Assignment_03.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nodove.WSD_Assignment_03.configuration.token.components.tokenDto;
import com.nodove.WSD_Assignment_03.configuration.token.jwtUtilities;
import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.configuration.utility.password.Base64PasswordEncoder;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.users.Redis_Refresh;
import com.nodove.WSD_Assignment_03.dto.users.UserLoginRequest;
import com.nodove.WSD_Assignment_03.service.redisService;
import com.nodove.WSD_Assignment_03.service.userLoginHistoryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class authenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final jwtUtilities jwtUtilities;
    private final ObjectMapper objectMapper;
    private final redisService redisService;
    private final userLoginHistoryService userLoginHistoryService;

    private Map<String, String> requestCache = new ConcurrentHashMap<>();

    public authenticationFilter(AuthenticationManager authenticationManager, jwtUtilities jwtUtilities, ObjectMapper objectMapper, redisService redisService, userLoginHistoryService userLoginHistoryService) {
        super.setFilterProcessesUrl("/auth/login"); // login url  변경하기
        this.authenticationManager = authenticationManager;
        this.jwtUtilities = jwtUtilities;
        this.objectMapper = objectMapper;
        this.redisService = redisService;
        this.userLoginHistoryService = userLoginHistoryService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║                       Authentication Filter                   ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        try {
            UserLoginRequest userLoginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);

            requestCache.put("requestBody", objectMapper.writeValueAsString(userLoginRequest));

            String encodedPassword = Base64PasswordEncoder.encode(userLoginRequest.getPassword());
            log.info("User ID: " + userLoginRequest.getUserId());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginRequest.getUserId(), encodedPassword);
            return authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            log.error("Authentication Failed");
            sendErrorResponse(response, "Invalid credentials", "AUTHENTICATION_FAILED", 401);
            throw e;
        } catch (IOException e) {
            sendErrorResponse(response, "Failed to process request", "REQUEST_PARSING_ERROR", 400);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) throws IOException {
        log.info("Authentication Success");

        String requestLogin = requestCache.get("requestBody");
        UserLoginRequest userLoginRequest = objectMapper.readValue(requestLogin, UserLoginRequest.class);

        this.userLoginHistoryService.saveLoginHistory(userLoginRequest, request);

        principalDetails principalDetails = (principalDetails) authentication.getPrincipal();
        tokenDto newToken = jwtUtilities.generateToken(authentication);

        String deviceId = UUID.randomUUID().toString();
        // redis 에 refresh token 저장
        Redis_Refresh newRedis_refresh = Redis_Refresh.builder()
                .userId(principalDetails.getUserId())
                .provider("LOCAL")
                .deviceId(deviceId)
                .build();
        redisService.saveRefreshToken(newRedis_refresh, newToken.getRefreshToken());

        // Access Token 을 Header 에 추가
        jwtUtilities.loginResponse(response, newToken, deviceId);
    }


    private void sendErrorResponse(HttpServletResponse response, String message, String code, int status) {
        try {
            ApiResponseDto<Void> errorResponse = ApiResponseDto.<Void>builder()
                    .status("error")
                    .message(message)
                    .code(code)
                    .build();

            response.setStatus(status);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("Failed to send error response: {}", e.getMessage());
        }
    }
}