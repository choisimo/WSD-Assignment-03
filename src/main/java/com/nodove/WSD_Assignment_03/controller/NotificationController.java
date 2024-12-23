package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.NotificationDto;
import com.nodove.WSD_Assignment_03.service.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/protected/notification")
    public ResponseEntity<?> getNotification(
            @AuthenticationPrincipal principalDetails principalDetails,
            @Parameter(description = "페이지 번호")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 사이즈")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "정렬 방식")
            @RequestParam(value = "sort", defaultValue = "desc") String sort) {
        if (page < 0) {
            page = 0;
        }
        if (size < 1) {
            size = 10;
        }
        if (!sort.equals("asc") && !sort.equals("desc")) {
            sort = "desc";
        }
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Notification Retrieved")
                .code("NOTIFICATION_RETRIEVED")
                .data(notificationService.getAllNotifications(principalDetails.getUserId(), page, size, sort))
                .build());
    }

    @GetMapping("/protected/notification/{id}")
    public ResponseEntity<?> getNotificationById(
            @Parameter(description = "알림 ID")
            @RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Notification Retrieved")
                .code("NOTIFICATION_RETRIEVED")
                .data(notificationService.getNotificationById(id))
                .build());
    }


    @PostMapping("/protected/notification")
    public ResponseEntity<?> createNotification(@AuthenticationPrincipal principalDetails principalDetails, NotificationDto notificationDto) {
        notificationDto.setSenderId(principalDetails.getUserId());
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Notification Created")
                .code("NOTIFICATION_CREATED")
                .data(notificationService.createNotification(notificationDto))
                .build());
    }

    @DeleteMapping("/protected/notification/{id}")
    public ResponseEntity<?> deleteNotification(
            @AuthenticationPrincipal principalDetails principalDetails,
            @Parameter(description = "알림 ID")
            @RequestParam(value = "id") Long id) {
        notificationService.deleteNotification(principalDetails.getUserId(), id);
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Notification Deleted")
                .code("NOTIFICATION_DELETED")
                .build());
    }

/*    @PutMapping("/protected/notification/{id}")
    public ResponseEntity<?> updateNotification(
            @Parameter(description = "알림 ID")
            @RequestParam(value = "id") Long id,
            @Parameter(description = "알림 제목")
            @RequestParam(value = "title") String title,
            @Parameter(description = "알림 내용")
            @RequestParam(value = "content") String content) {
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Notification Updated")
                .code("NOTIFICATION_UPDATED")
                .data(notificationService.updateNotification(id, title, content))
                .build());
    }*/
}
