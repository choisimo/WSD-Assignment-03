package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.*;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingUpdateDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingsDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.CompanyRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingSectorRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.SectorRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.userViewPageRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    @Transactional
    public void createJobPosting(JobPostingsDto jobPostingsDto) {
        if (jobPostingsDto == null) {
            log.error("JobPostingDto is null");
            return;
        }
        this.crawlerService.saveJobPosting(jobPostingsDto);
    }


    @Transactional
    public void deleteJobPosting(principalDetails principalDetails, long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobPosting not found"));
        if (!principalDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new IllegalArgumentException("You are not authorized to delete this job posting [ADMIN ONLY]");
        }

        jobPostingSectorRepository.deleteByJobPosting(jobPosting);
        jobPostingRepository.delete(jobPosting);
    }

    @Transactional
    public ResponseEntity<?> updateJobPosting(principalDetails principalDetails, JobPostingUpdateDto jobPostingsUpdateDto) {
        if (principalDetails == null) {
            log.error("there is no principalDetails");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (principalDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.error("You are not authorized to update this job posting [ADMIN ONLY]");
            return ResponseEntity.status(401).body("Unauthorized");
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
        updateSectors(jobPosting, jobPostingsUpdateDto.getSector());
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
        return ResponseEntity.status(200).body(updatedJobPosting + " updated successfully");
    }


    @Transactional
    public Page<JobPostingsDto> getJobListings(
            int page,
            int size,
            String location,
            String experience,
            String salary,
            String companyName,
            String employmentType,
            String sector,
            String deadline,
            String sortBy,
            String order
    ) {
        QJobPosting qJobPosting = QJobPosting.jobPosting;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();
        if (location != null) {
            builder.and(qJobPosting.location.eq(location));
        }
        if (experience != null) {
            builder.and(qJobPosting.experience.eq(experience));
        }
        if (salary != null) {
            builder.and(qJobPosting.salary.eq(salary));
        }
        if (companyName != null) {
            builder.and(qJobPosting.company.name.eq(companyName));
        }
        if (employmentType != null) {
            builder.and(qJobPosting.employmentType.eq(employmentType));
        }
        if (sector != null) {
/*
            builder.and(qJobPosting.sectors.any().name.containsIgnoreCase(sector)); // 섹터 조건 수정
*/
        }
        if (deadline != null) {
            builder.and(qJobPosting.deadline.eq(deadline));
        }
        if (companyName != null) {
            builder.and(qJobPosting.company.name.eq(companyName));
        }

        Pageable pageable = PageRequest.of(page, size);

        List<JobPosting> results = queryFactory.selectFrom(qJobPosting)
                .where(builder)
                .orderBy(order.equalsIgnoreCase(sortBy != null ? sortBy : "desc") ?
                        qJobPosting.postedAt.desc() :
                        qJobPosting.postedAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(qJobPosting)
                .where(builder)
                .fetchCount();

        // JobPosting → JobListingDto 매핑
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
                .sector(String.valueOf(jobPosting.getSectorNames()))
                .deadline(jobPosting.getDeadline())
                .logo(jobPosting.getLogo())
                .postedAt(String.valueOf(jobPosting.getPostedAt()))
                .build();
    }

    @Transactional
    public JobPostingsDto getJobPosting(principalDetails principalDetails, long id, HttpRequest request) {
        // 현재 공고 찾기 및 조회수 증가
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobPosting not found"));
        jobPosting.setViewCount(jobPosting.getViewCount() + 1);
        jobPostingRepository.save(jobPosting);

        userViewPage updateUserViewPage = userViewPage.builder()
                .viewedAt(LocalDateTime.now())
                .user(principalDetails.getUser())
                .pageUrl(request.getURI().toString())
                .userAgent(Objects.requireNonNull(request.getHeaders().get("User-Agent")).toString())
                .ipAddress(Objects.requireNonNull(request.getHeaders().get("X-Forwarded-For")).toString())
                .build();

        // 사용자 조회 페이지 저장
        this.userViewPageRepository.save(updateUserViewPage);

        return JobPostingsDto.builder()
                .id(jobPosting.getId())
                .title(jobPosting.getTitle())
                .companyName(jobPosting.getCompany().getName())
                .location(jobPosting.getLocation())
                .employmentType(jobPosting.getEmploymentType())
                .postedAt(jobPosting.getPostedAt().toString())
                .viewCount(jobPosting.getViewCount())
                .recommendedJobPostings(getRecommendedJobPostings())
                .build();
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


    private void updateSectors(JobPosting jobPosting, List<String> sectorNames) {
        // 기존 섹터 관계 삭제
        jobPostingSectorRepository.deleteByJobPosting(jobPosting);

        // 새로운 섹터 저장 및 관계 추가
        for (String sectorName : sectorNames) {
            Sector sector = sectorRepository.findByName(sectorName)
                    .orElseGet(() -> sectorRepository.save(Sector.builder().name(sectorName).build()));

            JobPosting_Sector jobPostingSector = JobPosting_Sector.builder()
                    .jobPosting(jobPosting)
                    .sector(sector)
                    .build();

            jobPostingSectorRepository.save(jobPostingSector);
            log.info("Linked Job Posting with Sector: Title={}, Sector={}", jobPosting.getTitle(), sectorName);
        }
    }

}
