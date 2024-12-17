package com.nodove.WSD_Assignment_03.dto.Crawler.BookMark;

import lombok.Builder;
import lombok.Data;

// BookMarkDto : 북마크를 추가 / 제거할 때 사용할 DTO 클래스
@Data
@Builder
public class BookmarkDto {
    private Long jobPostingId;  // 즐겨찾기한 채용공고의 ID
    private String note;        // 즐겨찾기에 대한 메모
}
