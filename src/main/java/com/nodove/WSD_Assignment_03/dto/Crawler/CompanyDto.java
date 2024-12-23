package com.nodove.WSD_Assignment_03.dto.Crawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDto {
    private Long id;
    private String name;
    private String location;
    private String industry;
    private String description;
}
