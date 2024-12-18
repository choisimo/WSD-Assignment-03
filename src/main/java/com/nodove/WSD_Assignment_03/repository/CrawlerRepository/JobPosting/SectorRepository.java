package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {
    Optional<Sector> findByName(String name);
}
