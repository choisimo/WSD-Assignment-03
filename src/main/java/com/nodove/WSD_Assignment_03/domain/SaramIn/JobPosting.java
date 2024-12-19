package com.nodove.WSD_Assignment_03.domain.SaramIn;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "`JobPosting`")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String salary;

    @Column(length = 50)
    private String employmentType;

    @Column(length = 50)
    private String experience; // 경력

    @Column(length = 100)
    private String education; // 학력 요구 사항

    @Column(length = 500)
    private String link; // 공고 링크

    @Column(length = 500)
    private String logo; // 회사 로고 URL

    @Column(length = 100)
    private String deadline; // 마감일

    @Builder.Default
    @Column(name = "posted_at")
    private LocalDateTime postedAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;


}
