package com.nodove.WSD_Assignment_03.repository.CrawlerRepository;

import com.nodove.WSD_Assignment_03.domain.JobKorea.Company;
import com.nodove.WSD_Assignment_03.domain.JobKorea.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Optional<JobPosting> findByTitleAndCompany(String title, Company company);

    Optional<JobPosting> findByTitleAndCompanyName(String title, String company);
}
