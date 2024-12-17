package com.nodove.WSD_Assignment_03.repository.CrawlerRepository;


import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<UserBookmark, Long>{

    Optional<UserBookmark> findByUserAndJobPostingId(users user, Long jobPostingId);
    Object findByUser(users user);

    List<UserBookmark> searchUserBookmarks(Long userId, BookmarkSearchRequestDto requestDto);
}
