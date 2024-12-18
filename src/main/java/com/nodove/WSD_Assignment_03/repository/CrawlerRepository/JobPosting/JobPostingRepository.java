package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Company;
import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.function.Supplier;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Optional<JobPosting> findByTitleAndCompany(String title, Company company);

    Optional<JobPosting> findByTitleAndCompanyName(String title, String company);


    Supplier<EntityManager> getEntityManager();
}
