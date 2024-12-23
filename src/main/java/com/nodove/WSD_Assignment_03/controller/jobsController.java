package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.Crawler.customSearchingCrawler;
import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingRequestDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingUpdateDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingsDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.crawlingData;
import com.nodove.WSD_Assignment_03.service.jobsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class jobsController {

    private final jobsService jobService;
    private final customSearchingCrawler customSearchingCrawler;

    @Operation(summary = "채용 공고 등록", description = "Creates a new job posting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Posting Created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Unauthorized", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<?> createJobPosting(@AuthenticationPrincipal principalDetails principalDetails, @RequestBody JobPostingsDto jobPostingsDto) {
        if (principalDetails == null) {
            return ResponseEntity.badRequest().body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("Unauthorized")
                    .code("UNAUTHORIZED")
                    .build());
        }
        jobService.createJobPosting(jobPostingsDto);
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .status("success")
                .message("Job Posting Created")
                .code("JOB_POSTING_CREATED")
                .build());
    }


    
    @Operation(summary = "공고 목록 조회", description = "Retrieves a list of job postings with optional filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Postings Retrieved", content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<?> getJobPostings(
            @Parameter(description = "페이지 번호 (기본값: 0)", example = "1")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기 (기본값: 20)", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "지역 필터", example = "서울")
            @RequestParam(required = false) String location,

            @Parameter(description = "경력 필터", example = "신입")
            @RequestParam(required = false) String experience,

            @Parameter(description = "급여 필터", example = "3000만원 이상")
            @RequestParam(required = false) String salary,

            @Parameter(description = "회사명 필터", example = "삼성전자")
            @RequestParam(required = false) String companyName,

            @Parameter(description = "고용 유형 필터", example = "정규직")
            @RequestParam(required = false) String employmentType,

            @Parameter(description = "섹터 필터", example = "IT/소프트웨어")
            @RequestParam(required = false) String sector,

            @Parameter(description = "마감일 필터", example = "2024-12-31")
            @RequestParam(required = false) String deadline,

            @Parameter(description = "검색 키워드 (제목 또는 회사명에 포함된 텍스트)", example = "백엔드 개발자")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "정렬 기준 (기본값: postedAt)", example = "viewCount")
            @RequestParam(defaultValue = "postedAt") String sortBy,

            @Parameter(description = "정렬 순서 (asc 또는 desc, 기본값: desc)", example = "asc")
            @RequestParam(defaultValue = "desc") String order

    ) {
        JobPostingRequestDto jobPostingRequestDto = JobPostingRequestDto.builder()
                .page(page)
                .size(size)
                .location(location)
                .experience(experience)
                .salary(salary)
                .companyName(companyName)
                .employmentType(employmentType)
                .sector(sector)
                .deadline(deadline)
                .keyword(keyword)
                .sortBy(sortBy)
                .order(order)
                .build();


        Page<JobPostingsDto> jobListings = jobService.getJobListings(jobPostingRequestDto);
        return ResponseEntity.ok(ApiResponseDto.<Page<JobPostingsDto>>builder()
                .status("success")
                .message("Job Postings Retrieved")
                .data(jobListings)
                .build());
    }

    @Operation(summary = "채용 공고 상세 조회", description = "상세 정보 제공, 조회수 증가, 관련 공고 추천")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Posting Retrieved", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Job Posting not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Job Posting not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobPosting(@AuthenticationPrincipal principalDetails principalDetails, @PathVariable long id, HttpServletRequest request) {
        try {
            JobPostingsDto jobPostDetail = jobService.getJobPosting(principalDetails, id, request);
            return ResponseEntity.ok(ApiResponseDto.<JobPostingsDto>builder()
                    .status("success")
                    .message("Job Posting Retrieved")
                    .data(jobPostDetail)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("Job Posting not found")
                    .code("JOB_POSTING_NOT_FOUND")
                    .build());
        }
    }

    @Operation(summary = "공고 수정하기", description = "Updates the details of a specific job posting by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Posting Updated", content = @Content(mediaType = "application/json")),
            @ApiResponse (responseCode = "400", description = "Unauthorized", content = @Content(mediaType = "application/json")),
            @ApiResponse (responseCode = "404", description = "Job Posting not found", content = @Content(mediaType = "application/json"))
    })
        @PutMapping
        public ResponseEntity<?> updateJobPosting(@AuthenticationPrincipal principalDetails principalDetails, @RequestBody @Valid JobPostingUpdateDto jobPostingsUpdateDto) {
            return this.jobService.updateJobPosting(principalDetails, jobPostingsUpdateDto);
        }

    @Operation(summary = "공고 삭제하기", description = "Deletes a specific job posting by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Posting Deleted", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Unauthorized", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Job Posting not found", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobPosting(@AuthenticationPrincipal principalDetails principalDetails, @PathVariable long id) {
        return this.jobService.deleteJobPosting(principalDetails, id);
    }

    @Operation(summary = "job Crawler", description = "Crawls job postings from a specific website.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Postings Retrieved", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/CustomJobCrawling")
    public ResponseEntity<?> CustomJobCrawling(@RequestBody(required = true) crawlingData requestData) {
        if (requestData == null) {
            return ResponseEntity.badRequest().body("Request Data is null");
        }
        customSearchingCrawler.customSearchingCrawling(requestData.getKeywords(), requestData.getTotalPage());
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .status("success")
                .message("Job Postings Retrieved")
                .code("JOB_POSTINGS_RETRIEVED")
                .build());
    }
}