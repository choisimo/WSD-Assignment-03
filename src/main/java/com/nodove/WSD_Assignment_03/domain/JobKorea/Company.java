package com.nodove.WSD_Assignment_03.domain.JobKorea;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

import static jakarta.persistence.GenerationType.*;

@Entity
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;        // 회사 이름
    private String industry;    // 산업 분야
    private String website;     // 회사 웹사이트
    private String description; // 회사 설명

    @OneToMany(mappedBy = "company")
    private List<JobPosting> jobPostings; // 해당 회사의 채용 공고

}
