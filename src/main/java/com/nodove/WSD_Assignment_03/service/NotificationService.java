package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Notification;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.Crawler.NotificationDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final usersService usersService;
    private final CompanyService companyService;
    private final bookMarkService bookMarkService;
    private final redisService redisService;
    private final jobsService jobsService;

    @Transactional
    public Object createNotification(NotificationDto notificationDto) {
        Notification notification = Notification.builder()
                .sender(usersService.getUser(notificationDto.getSenderId()))
                .receiver(usersService.getUser(notificationDto.getReceiverId()))
                .jobPostingId(jobsService.getJobPostingById(notificationDto.getJobPostingId()))
                .isRead(false)
                .type(notificationDto.getType())
                .message(notificationDto.getMessage())
                .createdDate(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        return notificationDto;
    }

    @Transactional
    public List<NotificationDto> getAllNotifications(String userId, int page, int size, String sort) {
        users user = usersService.getUser(userId);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return notificationRepository.findByReceiverAndIsRead(user, pageable, false)
                .stream()
                .map(notification -> NotificationDto.builder().id(notification.getId())
                        .senderId(notification.getSender().getUserId())
                        .receiverId(notification.getReceiver().getUserId())
                        .jobPostingId(notification.getJobPostingId().getId())
                        .type(notification.getType())
                        .message(notification.getMessage())
                        .isRead(notification.getIsRead())
                        .createdDate(notification.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found."));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(String userId, Long notificationId) {
        users user = usersService.getUser(userId);
        if (!notificationRepository.findById(notificationId).get().getReceiver().equals(user)) {
            throw new IllegalArgumentException("Notification not found.");
        }
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public Object getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(notification -> NotificationDto.builder().id(notification.getId())
                        .senderId(notification.getSender().getUserId())
                        .receiverId(notification.getReceiver().getUserId())
                        .jobPostingId(notification.getJobPostingId().getId())
                        .type(notification.getType())
                        .message(notification.getMessage())
                        .isRead(notification.getIsRead())
                        .createdDate(notification.getCreatedDate())
                        .build())
                .orElseThrow(() -> new IllegalArgumentException("Notification not found."));
    }
}
