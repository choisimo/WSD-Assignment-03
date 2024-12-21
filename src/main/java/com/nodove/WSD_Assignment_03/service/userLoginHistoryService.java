package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.domain.userLoginHistory;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.users.UserLoginRequest;
import com.nodove.WSD_Assignment_03.repository.userLoginHistoryRepository;
import com.nodove.WSD_Assignment_03.repository.usersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class userLoginHistoryService {

    private final userLoginHistoryRepository  loginHistoryRepository;
    private final usersRepository usersRepository;

    // Updated method to save login history
    @Transactional
    public void saveLoginHistory(UserLoginRequest request, HttpServletRequest httpRequest) {
        users user = usersRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Extract IP address and User-Agent from request
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        // Create and save login history
        userLoginHistory loginHistory = new userLoginHistory();
        loginHistory.setUser(user);
        loginHistory.setLoginTime(LocalDateTime.now());
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setUserAgent(userAgent);
        loginHistoryRepository.save(loginHistory);

    }
}