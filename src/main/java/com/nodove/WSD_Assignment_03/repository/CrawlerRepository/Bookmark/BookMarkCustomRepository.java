package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.Bookmark;

import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;

import java.util.List;

public interface BookMarkCustomRepository {
    List<UserBookmark> searchUserBookmarks(Long userId, BookmarkSearchRequestDto requestDto);
}
