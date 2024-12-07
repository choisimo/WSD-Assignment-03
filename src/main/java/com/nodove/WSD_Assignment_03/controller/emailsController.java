package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.dto.users.emailRequest;
import com.nodove.WSD_Assignment_03.service.redisService;
import com.nodove.WSD_Assignment_03.service.smtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name="email 관련", description = "email")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class emailsController {

    private final smtpService emailService;

    @Operation(summary="회원가입 이메일 보내기", description = "이메일 코드 전송 관련")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "이메일 전송 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버오류", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/public/sendJoinEmail")
    public ResponseEntity<?> sendEmail(@RequestBody @Valid emailRequest request) {
        try {
            String result = emailService.sendJoinMail(request);
            if (result == null) {
                return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("이메일 전송 실패", e);
            return ResponseEntity.badRequest().body("이메일 전송 실패");
        }
    }
}
