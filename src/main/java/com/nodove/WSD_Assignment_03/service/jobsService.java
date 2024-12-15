package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QJobPosting;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingsDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPostingRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class jobsService {

    private final JobPostingRepository jobPostingRepository;
    private final CrawlerService crawlerService;
    private final JPAQueryFactory queryFactory;

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
        jobPostingRepository.delete(jobPosting);
    }

    @Transactional
    public void updateJobPosting(principalDetails principalDetails, JobPostingsDto jobPostingsDto) {
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
            builder.and(qJobPosting.sector.containsIgnoreCase(sector));
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
                .sector(jobPosting.getSector())
                .deadline(jobPosting.getDeadline())
                .logo(jobPosting.getLogo())
                .postedAt(String.valueOf(jobPosting.getPostedAt()))
                .build();
    }

    @Transactional
    public JobPostingsDto getJobPosting(long id) {
        // 현재 공고 찾기 및 조회수 증가
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JobPosting not found"));
        jobPosting.setViewCount(jobPosting.getViewCount() + 1);
        jobPostingRepository.save(jobPosting);

        return JobPostingsDto.builder()
                .id(jobPosting.getId())
                .title(jobPosting.getTitle())
                .companyName(jobPosting.getCompany().getName())
                .location(jobPosting.getLocation())
                .employmentType(jobPosting.getEmploymentType())
                .postedAt(jobPosting.getPostedAt().toString())
                .viewCount(jobPosting.getViewCount())
                .build();
    }

    // 추천 공고를 DTO로 변환

}
