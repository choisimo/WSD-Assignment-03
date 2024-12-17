package com.nodove.WSD_Assignment_03.dto.Crawler;

import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import com.nodove.WSD_Assignment_03.domain.SaramIn.StatusEnum;
import com.nodove.WSD_Assignment_03.domain.users;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationsDto {

    @NotNull
    private Long jobPostingId; // 지원한 채용공고의 ID

    @NotNull
    private StatusEnum status; // 지원 상태
}
