package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.constants.redisConstants;
import com.nodove.WSD_Assignment_03.dto.users.Redis_Refresh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class redisService {
    @Value("${email.expire}")
    private long emailExpire;

    @Value("${REFRESH_TOKEN_VALIDITY}")
    private long refreshTokenExpire;

    private final RedisTemplate<String, String> redisTemplate;

    /* start redis-refresh */

    private String refreshTokenKeyGenerator(Redis_Refresh redis_refresh) {

        String provider = redis_refresh.getProvider() != null ? redis_refresh.getProvider() : "UNKNOWN_PROVIDER";
        String userId = redis_refresh.getUserId() != null ? redis_refresh.getUserId() : "UNKNOWN_USER";
        String deviceId = redis_refresh.getDeviceId() != null ? redis_refresh.getDeviceId() : "UNKNOWN_DEVICE";

        return new StringBuilder(provider)
                .append("_REFRESH_")
                .append(userId)
                .append("_")
                .append(deviceId)
                .toString();
    }

    public boolean saveRefreshToken(Redis_Refresh redis_refresh, String refreshToken) {
        try {
            String key = this.refreshTokenKeyGenerator(redis_refresh);
            redisTemplate.opsForValue().set(key, refreshToken, refreshTokenExpire, TimeUnit.MILLISECONDS);
            log.info("Refresh token saved for userId={}, deviceId={}", redis_refresh.getUserId(), redis_refresh.getDeviceId());
            return true;
        } catch (Exception e) {
            log.error("Failed to save refresh token for userId={}, deviceId={}", redis_refresh.getUserId(), redis_refresh.getDeviceId());
            return false;
        }
    }

    public String getRefreshToken(Redis_Refresh redis_refresh) {
        return redisTemplate.opsForValue().get(this.refreshTokenKeyGenerator(redis_refresh));
    }

    public boolean deleteRefreshToken(Redis_Refresh redis_refresh) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(this.refreshTokenKeyGenerator(redis_refresh)));
        } catch (Exception e) {
            log.error("Failed to delete refresh token for userId={}, deviceId={}", redis_refresh.getProvider(), redis_refresh.getDeviceId());
            return false;
        }
    }
    /* end redis-refresh */

    /* start redis-email */

    // 인증번호 저장
    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(redisConstants.VERIFICATION_CODE_KEY_PREFIX + email, code, Duration.ofMinutes(10)); // 인증번호 TTL 설정
    }
    // 인증번호 가져오기
    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(redisConstants.VERIFICATION_CODE_KEY_PREFIX + email);
    }
    // 인증번호 삭제
    public Boolean deleteVerificationCode(String email) {
        return redisTemplate.delete(redisConstants.VERIFICATION_CODE_KEY_PREFIX + email);
    }
    //  이메일 중복 체크
    public boolean checkEmailExists(String email) {
        return redisTemplate.opsForValue().get(redisConstants.EMAIL_KEY_PREFIX + email) != null;
    }
    // 이메일 저장
    public void saveEmail(String email) {
        redisTemplate.opsForValue().set(redisConstants.EMAIL_KEY_PREFIX + email, "true", Duration.ofDays(1)); // 캐시 TTL 설정
    }

    /* end redis-email */

    /* start redis-user */

    // blackList 저장
    public void saveBlackList(String token) {
        redisTemplate.opsForValue().set(redisConstants.BLACKLIST_KEY_PREFIX + token, "true", Duration.ofDays(1)); // 캐시 TTL 설정
    }
    // blackList 삭제
    public Boolean deleteBlackList(String token) {
        return redisTemplate.delete(redisConstants.BLACKLIST_KEY_PREFIX + token);
    }
    // blackList 확인
    public boolean checkBlackList(String token) {
        return redisTemplate.opsForValue().get(redisConstants.BLACKLIST_KEY_PREFIX + token) != null;
    }
    /* end redis-user*/
}
