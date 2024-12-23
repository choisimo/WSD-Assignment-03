package com.nodove.WSD_Assignment_03.repository.CrawlerRepository;

import com.nodove.WSD_Assignment_03.domain.SaramIn.SearchHistory;
import com.nodove.WSD_Assignment_03.domain.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUserId(users userId);

    void deleteByUserId(users user);

    void deleteByUserIdAndId(users user, Long id);
}
