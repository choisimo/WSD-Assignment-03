package com.nodove.WSD_Assignment_03.dto.Crawler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobPostingRequestDto {
    private int page;
    private int size;
    private String location;
    private String experience;
    private String salary;
    private String companyName;
    private String employmentType;
    private String sector;
    private String deadline;
    private String keyword;
    private String sortBy;
    private String order;
}
