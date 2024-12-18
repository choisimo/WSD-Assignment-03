package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting;

import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting_Sector;
import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting_SectorId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobPostingSectorRepository extends JpaRepository<JobPosting_Sector, JobPosting_SectorId> {
    Optional<JobPosting_Sector> findBySector_Name(String sectorName);

    void deleteByJobPosting(JobPosting jobPosting);
}
