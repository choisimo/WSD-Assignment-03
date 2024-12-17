package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.Application;
import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import com.nodove.WSD_Assignment_03.domain.SaramIn.StatusEnum;
import com.nodove.WSD_Assignment_03.dto.Crawler.ApplicationsDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.ApplicationRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPostingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationsService {

    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public ResponseEntity<?> getApplicationList(principalDetails principalDetails, StatusEnum status, String sortedBy, int pageSize, int pageNumber) {

        if (pageSize == 0 || pageNumber == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("pageSize or page is null");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "appliedAt");
        if (sortedBy != null && sortedBy.equals("asc")) {
            sort = Sort.by(Sort.Direction.ASC, "appliedAt");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // if user is admin, return all applications
        if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            Page<Application> applications;
            if (status != null) {
                applications = applicationRepository.findByStatus(status, pageable);
            } else {
                applications = applicationRepository.findAll(pageable);
            }
            return ResponseEntity.ok(applications);
        }
        // check if user exists
        if (principalDetails.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        // return applications for the user
        Page<Application> userApplications;
        if (status != null) {
            userApplications = applicationRepository.findByUserAndStatus(principalDetails.getUser(), status, pageable);
        } else {
            userApplications = applicationRepository.findByUser(principalDetails.getUser(), pageable);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userApplications);
    }

    @Transactional
    public ResponseEntity<?> setApplication(principalDetails principalDetails, ApplicationsDto requestDto) {
        // Check if user is blocked
        if (principalDetails.getUser().isBlocked())
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are blocked from applying to job postings");
        }

        // Check if user has already applied to this job posting
        if (applicationRepository.findByUserAndJobPostingId(principalDetails.getUser(), requestDto.getJobPostingId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already applied to this job posting");
        }

        JobPosting jobPosting = jobPostingRepository.findById(requestDto.getJobPostingId())
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));

        Application application = Application.builder()
                .jobPosting(jobPosting)
                .user(principalDetails.getUser())
                .appliedAt(LocalDateTime.now())
                .build();

        try {
            applicationRepository.save(application);
            return ResponseEntity.status(HttpStatus.CREATED).body("Application submitted successfully");
        } catch (Exception e) {
            log.error("Failed to submit application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit application");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteApplication(principalDetails principalDetails, long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_USER"))) {
            throw new IllegalArgumentException("You are not authorized to delete this application [at least USER permission required]");
        }
        if (!Objects.equals(application.getUser().getId(), principalDetails.getUser().getId())) {
            throw new IllegalArgumentException("You are not authorized to delete this application [USER MISMATCH]");
        }
        try {
            applicationRepository.delete(application);
            return ResponseEntity.status(HttpStatus.OK).body("Application deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete application");
        }
    }
}
