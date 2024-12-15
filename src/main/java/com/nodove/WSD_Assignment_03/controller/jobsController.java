package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingsDto;
import com.nodove.WSD_Assignment_03.service.jobsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class jobsController {

    private final jobsService jobService;

    @Operation(summary = "Create Job Posting", description = "Creates a new job posting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Posting Created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Unauthorized", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<?> createJobPosting(@AuthenticationPrincipal principalDetails principalDetails, @RequestBody JobPostingsDto jobPostingsDto) {
        if (principalDetails == null) {
            return ResponseEntity.badRequest().body("Unauthorized");
        }
        jobService.createJobPosting(jobPostingsDto);
        return ResponseEntity.ok("Job Posting Created");
    }

    @Operation(summary = "Get Job Postings", description = "Retrieves a list of job postings with optional filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Postings Retrieved", content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<?> getJobPostings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String salary,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String deadline,
            @RequestParam(defaultValue = "postedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order
    ) {
        Page<JobPostingsDto> jobListings = jobService.getJobListings(
                page, size, location, experience, salary, companyName, employmentType, sector, deadline, sortBy, order
        );
        return ResponseEntity.ok(jobListings);
    }

    @Operation(summary = "Get Job Posting", description = "Retrieves the details of a specific job posting by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Posting Retrieved", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobPosting(@PathVariable long id) {
        JobPostingsDto jobPostDetail = jobService.getJobPosting(id);
        return ResponseEntity.ok(jobPostDetail);
    }
}