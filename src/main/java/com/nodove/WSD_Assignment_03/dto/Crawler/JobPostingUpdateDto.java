package com.nodove.WSD_Assignment_03.dto.Crawler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 반환하지 않음
public class JobPostingUpdateDto {
    private long id;
    private String title;
    private String companyName;
    private String location;
    private String deadline;
    private String experience;
    private String education;
    private String employmentType;
    private String salary;
    private List<String> sector;
    private String link;
    private String logo;
}
