package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.Role;
import com.nodove.WSD_Assignment_03.domain.SaramIn.Application;
import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import com.nodove.WSD_Assignment_03.domain.SaramIn.StatusEnum;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.ApplicationsDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.ApplicationRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingRepository;
import com.nodove.WSD_Assignment_03.repository.usersRepository;
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

import static com.nodove.WSD_Assignment_03.domain.SaramIn.QApplication.application;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationsService {

    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final usersRepository usersRepository;
    private final usersService usersService;

    @Transactional
    public ResponseEntity<?> getApplicationList(principalDetails principalDetails, StatusEnum status, String sortedBy, int pageSize, int pageNumber) {

        if (pageSize <= 0 || pageNumber < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseDto.builder()
                    .status("error")
                    .message("Invalid page size or number")
                    .code("INVALID_PAGE_SIZE_OR_NUMBER")
                    .build());
        }

        if (principalDetails == null || principalDetails.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("Unauthorized")
                    .code("UNAUTHORIZED")
                    .build());
        }

        users user = usersRepository.findByUserId(principalDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        Sort sort = Sort.by(Sort.Direction.DESC, "appliedAt");
        if (sortedBy != null && sortedBy.equals("asc")) {
            sort = Sort.by(Sort.Direction.ASC, "appliedAt");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // 관리자 권한 처리
        boolean isAdmin = principalDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));


        // if user is admin, return all applications
        if (isAdmin) {
            Page<Application> applications;
            if (status != null) {
                applications = applicationRepository.findByStatus(status, pageable);
            } else {
                applications = applicationRepository.findAll(pageable);
            }
            return ResponseEntity.ok().body(ApiResponseDto.<Page<Application>>builder()
                    .status("success")
                    .message("Applications retrieved successfully")
                    .code("APPLICATIONS_RETRIEVED")
                    .data(applications)
                    .build()
            );
        }

        // return applications for the user
        Page<Application> userApplications;
        if (status != null) {
            userApplications = applicationRepository.findByUserAndStatus(user, status, pageable);
        } else {
            userApplications = applicationRepository.findByUser(user, pageable);
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDto.<Page<Application>>builder()
                .status("success")
                .message("Applications retrieved successfully")
                .code("APPLICATIONS_RETRIEVED")
                .data(userApplications)
                .build());
    }

    @Transactional
    public ResponseEntity<?> setApplication(principalDetails principalDetails, ApplicationsDto requestDto) {

        // 영속화된 User 객체 가져오기
        users user = usersRepository.findByUserId(principalDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        // Check if user is blocked
        if (user.isBlocked()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("User is blocked")
                    .code("USER_BLOCKED")
                    .build());
        }

        // Check if user has already applied to this job posting
        if (applicationRepository.findByUserAndJobPostingId(user, requestDto.getJobPostingId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("User has already applied to this job posting")
                    .code("USER_ALREADY_APPLIED")
                    .build());
        }

        JobPosting jobPosting = jobPostingRepository.findById(requestDto.getJobPostingId())
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));

        Application application = Application.builder()
                .jobPosting(jobPosting)
                .user(user)
                .note(requestDto.getNote())
                .build();

        try {
            applicationRepository.save(application);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.<Void>builder()
                    .status("success")
                    .message("Application submitted successfully")
                    .code("APPLICATION_SUBMITTED")
                    .build());
        } catch (Exception e) {
            log.error("Failed to submit application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("Failed to submit application")
                    .code("APPLICATION_FAILED")
                    .build());
        }
    }

    @Transactional
    public ResponseEntity<?> deleteApplication(principalDetails principalDetails, long id) {

        users user = usersRepository.findByUserId(principalDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        log.info("user authority : {}", principalDetails.getAuthorities().toString());
        boolean hasAccess = principalDetails.getAuthorities().stream()
                .anyMatch(a -> "USER".equals(a.getAuthority()) || "ADMIN".equals(a.getAuthority()));

        if (!hasAccess) {
            log.warn("Unauthorized access attempt by user: {}", principalDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("Unauthorized: Insufficient permissions")
                    .code("UNAUTHORIZED")
                    .build());
        }

        // Check if the application belongs to the user
        if (!(Objects.equals(application.getUser().getId(), user.getId()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("지원 사용자와 현재 사용자가 일치하지 않습니다.")
                    .code("UNAUTHORIZED")
                    .build());
        }

        try {
            applicationRepository.delete(application);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDto.<Void>builder()
                    .status("success")
                    .message("Application deleted successfully")
                    .code("APPLICATION_DELETED")
                    .build());
        } catch (Exception e) {
            log.error("Failed to delete application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("Failed to delete application")
                    .code("APPLICATION_DELETE_FAILED")
                    .build());
        }
    }
}
