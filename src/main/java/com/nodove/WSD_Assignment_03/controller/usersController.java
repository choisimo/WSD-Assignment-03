package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.users.UserLoginRequest;
import com.nodove.WSD_Assignment_03.dto.users.UserProfileRequest;
import com.nodove.WSD_Assignment_03.dto.users.UserRegisterRequest;
import com.nodove.WSD_Assignment_03.service.usersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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


    @Operation(
            summary = "프로필 업데이트",
            description = "사용자의 프로필 정보를 업데이트합니다. 요청 본문에 업데이트할 정보를 포함하여 요청을 보냅니다." +
                    "이메일 변경 시에는 인증 코드 요청 후 인증 코드를 포함해서 요청해야 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 업데이트 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 | 중복 체크 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류로 프로필 업데이트 실패", content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/auth/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal principalDetails principalDetails,
                                           @RequestBody @Valid UserProfileRequest request) {
        return this.usersService.updateProfile(principalDetails, request);
    }


    @Operation(summary = "로그인", description = "ID와 비밀번호를 통해 로그인을 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @PostMapping("/auth/login")
    public void login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        log.info("Swagger UI를 통한 로그인 요청");
        // 로그인은 AuthenticationFilter에서 자동 처리됨. 응답은 필터에 의해 전송됩니다.
    }

}
