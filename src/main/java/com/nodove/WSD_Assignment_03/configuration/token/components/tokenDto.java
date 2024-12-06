package com.nodove.WSD_Assignment_03.configuration.token.components;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class tokenDto {
    private String accessToken;
    private String refreshToken;
}
