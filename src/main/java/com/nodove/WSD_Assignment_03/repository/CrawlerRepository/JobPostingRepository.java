package com.nodove.WSD_Assignment_03.repository.CrawlerRepository;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Company;
import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Optional<JobPosting> findByTitleAndCompany(String title, Company company);

    Optional<JobPosting> findByTitleAndCompanyName(String title, String company);


}
