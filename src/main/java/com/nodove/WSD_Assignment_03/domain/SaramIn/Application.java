package com.nodove.WSD_Assignment_03.domain.SaramIn;

import com.nodove.WSD_Assignment_03.domain.users;
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

    @Builder.Default
    @JoinColumn(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now(); // 지원 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private users user; // 지원한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_posting_id", nullable = false)
    private JobPosting jobPosting; // 지원한 공고

    @Builder.Default
    @Column(name = "status", length = 50, nullable = false)
    private StatusEnum status = StatusEnum.PENDING; // 지원 상태 (Optional)

    @Column(name = "note", length = 500, nullable = true)
    private String note; // 사용자가 추가할 수 있는 메모 (Optional)

}
