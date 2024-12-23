package com.nodove.WSD_Assignment_03.dto.Crawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;
    private String senderId; // 알림 발신자 ID
    private String receiverId; // 알림 수신자 ID (optional)
    private Long jobPostingId; // 연관된 채용 공고 ID (optional)
    private Long commentId; // 연관된 댓글 ID (optional)
    private String message; // 알림 메시지
    private String type; // 알림 유형, e.g., ApplicationStatus, Reminder
    private LocalDateTime createdDate; // 생성 시간
    private Boolean isRead; // 읽음 여부

}
