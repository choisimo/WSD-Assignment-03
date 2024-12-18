package com.nodove.WSD_Assignment_03.dto.Crawler.BookMark;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookmarkSearchRequestDto {

    private String keyword; // 키워드 검색 (회사명, 포지션)
    private String location; // 지역별 필터링
    private String experience; // 경력별 필터링
    private String salary; // 급여별 필터링
    private String sector; // 기술 스택별 필터링 (as techStack)
    private String sortedBy = "latest"; // 정렬 기준: latest, salary, experience 등
    private String note; // 북마크 메모  (Optional)
    private int pageNumber = 1; // 페이지 번호
    private int pageSize = 10; // 페이지 크기
}
