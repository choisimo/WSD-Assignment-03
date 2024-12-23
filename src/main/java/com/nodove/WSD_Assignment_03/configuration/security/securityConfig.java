package com.nodove.WSD_Assignment_03.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nodove.WSD_Assignment_03.configuration.token.jwtUtilities;
import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetailsService;
import com.nodove.WSD_Assignment_03.configuration.utility.password.Base64PasswordEncoder;
import com.nodove.WSD_Assignment_03.filter.authenticationFilter;
import com.nodove.WSD_Assignment_03.filter.authorizationFilter;
import com.nodove.WSD_Assignment_03.service.redisService;
import com.nodove.WSD_Assignment_03.service.userLoginHistoryService;
import com.nodove.WSD_Assignment_03.service.usersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class securityConfig {

    private final principalDetailsService principalDetailsService;
    private final jwtUtilities jwtUtilities;
    private final redisService redisService;
    private final usersService usersService;
    private final userLoginHistoryService userLoginHistoryService;

    @Value("${site.domain}")
    private String domain;
    @Value("${site.cookie.domain}")
    private String cookieDomain;

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CorsConfigurationSource corsConfigurationSource;

    private final List<String> permitList = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/swagger-io.html"
    );

    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // cors
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));
        // 폼 로그인 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);
        // Cross-Site Request Forgery 공격 방어 비활성화
        http.csrf(AbstractHttpConfigurer::disable);
        // HTTP 기본 인증 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);
        // session 기반 로그인 비활성화
        http.sessionManagement(management ->
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //filter
        http.addFilterBefore(new authorizationFilter(jwtUtilities, objectMapper(), redisService, usersService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(new authenticationFilter(authenticationManager(), this.jwtUtilities, objectMapper(), redisService, userLoginHistoryService), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests((authorize) -> {
            authorize.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
            authorize.requestMatchers("/api/public/**").permitAll();
            authorize.requestMatchers(permitList.toArray(new String[0])).permitAll();
            authorize.requestMatchers("/api/private/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN");
            authorize.requestMatchers("/api/protected/**").hasAnyAuthority("USER", "COMPANY", "ADMIN", "ROLE_USER", "ROLE_COMPANY", "ROLE_ADMIN");
            authorize.anyRequest().permitAll();
        });
        return http.build();
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

    // todo : 실배포 시 passwordEncoder 변경 필요
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}


