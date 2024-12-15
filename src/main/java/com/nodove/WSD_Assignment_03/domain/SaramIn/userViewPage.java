package com.nodove.WSD_Assignment_03.domain.SaramIn;

import com.nodove.WSD_Assignment_03.domain.users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_view_page")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class userViewPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private users user; // 사용자와 연결 (Foreign Key)

    @Column(name = "page_url", nullable = false, length = 255)
    private String pageUrl; // 조회한 페이지의 URL

    @Builder.Default
    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt = LocalDateTime.now(); // 페이지 조회 시간

    @Column(name = "ip_address", length = 45)
    private String ipAddress; // 사용자의 IP 주소 (IPv6 지원)

    @Column(name = "user_agent", length = 255)
    private String userAgent; // 사용자의 브라우저/환경 정보
}