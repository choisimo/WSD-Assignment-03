package com.nodove.WSD_Assignment_03.repository.CrawlerRepository;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Application;
import com.nodove.WSD_Assignment_03.domain.SaramIn.StatusEnum;
import com.nodove.WSD_Assignment_03.domain.users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findByUser(users user, Pageable pageable);
    Optional<Application> findByUserAndJobPostingId(users user, Long jobPostingId);

    Page<Application> findByStatus(StatusEnum status, Pageable pageable);

    Page<Application> findByUserAndStatus(users user, StatusEnum status, Pageable pageable);
}
