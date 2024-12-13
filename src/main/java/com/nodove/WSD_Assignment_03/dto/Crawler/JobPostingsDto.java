package com.nodove.WSD_Assignment_03.dto.Crawler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobPostingsDto {
    private String title;
    private String companyName;
    private String location;
    private String deadline;
    private String experience;
    private String education;
    private String employmentType;
    private String salary;
    private String sector;
    private String link;
}
