package com.nodove.WSD_Assignment_03.configuration.utility.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

class PasswordValidator implements ConstraintValidator<passwordValid, String> {
    // 정규식 패턴: 최소 8자, 하나 이상의 숫자와 하나의 특수문자 포함
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";

    private final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // null이면 유효하지 않음
        }
        return pattern.matcher(value).matches(); // 정규식을 통해 검증
    }

}
