package com.nodove.WSD_Assignment_03.dto.users;

import com.nodove.WSD_Assignment_03.configuration.utility.password.passwordValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @NotBlank
    private String userId;
    @passwordValid
    private String password;
    @Email @Valid
    private String email;
    @NotNull
    private String emailVerifyCode;
    private String username;
    private String nickname;

}
