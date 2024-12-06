package com.nodove.WSD_Assignment_03.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class emailRequest {
    @Email
    @NotBlank
    private String email;
}