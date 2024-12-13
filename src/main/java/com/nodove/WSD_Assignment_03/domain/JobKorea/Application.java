package com.nodove.WSD_Assignment_03.domain.JobKorea;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appliedAt; // 지원 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private JobUser user; // 지원한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_posting_id")
    private JobPosting jobPosting; // 지원한 공고


}
