package com.nodove.WSD_Assignment_03.domain.SaramIn;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name ="`company`")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "location" ,length = 200)
    private String location;

    @Builder.Default
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "industry", length = 100, nullable = true)
    private String industry;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobPosting> jobPostings;
}
