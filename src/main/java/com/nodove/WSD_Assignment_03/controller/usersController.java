package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.users.UserRegisterRequest;
import com.nodove.WSD_Assignment_03.service.usersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
@RequiredArgsConstructor
public class usersController {

    private final usersService usersService;

    @Operation(
            summary = "Access Token 갱신",
            description = "만료된 Access Token을 Refresh Token을 이용하여 갱신합니다. Refresh Token은 HTTP-Only 쿠키에 저장되어 있어야 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access Token 갱신 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않거나 만료됨", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 Access Token 갱신 실패", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal principalDetails principalDetails) {
        if (principalDetails == null) {
            return ResponseEntity.status(401).body("Refresh Token이 유효하지 않거나 만료되었습니다.");
        }
        return this.usersService.refreshAcessToken(request, response);
    }

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다. 요청 본문에 필요한 정보를 포함하여 요청을 보냅니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류로 회원가입 실패", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(
            @RequestBody(required = true) UserRegisterRequest request) {
        return this.usersService.registerUser(request);
    }
}
