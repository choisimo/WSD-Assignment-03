package com.nodove.WSD_Assignment_03.domain.SaramIn;

import com.nodove.WSD_Assignment_03.domain.users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private String note; // 사용자가 추가할 수 있는 메모 (Optional)

    @Builder.Default
    // 북마크한 날짜 (Optional)
    private LocalDate bookmarkedAt = LocalDate.now(); // 북마크한 날짜 (Optional)

}
