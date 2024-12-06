package com.nodove.WSD_Assignment_03.configuration.utility.password;

import com.nodove.WSD_Assignment_03.configuration.utility.password.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class) // 실제 검증 로직을 실행할 클래스
public @interface passwordValid {
    String message() default "비밀번호가 유효하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
