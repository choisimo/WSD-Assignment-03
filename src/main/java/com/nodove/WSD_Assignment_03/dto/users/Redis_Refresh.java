package com.nodove.WSD_Assignment_03.dto.users;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Redis_Refresh {
    private String userId;
    private String provider;
    private String deviceId;
}
