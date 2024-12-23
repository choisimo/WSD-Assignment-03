package com.nodove.WSD_Assignment_03.domain.SaramIn;

import com.nodove.WSD_Assignment_03.domain.users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_sender_id", nullable = false)
    private users sender;

    @ManyToOne
    @JoinColumn(name = "user_receiver_id", nullable = true)
    private users receiver;

    @ManyToOne
    @JoinColumn(name = "job_posting_id", nullable = true)
    private JobPosting jobPostingId;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = true)
    private Comment commentId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "type", nullable = false)
    private String type; // E.g., ApplicationStatus, Reminder

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;
}
