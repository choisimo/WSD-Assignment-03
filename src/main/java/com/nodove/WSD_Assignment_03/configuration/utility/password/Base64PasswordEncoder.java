package com.nodove.WSD_Assignment_03.configuration.utility.password;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;

@Configuration
public class Base64PasswordEncoder {

    public static String encode(CharSequence rawPassword) {
        return Base64.getEncoder().encodeToString(rawPassword.toString().getBytes());
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encodedRawPassword = Base64.getEncoder().encodeToString(rawPassword.toString().getBytes());
        return encodedRawPassword.equals(encodedPassword);
    }

}
