package com.nodove.WSD_Assignment_03.dto.Crawler.BookMark;

import lombok.Data;

@Data
public class BookmarkSearchRequestDto {

    private String keyword; // 키워드 검색 (회사명, 포지션)
    private String location; // 지역별 필터링
    private String experience; // 경력별 필터링
    private String salary; // 급여별 필터링
    private String techStack; // 기술 스택별 필터링
    private String sortBy = "latest"; // 정렬 기준: latest, salary, experience 등
    private int page = 0; // 페이지 번호
    private int size = 10; // 페이지 크기
}
