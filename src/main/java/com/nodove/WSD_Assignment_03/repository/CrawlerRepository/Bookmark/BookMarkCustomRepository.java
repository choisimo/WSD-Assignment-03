package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.Bookmark;

import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

// This interface is used to search user bookmarks.
public interface BookMarkCustomRepository {
    List<UserBookmark> searchUserBookmarks(Long userId, BookmarkSearchRequestDto requestDto);

    List<UserBookmark> searchUserBookmarksWithPagingAndSortingUserByUserOrderByDescInOrderToBookmarkedDate(Pageable pageable);
}
