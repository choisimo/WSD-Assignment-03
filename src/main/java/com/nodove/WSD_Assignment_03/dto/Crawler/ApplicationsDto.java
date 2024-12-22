package com.nodove.WSD_Assignment_03.dto.Crawler;

import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import com.nodove.WSD_Assignment_03.domain.SaramIn.StatusEnum;
import com.nodove.WSD_Assignment_03.domain.users;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationsDto {

    @NotNull
    private Long jobPostingId; // 지원한 채용공고의 ID

    // 기본값 변경 안되도록 설정
    @NotNull @Builder.Default
    private final StatusEnum status = StatusEnum.PENDING;

    private String note; // 지원 메모

}
