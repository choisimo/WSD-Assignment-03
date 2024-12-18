package com.nodove.WSD_Assignment_03.domain.SaramIn;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class JobPosting_Sector {

    @EmbeddedId
    private JobPosting_SectorId id;

    @ManyToOne
    @MapsId("jobPostingId")
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne
    @MapsId("sectorId")
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;
}