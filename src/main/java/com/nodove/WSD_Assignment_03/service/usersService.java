package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.jwtUtilities;
import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.configuration.utility.password.Base64PasswordEncoder;
import com.nodove.WSD_Assignment_03.constants.securityConstants;
import com.nodove.WSD_Assignment_03.domain.Role;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.users.UserLoginRequest;
import com.nodove.WSD_Assignment_03.dto.users.UserProfileRequest;
import com.nodove.WSD_Assignment_03.dto.users.UserRegisterRequest;
import com.nodove.WSD_Assignment_03.dto.users.emailRequest;
import com.nodove.WSD_Assignment_03.repository.usersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class usersService {

    private final redisService redisService;
    private final usersRepository usersRepository;
    private final jwtUtilities jwtUtilities;
    private final smtpService emailService;
    private final Base64PasswordEncoder passwordEncoder;

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
        if (redisService.isNicknameExists(nickname)) {
            log.info("Nickname already exists");
            return true;
        }
        return usersRepository.findByNickname(nickname).isPresent();
    }
    public boolean isUserIdExists(String userId) {
        if (redisService.isUserIdExists(userId)) {
            log.info("User ID already exists");
            return true;
        }
        return usersRepository.findByUserId(userId).isPresent();
    }
    public boolean isExistsEmail(String email) {
        if (redisService.isExistsEmail(email)) {
            log.info("Email already exists");
            return true;
        }
        return usersRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public ResponseEntity<?> refreshAcessToken(HttpServletRequest request, HttpServletResponse response){

        try {
            String refreshToken = jwtUtilities.getRefreshToken(request);

            if (jwtUtilities.isTokenExpired(refreshToken, 1)) {
                log.error("Refresh Token is invalid or expired");
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body(ApiResponseDto.builder()
                                .status("error")
                                .message("Refresh Token is invalid or expired")
                                .code("TOKEN_EXPIRED")
                                .build());
            }

            String userId = jwtUtilities.parseToken(refreshToken, 1).get("userId").toString();
            String newAccessToken = jwtUtilities.generateAccessToken(userId);

/*
            // Extract expiration times
            Date oldTokenExpiration = jwtUtilities.parseToken(refreshToken, 1).getExpiration();
            Date newTokenExpiration = jwtUtilities.parseToken(newAccessToken, 0).getExpiration();

*/

            log.info("Access Token reissued successfully for userId: {}", userId);
            response.setHeader(securityConstants.TOKEN_HEADER, securityConstants.TOKEN_PREFIX + newAccessToken);
            return ResponseEntity.ok()
                    .header(securityConstants.TOKEN_HEADER, securityConstants.TOKEN_PREFIX + newAccessToken)
                    .body(ApiResponseDto.builder()
                            .status("success")
                            .message("Access Token reissued successfully")
                            .code("TOKEN_REISSUED")
                            .build());
        } catch (Exception e) {
            log.error("Error refreshing Access Token: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.builder()
                            .status("error")
                            .message("Failed to reissue Access Token")
                            .code("TOKEN_REISSUE_FAILED")
                            .build());
        }
    }


    @Transactional
    public ResponseEntity<?> registerUser(UserRegisterRequest request) {
        log.info("회원가입 요청: {} , {}", request.getUserId(), request.getEmail());

        // 중복 사용자 체크
        if (isExistsEmail(request.getEmail())) {
            log.error("이미 존재하는 이메일: {}", request.getEmail());
            return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                    .status("error")
                    .message("이미 존재하는 이메일입니다.")
                    .code("EMAIL_EXISTS")
                    .build());
        }

        // 중복 사용자 체크
        if (isUserIdExists(request.getUserId())) {
            log.error("이미 존재하는 사용자 ID: {}", request.getUserId());
            return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                    .status("error")
                    .message("이미 존재하는 사용자 ID입니다.")
                    .code("USER_ID_EXISTS")
                    .build());
        }

        if (isNicknameExists(request.getNickname())) {
            log.error("이미 존재하는 닉네임: {}", request.getNickname());
            return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                    .status("error")
                    .message("이미 존재하는 닉네임입니다.")
                    .code("NICKNAME_EXISTS")
                    .build());
        }

        // 이메일 인증 코드 확인
        if (request.getEmailVerifyCode() == null) {
            log.error("이메일 인증 코드가 필요합니다.");
            return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                    .status("error")
                    .message("이메일 인증 코드가 필요합니다.")
                    .code("EMAIL_VERIFICATION_CODE_REQUIRED")
                    .build()
            );
        }

        // 이메일 인증 코드 확인
        String verificationCode = this.redisService.getVerificationCode(request.getEmail());
        if (verificationCode == null) {
            log.error("Redis에서 이메일 인증 코드를 찾을 수 없습니다: {}", request.getEmail());
            return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                    .status("error")
                    .message("이메일 인증 코드가 만료되었습니다. 다시 시도해주세요.")
                    .code("EMAIL_VERIFICATION_CODE_EXPIRED")
                    .build()
            );
        }

        if (!verificationCode.equals(request.getEmailVerifyCode())) {
            log.error("이메일 인증 코드가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                    .status("error")
                    .message("이메일 인증 코드가 일치하지 않습니다.")
                    .code("EMAIL_VERIFICATION_CODE_MISMATCH")
                    .build()
            );
        }


        // 비밀번호 암?호?화 (Base64)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성 및 저장
        users newUser = users.builder()
                .userId(request.getUserId())
                .password(encodedPassword)
                .email(request.getEmail())
                .username(request.getUsername())
                .nickname(request.getNickname())
                .role(Role.valueOf("USER"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .isBlocked(false)
                .build();

        usersRepository.save(newUser);

        log.info("회원가입 성공: {}", newUser.getUserId());
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("회원가입 성공")
                .code("REGISTER_SUCCESS")
                .build());
    }


    @Transactional
    public ResponseEntity<?> updateProfile(principalDetails principalDetails, UserProfileRequest request) {
        try {
            log.info("프로필 업데이트 요청: {}", principalDetails.getUserId());

            // 사용자 정보 조회
            users user = usersRepository.findByUserId(principalDetails.getUserId()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            if (request.getEmail() != null && request.getEmailVerificationCode() != null) {
                // email 중복 체크
                emailRequest emailRequest1 = new emailRequest();
                emailRequest1.setEmail(request.getEmail());
                if (this.emailService.checkEmailExists(emailRequest1)) {
                    return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                            .status("error")
                            .message("이미 존재하는 이메일입니다.")
                            .code("EMAIL_EXISTS")
                            .build());
                }

                // email 인증 코드 확인
                if (this.redisService.getVerificationCode(request.getEmail()).equals(request.getEmailVerificationCode())) {
                    redisService.deleteVerificationCode(request.getEmail());
                    user.setEmail(request.getEmail());
                } else {
                    return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                            .status("error")
                            .message("이메일 인증 코드가 일치하지 않습니다.")
                            .code("EMAIL_VERIFICATION_CODE_MISMATCH")
                            .build());
                }
            }


            // 닉네임 변경 처리
            if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
                if (isNicknameExists(request.getNickname())) {
                    return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                            .status("error")
                            .message("이미 존재하는 닉네임입니다.")
                            .code("NICKNAME_EXISTS")
                            .build());
                }
                user.setNickname(request.getNickname());
            }

            // 비밀번호 변경 처리
            if (request.getOriginPassword() != null && request.getNewPassword() != null) {
                // 기존 비밀번호 확인
                if (!passwordEncoder.matches(request.getOriginPassword(), user.getPassword())) {
                    return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                            .status("error")
                            .message("기존 비밀번호가 일치하지 않습니다.")
                            .code("PASSWORD_MISMATCH")
                            .build());
                }

                // 새 비밀번호가 기존 비밀번호와 동일하지 않은 경우에만 업데이트
                if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                    return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                            .status("error")
                            .message("새 비밀번호가 기존 비밀번호와 동일합니다.")
                            .code("PASSWORD_SAME")
                            .build());
                }

                // 비밀번호 업데이트
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }

            // 이름 변경 처리
            if (!user.getUsername().equals(request.getUsername())) {
                user.setUsername(request.getUsername());
            }

            // 업데이트된 엔터티 저장
            usersRepository.save(user);


            // 닉네임 변경 시 새로운 토큰 발급
            if (!principalDetails.getUser().getNickname().equals(request.getNickname())) {
                String newAccessToken = jwtUtilities.generateAccessToken(user.getUserId());
                return ResponseEntity.ok()
                        .header(securityConstants.TOKEN_HEADER, securityConstants.TOKEN_PREFIX + newAccessToken)
                        .body(ApiResponseDto.builder()
                                .status("success")
                                .message("프로필 업데이트 성공")
                                .code("PROFILE_UPDATED")
                                .build());
            }

            return ResponseEntity.ok(ApiResponseDto.builder()
                    .status("success")
                    .message("프로필 업데이트 성공")
                    .code("PROFILE_UPDATED")
                    .build());
        } catch (Exception e) {
            log.error("프로필 업데이트 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseDto.builder()
                    .status("error")
                    .message("프로필 업데이트에 실패했습니다.")
                    .code("PROFILE_UPDATE_FAILED")
                    .build());
        }
    }

    @Transactional
    public ResponseEntity<?> withdrawUser(principalDetails principalDetails) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }
        try {
            log.info("회원탈퇴 요청: {}", principalDetails.getUserId());

            users user = usersRepository.findByUserId(principalDetails.getUserId()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            if (redisService.isUserIdExists(user.getUserId())) {
                redisService.deleteUserIdExists(user.getUserId());
            }

            if (redisService.isExistsEmail(user.getEmail())) {
                redisService.deleteEmailExists(user.getEmail());
            }

            if (redisService.isNicknameExists(user.getNickname())) {
                redisService.deleteNicknameExists(user.getNickname());
            }

            // 사용자 삭제
            user.setDeleted(true);
            usersRepository.save(user);

            log.info("회원탈퇴 성공: {}", user.getUserId());
        } catch (Exception e) {
            log.error("회원탈퇴 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.builder()
                            .status("error")
                            .message("회원탈퇴에 실패했습니다.")
                            .code("WITHDRAW_FAILED")
                            .build());
        }
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("회원탈퇴 성공")
                .code("WITHDRAW_SUCCESS")
                .build());
    }

    public users getUser(String userId) {
        return usersRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public users getUserById(Long Id) {
        return usersRepository.findById(Id).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
