package com.nodove.WSD_Assignment_03.domain.SaramIn;

import jakarta.persistence.Embeddable;
import lombok.Data;


import java.io.Serializable;

@Data
@Embeddable
public class JobPosting_SectorId implements Serializable {

    private Long jobPostingId;
    private Long sectorId;

    public JobPosting_SectorId(){}

    public JobPosting_SectorId(Long jobPostingId, Long sectorId) {
        this.jobPostingId = jobPostingId;
        this.sectorId = sectorId;
    }
}
