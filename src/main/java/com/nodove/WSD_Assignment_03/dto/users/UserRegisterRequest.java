package com.nodove.WSD_Assignment_03.dto.users;

import com.nodove.WSD_Assignment_03.configuration.utility.password.passwordValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRegisterRequest {
    private String userId;
    @passwordValid
    private String password;
    @Email @Valid
    private String email;
    private String username;
    private String nickname;

}
