package com.nodove.WSD_Assignment_03.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nodove.WSD_Assignment_03.configuration.token.jwtUtilities;
import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.constants.securityConstants;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.service.redisService;
import com.nodove.WSD_Assignment_03.service.usersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class authorizationFilter extends OncePerRequestFilter {

    private final jwtUtilities jwtUtilities;
    private final ObjectMapper objectMapper;
    private final redisService redisService;
    private final usersService usersService;


    public authorizationFilter(jwtUtilities jwtUtilities, ObjectMapper objectMapper, redisService redisService, usersService usersService) {
        this.jwtUtilities = jwtUtilities;
        this.objectMapper = objectMapper;
        this.redisService = redisService;
        this.usersService = usersService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║                       Authorization Filter                    ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        String authorizationHeader = request.getHeader(securityConstants.TOKEN_HEADER);

        if (authorizationHeader == null || !authorizationHeader.startsWith(securityConstants.TOKEN_PREFIX))
        {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            boolean tokenChanged = false;
            String token = authorizationHeader.substring(securityConstants.TOKEN_PREFIX.length());
            String baseToken = token;

            String userId;
            if (jwtUtilities.isTokenExpired(token, 0)) {
                log.warn("Access Token is expired. Checking Refresh Token for reissue.");
                String refreshToken = jwtUtilities.getRefreshToken(request);
                if (refreshToken == null || jwtUtilities.isTokenExpired(refreshToken, 1)) {
                    log.error("Refresh Token is invalid or expired.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(objectMapper.writeValueAsString(
                            ApiResponseDto.builder()
                                    .status("error")
                                    .message("Refresh Token is invalid or expired. Please log in again.")
                                    .code("TOKEN_EXPIRED")
                                    .build()
                    ));
                    return;
                }
                userId = jwtUtilities.parseToken(refreshToken, 1).get("userId").toString();
                token = jwtUtilities.generateAccessToken(userId);
                tokenChanged = true;
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                response.getWriter().write(objectMapper.writeValueAsString(
                        ApiResponseDto.builder()
                                .status("success")
                                .message("Access Token reissued successfully")
                                .code("TOKEN_REISSUED")
                                .build()
                ));
                response.setHeader(securityConstants.TOKEN_HEADER, securityConstants.TOKEN_PREFIX + token);
                log.info("Access Token reissued successfully");
            }

            if (tokenChanged ? usersService.isTokenBlackListed(baseToken) : usersService.isTokenBlackListed(token)) {
                log.error("Token is blacklisted");
                if (tokenChanged) {
                    redisService.saveBlackList(token);
                }
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(ApiResponseDto.builder()
                        .status("error")
                        .message("blacklisted user")
                        .code("403")
                        .build().toString()
                );
                return;
            }

            if (!tokenChanged && jwtUtilities.isTokenExpired(token, 0)) {
                log.error("Token is expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(ApiResponseDto.builder()
                        .code("403")
                        .message("token is expired")
                        .status("error")
                        .build().toString());
                return;
            }

            Authentication authentication = jwtUtilities.getAuthentication(token);
            if (authentication == null) {
                log.error("Authentication failed for token: {}", token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(ApiResponseDto.builder()
                        .code("403")
                        .message("Authentication failed")
                        .status("error")
                        .build().toString());
                return;
            }
/*
            if (authentication.getPrincipal() instanceof principalDetails userDetails) {
                // upcasting to principalDetails
                if (userDetails.getUser().isDeleted() || !userDetails.isAccountNonLocked()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(ApiResponseDto.<Void>builder()
                            .status("error")
                            .message("Account is locked or deleted")
                            .code("ACCOUNT_LOCKED_OR_DELETED")
                            .build().toString());
                    return;
                }
            }*/

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }  catch (Exception e) {
            log.error("Error occurred while extracting token from header: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(ApiResponseDto.builder()
                    .status("error")
                    .message("Error occurred while extracting token from header due to " + e.getMessage())
                    .code("403")
                    .build().toString()
            );
            return;
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
