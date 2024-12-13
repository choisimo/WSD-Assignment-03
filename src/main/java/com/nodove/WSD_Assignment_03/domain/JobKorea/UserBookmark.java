package com.nodove.WSD_Assignment_03.domain.JobKorea;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class UserBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private JobUser user; // 연관된 사용자

    @ManyToOne
    @JoinColumn(name = "job_posting_id")
    private JobPosting jobPosting; // 연관된 채용 공고

    private String note; // 사용자가 추가할 수 있는 메모 (Optional)

    // 북마크한 날짜 (Optional)
    private LocalDate bookmarkedAt = LocalDate.now(); // 북마크한 날짜 (Optional)

}
