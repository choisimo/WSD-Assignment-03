package com.nodove.WSD_Assignment_03.dto.Crawler.BookMark;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BookmarkResponseDto {

    private Long id;                // 북마크 ID
    private Long jobPostingId;      // JobPosting ID
    private String title;           // 공고 제목
    private String companyName;     // 회사명
    private String location;        // 지역
    private String salary;          // 급여
    private String employmentType;  // 고용 형태
    private String experience;      // 경력
    private LocalDate bookmarkedAt; // 북마크한 날짜
    private List<String> sector;    // 분야   (Optional)
    private String note;            // 메모 (Optional)

}
