package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting;

import com.nodove.WSD_Assignment_03.domain.SaramIn.userViewPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface userViewPageRepository extends JpaRepository<userViewPage, Long> {
    List<userViewPage> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
