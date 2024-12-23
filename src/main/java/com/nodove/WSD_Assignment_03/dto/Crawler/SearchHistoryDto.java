package com.nodove.WSD_Assignment_03.dto.Crawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryDto {

    private Long id;
    private String userId;
    private String searchKeyword;
    private LocalDateTime searchDate;
}
