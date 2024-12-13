package com.nodove.WSD_Assignment_03.domain.JobKorea;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;          // 채용 공고 제목
    private String description;    // 공고 설명
    private String location;       // 근무지
    private String employmentType; // 고용 형태 (정규직, 계약직 등)
    private LocalDate deadline;    // 지원 마감일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;       // 연관된 회사 정보

    @OneToMany(mappedBy = "jobPosting")
    private List<Application> applications; // 지원 내역


}
