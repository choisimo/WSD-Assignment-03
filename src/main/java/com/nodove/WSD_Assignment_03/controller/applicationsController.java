package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.StatusEnum;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.ApplicationsDto;
import com.nodove.WSD_Assignment_03.service.ApplicationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class applicationsController {


    private final ApplicationsService applicationsService;

    // 지원 내역 조회
    @Operation(summary = "지원 내역 조회", description = "사용자의 지원 내역을 조회합니다.", responses = {
        @ApiResponse(responseCode = "200", description = "지원 내역 조회 성공"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "400", description = "pageSize or page is null")
    })
    @GetMapping
    public ResponseEntity<?> getApplcationList(@AuthenticationPrincipal principalDetails principalDetails,
                                                                                   @Parameter(description = "한 페이지에 표시할 지원 내역 수", example = "20")
                                               @RequestParam("pageSize") @DefaultValue("20") int pageSize,
                                                                                   @Parameter(description = "페이지 번호", example = "1")
                                               @RequestParam("pageNumber") @DefaultValue("1") int pageNumber,
                                                                                   @Parameter(description = "지원 상태 [PENDING, REVIEWING, INTERVIEW, OFFERED, REJECTED, WITHDRAWN]", example = "APPROVED")
                                               @RequestParam(value = "status", required = false) StatusEnum status,
                                                                                   @Parameter(description = "정렬 순서 (asc or desc)", example = "desc") @RequestParam(defaultValue = "desc") String sortedBy) {
    if (principalDetails == null) {
            log.error("there is no principalDetails");
            return ResponseEntity.status(401).body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("unauthorized")
                            .message("토큰 정보 없음")
                            .build()
            );        }

        if (pageSize <= 0 || pageNumber < 0) {
            log.error("pageSize or page is null");
            return ResponseEntity.status(400).body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("bad_request")
                            .message("pageSize or page is null")
                            .build()
            );
        }

        if (status == null || sortedBy == null || sortedBy.isEmpty()) {
            log.error("status or sortedBy is null");
            return ResponseEntity.status(400).body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("invalid_sort_param")
                            .message("sortedBy must not be null or empty")
                            .build()
            );
        }

        return this.applicationsService.getApplicationList(principalDetails, status, sortedBy, pageSize, pageNumber);
    }



    @Operation(summary = "지원 취소", description = "특정 지원 내역을 삭제합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "지원 내역 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "ApplicationId is null"),
            @ApiResponse(responseCode = "500", description = "Failed to delete application")
    })
    @DeleteMapping
    public ResponseEntity<?> deleteApplication(@AuthenticationPrincipal principalDetails principalDetails, @RequestParam("ApplicationId") long ApplicationId) {
        if (principalDetails == null) {
            log.error("principalDetails is null");
            return ResponseEntity.status(401).body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("unauthorized")
                            .message("Unauthorized")
                            .build()
            );
        }

        if (ApplicationId <= 0){
            log.error("ApplicationId is null");
            return ResponseEntity.badRequest().body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("invalid_application_id")
                            .message("ApplicationId must be greater than 0")
                            .build()
            );
        }

        return this.applicationsService.deleteApplication(principalDetails, ApplicationId);
    }



    @Operation(summary = "지원하기", description = "새로운 지원 내역을 등록합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "Application submitted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "requestDto is null"),
            @ApiResponse(responseCode = "500", description = "Failed to submit application")
    })
    @PostMapping
    public ResponseEntity<?> setApplication(@AuthenticationPrincipal principalDetails principalDetails,
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "지원 정보", required = true,
                                            content = @Content(schema = @Schema(implementation = ApplicationsDto.class)))
                                            @RequestBody ApplicationsDto requestDto) {
        if (principalDetails == null) {
            log.error("principalDetails is null");
            return ResponseEntity.status(401).body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("unauthorized")
                            .message("Unauthorized")
                            .build()
            );
        }

        if (requestDto == null) {
            log.error("requestDto is null");
            return ResponseEntity.badRequest().body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("invalid_request_dto")
                            .message("requestDto must not be null")
                            .build()
            );
        }

        return this.applicationsService.setApplication(principalDetails, requestDto);
    }

}
