package com.nodove.WSD_Assignment_03.domain.SaramIn;

import com.nodove.WSD_Assignment_03.domain.users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "search_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private users userId;

    @Column(name = "keyword", nullable = false)
    private String searchKeyword;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime searchDate;
}
