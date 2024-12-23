package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.Role;
import com.nodove.WSD_Assignment_03.domain.SaramIn.*;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingRequestDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingUpdateDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingsDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.SearchHistoryDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.CompanyRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingSectorRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.SectorRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.userViewPageRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class jobsService {

    private final JobPostingRepository jobPostingRepository;
    private final CrawlerService crawlerService;
    private final JPAQueryFactory queryFactory;
    private final CompanyRepository companyRepository;
    private final userViewPageRepository userViewPageRepository;
    private final JobPostingSectorRepository jobPostingSectorRepository;
    private final SectorRepository sectorRepository;
    private final com.nodove.WSD_Assignment_03.repository.usersRepository usersRepository;
    private final SearchHistoryService searchHistoryService;

    @Transactional
    public void createJobPosting(JobPostingsDto jobPostingsDto) {
        if (jobPostingsDto == null) {
            log.error("JobPostingDto is null");
            return;
        }
        this.crawlerService.saveJobPosting(jobPostingsDto);
    }


    public ResponseEntity<?> deleteJobPosting(principalDetails principalDetails, long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobPosting not found"));

        if (!principalDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.builder()
                    .status("error")
                    .code("UNAUTHORIZED")
                    .message("You are not authorized to delete this job posting [ADMIN ONLY]")
                    .build());
        }

        jobPostingSectorRepository.deleteByJobPosting(jobPosting);
        jobPostingRepository.delete(jobPosting);
        return ResponseEntity.ok().body(ApiResponseDto.builder()
                .status("success").code("JOB_POSTING_DELETED").message("Job Posting Deleted").build());
    }

    @Transactional
    public ResponseEntity<?> updateJobPosting(principalDetails principalDetails, JobPostingUpdateDto jobPostingsUpdateDto) {
        if (principalDetails == null) {
            log.error("there is no principalDetails");
            return ResponseEntity.status(401).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("Unauthorized")
                    .code("UNAUTHORIZED")
                    .build());
        }

        if (principalDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().matches(String.valueOf(Role.ADMIN.getKey())))) {
            log.error("You are not authorized to update this job posting [ADMIN ONLY]");
            principalDetails.getAuthorities().forEach(authority -> log.info("Authority: {}", authority.getAuthority()));
            String requestRole = principalDetails.getAuthorities().toString();
            return ResponseEntity.status(401).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("관리자만 수정 가능합니다. 현재 권한: " + requestRole)
                    .code("UNAUTHORIZED")
                    .build());
        }

        // 현재 공고 등록 되어있는지 확인하기 위해 id로 조회
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingsUpdateDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고 내역입니다."));

        if (!jobPostingsUpdateDto.getCompanyName().equals(jobPosting.getCompany().getName())) {
            log.info("회사 이름이 변경되었습니다.");
            companyRepository.findByName(jobPostingsUpdateDto.getCompanyName())
                    .ifPresentOrElse(
                            jobPosting::setCompany,
                            () -> {
                                log.info("회사 정보가 존재하지 않습니다.");
                                return;
                            }
                    );
        }

        // 섹터 업데이트
        updateSectors(jobPosting, jobPostingsUpdateDto.getSector() != null
                ? jobPostingsUpdateDto.getSector()
                : jobPostingSectorRepository.findJobPosting_SectorsByJobPosting(jobPosting)
                .stream()
                .map(sector -> sector.getSector().getName() // sector 이름만 추출
                ).collect(Collectors.toList())
        );

        // 업데이트
        JobPosting updatedJobPosting = JobPosting.builder()
                .id(jobPostingsUpdateDto.getId())
                .title(jobPostingsUpdateDto.getTitle())
                .company(jobPosting.getCompany())
                .location(jobPostingsUpdateDto.getLocation())
                .experience(jobPostingsUpdateDto.getExperience())
                .education(jobPostingsUpdateDto.getEducation())
                .employmentType(jobPostingsUpdateDto.getEmploymentType())
                .salary(jobPostingsUpdateDto.getSalary())
                .deadline(jobPostingsUpdateDto.getDeadline())
                .logo(jobPostingsUpdateDto.getLogo())
                .build();

        jobPostingRepository.save(updatedJobPosting);
        // 변경 내역 body에 담아 반환 (기존 내역과 비교해서 변경된 내역들만)
        return ResponseEntity.status(200).body(ApiResponseDto.<JobPostingsDto>builder()
                .status("success")
                .message("Job Posting Updated")
                .code("JOB_POSTING_UPDATED")
                .data(convertToDto(updatedJobPosting))
                .build());
    }


    @Transactional
    public Page<JobPostingsDto> getJobListings(JobPostingRequestDto requestDto) {
        QJobPosting qJobPosting = QJobPosting.jobPosting;
        QJobPosting_Sector qJobPostingSector = QJobPosting_Sector.jobPosting_Sector;
        QSector qSector = QSector.sector;

        BooleanBuilder builder = new BooleanBuilder();

        if (requestDto.getLocation() != null)
            builder.and(qJobPosting.location.containsIgnoreCase(requestDto.getLocation()));
        if (requestDto.getExperience() != null)
            builder.and(qJobPosting.experience.containsIgnoreCase(requestDto.getExperience()));
        if (requestDto.getSalary() != null)
            builder.and(qJobPosting.salary.containsIgnoreCase(requestDto.getSalary()));
        if (requestDto.getCompanyName() != null)
            builder.and(qJobPosting.company.name.containsIgnoreCase(requestDto.getCompanyName()));
        if (requestDto.getEmploymentType() != null)
            builder.and(qJobPosting.employmentType.containsIgnoreCase(requestDto.getEmploymentType()));
        // 섹터 조건 추가
        if (requestDto.getSector() != null) {
            builder.and(qJobPosting.id.in(
                    JPAExpressions.select(qJobPostingSector.jobPosting.id)
                            .from(qJobPostingSector)
                            .join(qJobPostingSector.sector, qSector)
                            .where(qSector.name.containsIgnoreCase(requestDto.getSector()))
            ));
        }

        if (requestDto.getDeadline() != null)
            builder.and(qJobPosting.deadline.eq(requestDto.getDeadline()));

        // 통합 키워드 검색
        if (requestDto.getKeyword() != null) {
            builder.and(
                    qJobPosting.title.containsIgnoreCase(requestDto.getKeyword())
                            .or(qJobPosting.company.name.containsIgnoreCase(requestDto.getKeyword()))
                            .or(qJobPosting.location.containsIgnoreCase(requestDto.getKeyword()))
                            .or(qJobPosting.description.containsIgnoreCase(requestDto.getKeyword()))
                            .or(qJobPosting.employmentType.containsIgnoreCase(requestDto.getKeyword()))
                            .or(qJobPosting.experience.containsIgnoreCase(requestDto.getKeyword()))
                            .or(qJobPosting.education.containsIgnoreCase(requestDto.getKeyword()))
                            .or(qJobPosting.salary.containsIgnoreCase(requestDto.getKeyword())
                            .or(qJobPosting.deadline.containsIgnoreCase(requestDto.getKeyword()))
            ));
        }



        // 페이징 및 정렬
        int page = requestDto.getPage();
        int size = requestDto.getSize();
        String order = requestDto.getOrder();
        String sortBy = requestDto.getSortBy();

        Pageable pageable = PageRequest.of(page, size);

        // 데이터 조회
        List<JobPosting> results = queryFactory.selectFrom(qJobPosting)
                .where(builder)
                .orderBy(sortBy != null && sortBy.equals("desc") ?
                        qJobPosting.postedAt.desc() :
                        qJobPosting.postedAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회
        long total = queryFactory.selectFrom(qJobPosting)
                .where(builder)
                .fetchCount();

        // DTO 변환
        List<JobPostingsDto> dtoResults = results.stream()
                .map(this::convertToDto)
                .toList();

        return new PageImpl<>(dtoResults, pageable, total);

    }


    private JobPostingsDto convertToDto(JobPosting jobPosting) {
        return JobPostingsDto.builder()
                .id(jobPosting.getId())
                .title(jobPosting.getTitle())
                .companyName(jobPosting.getCompany().getName())
                .location(jobPosting.getLocation())
                .experience(jobPosting.getExperience())
                .education(jobPosting.getEducation())
                .employmentType(jobPosting.getEmploymentType())
                .salary(jobPosting.getSalary())
                .deadline(jobPosting.getDeadline())
                .logo(jobPosting.getLogo())
                .postedAt(String.valueOf(jobPosting.getPostedAt()))
                .build();
    }

    @Transactional
    public JobPostingsDto getJobPosting(principalDetails principalDetails, long id, HttpServletRequest request) {

        // 인증된 사용자 확인
        if (principalDetails == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        // 현재 공고 찾기 및 조회수 증가
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobPosting not found"));
        jobPosting.setViewCount(jobPosting.getViewCount() + 1);
        jobPostingRepository.save(jobPosting);

        // 헤더 값 검증
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "Unknown";
        }

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr(); // 기본 IP 설정
        }

        users user = usersRepository.findByUserId(principalDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 사용자 페이지 조회 기록 생성
        userViewPage updateUserViewPage = userViewPage.builder()
                .viewedAt(LocalDateTime.now())
                .user(user)
                .pageUrl(request.getRequestURI())
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .build();

        userViewPageRepository.save(updateUserViewPage);

        searchHistoryService.saveSearchHistory( SearchHistoryDto.builder()
                .userId(user.getUserId())
                .searchDate(LocalDateTime.now())
                .searchKeyword(jobPosting.getTitle())
                .build()
       );
        // JobPosting → DTO 변환
        return JobPostingsDto.builder()
                .id(jobPosting.getId())
                .title(jobPosting.getTitle())
                .companyName(jobPosting.getCompany().getName())
                .location(jobPosting.getLocation())
                .experience(jobPosting.getExperience())
                .education(jobPosting.getEducation())
                .employmentType(jobPosting.getEmploymentType())
                .salary(jobPosting.getSalary())
                .deadline(jobPosting.getDeadline())
                .logo(jobPosting.getLogo())
                .viewCount(jobPosting.getViewCount())
                .postedAt(String.valueOf(jobPosting.getPostedAt()))
                .recommendedJobPostings(getRecommendedJobPostings())
                .build();
    }


    @Transactional
    public JobPosting getJobPostingById(long id) {
        return jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobPosting not found"));
    }
    // 추천 공고 불러오기
    @Transactional
    public List<JobPostingsDto> getRecommendedJobPostings() {
        QJobPosting qJobPosting = QJobPosting.jobPosting;
        List<JobPosting> results = queryFactory.selectFrom(qJobPosting)
                .orderBy(qJobPosting.viewCount.desc())
                .limit(5)
                .fetch();

        return results.stream()
                .map(this::convertToDto)
                .toList();
    }

    // todo : 섹터 업데이트 request Long, String 2개 만들어서 변경 없을 시 DB 호출 안하게 하기
    private void updateSectors(JobPosting jobPosting, List<String> sectorNames) {
        if (sectorNames == null || sectorNames.isEmpty()) {
            log.info("There's no change in sectors");
        } else {
        // 기존 섹터 관계 삭제
        jobPostingSectorRepository.deleteByJobPosting(jobPosting);

        // 새로운 섹터 저장 및 관계 추가
        for (String sectorName : sectorNames) {
            Sector sector = sectorRepository.findByName(sectorName)
                    .orElseGet(() -> sectorRepository.save(Sector.builder().name(sectorName).build()));


            JobPosting_SectorId id = new JobPosting_SectorId(jobPosting.getId(), sector.getId());
            JobPosting_Sector jobPostingSector = JobPosting_Sector.builder()
                    .id(id)
                    .jobPosting(jobPosting)
                    .sector(sector)
                    .build();

            jobPostingSectorRepository.save(jobPostingSector);
            log.info("Linked Job Posting with Sector: Title={}, Sector={}", jobPosting.getTitle(), sectorName);
        }
    }
}

}
