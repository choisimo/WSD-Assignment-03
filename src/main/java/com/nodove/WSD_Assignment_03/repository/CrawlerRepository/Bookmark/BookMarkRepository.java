package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.Bookmark;


import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

// default JpaRepository interface for UserBookmark
public interface BookMarkRepository extends JpaRepository<UserBookmark, Long>, BookMarkCustomRepository{

    Optional<UserBookmark> findByUserAndJobPostingId(users user, Long jobPostingId);
    List<UserBookmark> findByUser(users user);

}
