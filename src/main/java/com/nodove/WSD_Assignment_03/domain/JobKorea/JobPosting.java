package com.nodove.WSD_Assignment_03.domain.JobKorea;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Builder.Default
    @Column(name = "posted_at")
    private LocalDateTime postedAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;



}
