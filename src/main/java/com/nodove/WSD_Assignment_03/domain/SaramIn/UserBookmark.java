package com.nodove.WSD_Assignment_03.domain.SaramIn;

import com.nodove.WSD_Assignment_03.domain.users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "`UserBookmark`")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 북마크한 사용자
    @JoinColumn(name = "user_id", nullable = false)
    private users user;

    @ManyToOne // 북마크한 공고
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Builder.Default
    @Column(name = "bookmarked_at", nullable = false)
    private LocalDate bookmarkedAt = LocalDate.now(); // 북마크한 날짜 (Optional)

    @Builder.Default
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 북마크 생성 시간

    @Builder.Default
    @Column(name = "note", nullable = true)
    private String note = null; // 사용자가 추가할 수 있는 메모 (Optional)
}
