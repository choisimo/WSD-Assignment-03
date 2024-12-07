package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.jwtUtilities;
import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.configuration.utility.password.Base64PasswordEncoder;
import com.nodove.WSD_Assignment_03.constants.securityConstants;
import com.nodove.WSD_Assignment_03.domain.Role;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.users.UserProfileRequest;
import com.nodove.WSD_Assignment_03.dto.users.UserRegisterRequest;
import com.nodove.WSD_Assignment_03.dto.users.emailRequest;
import com.nodove.WSD_Assignment_03.repository.usersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
    private final smtpService emailService;
    private final Base64PasswordEncoder base64PasswordEncoder;

    public boolean isTokenBlackListed(String token) {

        if (redisService.checkBlackList(token)) {
            log.info("Token is blacklisted");
            return true;
        }

        return usersRepository.findByUserId(jwtUtilities.parseToken(token, 0).get("userId").toString())
                .map(users::isBlocked)
                .orElse(false);
    }


    public boolean isNicknameExists(String nickname) {
        return usersRepository.findByNickname(nickname).isPresent();
    }
    public boolean isUserIdExists(String userId) {
        return usersRepository.findByUserId(userId).isPresent();
    }


    @Transactional
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


    @Transactional
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


    @Transactional
    public ResponseEntity<?> updateProfile(principalDetails principalDetails, UserProfileRequest request) {
        try {
            log.info("프로필 업데이트 요청: {}", principalDetails.getUserId());

            users user = usersRepository.findByUserId(principalDetails.getUserId()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            if (request.getEmail() != null && request.getEmailVerificationCode() != null) {
                // email 중복 체크
                emailRequest emailRequest1 = new emailRequest();
                emailRequest1.setEmail(request.getEmail());
                if (this.emailService.checkEmailExists(emailRequest1)) {
                    return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
                }

                // email 인증 코드 확인
                if (this.redisService.getVerificationCode(request.getEmail()).equals(request.getEmailVerificationCode())) {
                    this.redisService.deleteVerificationCode(request.getEmail());
                } else {
                    return ResponseEntity.badRequest().body("이메일 인증 코드가 일치하지 않습니다.");
                }
            }

            // 닉네임 중복 체크
            if (isNicknameExists(request.getNickname()))
            {
                return ResponseEntity.badRequest().body("이미 존재하는 닉네임입니다.");
            }

            boolean nicknameChanged = !request.getNickname().equals(user.getNickname());

            // password 체크
            if (!Base64PasswordEncoder.matches(request.getOriginPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
            }

            // 사용자 정보 업데이트
            users updatedUser = users.builder()
                    .userId(user.getUserId())
                    .password(Base64PasswordEncoder.encode(request.getNewPassword()))
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .nickname(request.getNickname())
                    .role(user.getRole())
                    .build();

            // 사용자 정보 업데이트
            usersRepository.save(updatedUser);

            // 닉네임 변경으로 인한 새로운 Access Token 발급 (닉네임 변경 시에만)
            if (nicknameChanged) {
                log.info("닉네임 변경으로 인한 새로운 Access Token 발급: {}", updatedUser.getUserId());
                String newAccessToken = jwtUtilities.generateAccessToken(updatedUser.getUserId());
                return ResponseEntity.ok()
                        .header(securityConstants.TOKEN_HEADER, securityConstants.TOKEN_PREFIX + newAccessToken)
                        .body("프로필 업데이트 성공");
            }

            return ResponseEntity.ok("프로필 업데이트 성공");
        } catch (Exception e) {
            log.error("프로필 업데이트 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("프로필 업데이트에 실패했습니다.");
        }
    }

}
