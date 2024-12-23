package com.nodove.WSD_Assignment_03.domain;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "login_history")
public class userLoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private users user;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name="ip_address", nullable = true)
    private String ipAddress;

    @Column(name="user_agent", nullable = true)
    private String userAgent;
}
