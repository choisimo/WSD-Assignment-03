package com.nodove.WSD_Assignment_03.domain.JobKorea;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "`Application`")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now(); // 지원 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private JobUser user; // 지원한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_posting_id", nullable = false)
    private JobPosting jobPosting; // 지원한 공고


}
