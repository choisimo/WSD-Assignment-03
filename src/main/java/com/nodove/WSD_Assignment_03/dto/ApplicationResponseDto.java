package com.nodove.WSD_Assignment_03.dto;

import com.nodove.WSD_Assignment_03.domain.SaramIn.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationResponseDto {

    private Long id;
    private String note;
    private LocalDateTime appliedAt;
    private String userName;
    private String jobTitle;
    private StatusEnum status;

}
