package com.nodove.WSD_Assignment_03.configuration.utility.password;

import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class Base64PasswordEncoder {

    // 비밀번호를 Base64로 암호화
    public static String encode(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    // Base64로 암호화된 비밀번호를 원래 값과 비교
    public static boolean matches(String rawPassword, String encodedPassword) {
        String encodedRawPassword = encode(rawPassword);
        return encodedRawPassword.equals(encodedPassword);
    }

}
