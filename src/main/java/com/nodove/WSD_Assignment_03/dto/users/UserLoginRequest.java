package com.nodove.WSD_Assignment_03.dto.users;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String userId;
    private String password;
}
