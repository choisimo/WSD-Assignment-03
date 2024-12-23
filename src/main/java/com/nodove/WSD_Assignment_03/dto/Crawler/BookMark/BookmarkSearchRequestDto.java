package com.nodove.WSD_Assignment_03.dto.Crawler.BookMark;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookmarkSearchRequestDto {
    private String note;
    private int pageNumber;
    private int pageSize;
}
