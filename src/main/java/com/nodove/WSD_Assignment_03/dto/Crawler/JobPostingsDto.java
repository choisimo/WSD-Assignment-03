package com.nodove.WSD_Assignment_03.dto.Crawler;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JobPostingsDto {
    private long id;
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
    private String logo;
    private String postedAt;
    private int viewCount;
    @Builder.Default
    private List<JobPostingsDto> recommendedJobPostings = null;
}
