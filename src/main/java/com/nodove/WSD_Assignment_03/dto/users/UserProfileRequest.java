package com.nodove.WSD_Assignment_03.dto.users;

import com.nodove.WSD_Assignment_03.configuration.utility.password.passwordValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {

    @Size(max = 30, message = "Nickname must be less than 30 characters") @NotNull
    private String nickname;

    @Size(max = 100, message = "Email must be less than 100 characters")
    @Email(message = "Email should be valid") @NotNull
    private String email;

    @Size(max = 30, message = "Username must be less than 30 characters")
    @NotNull
    private String username;

    private String originPassword;

    @passwordValid // Custom annotation for password validation
    private String newPassword;

    private String emailVerificationCode;

}
