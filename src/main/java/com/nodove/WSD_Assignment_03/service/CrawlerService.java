package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Company;
import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingsDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.CompanyRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPostingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlerService {

    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final redisService redisService;

    @Transactional
    public void saveJobPosting(JobPostingsDto jobPostingsDto) {

        log.info("Saving new Job Posting: {}", jobPostingsDto);

        String companyName = jobPostingsDto.getCompanyName();
        String title = jobPostingsDto.getTitle();
        String location = jobPostingsDto.getLocation();
        String Salary = jobPostingsDto.getSalary();
        String employmentType = jobPostingsDto.getEmploymentType();


        // 회사 이름 캐싱 확인 및 저장
        Company company = companyRepository.findByName(companyName)
                .orElseGet(() -> {
                    if (!redisService.isCompanyNameExists(companyName)) {
                        redisService.saveCompanyName(companyName);
                        log.info("Company name cached: {}", companyName);
                    }
                    Company newCompany = Company.builder()
                            .name(companyName)
                            .location(location)
                            .build();
                    return companyRepository.save(newCompany);
                });

        // 채용 공고 중복 확인 및 저장
        if (jobPostingRepository.findByTitleAndCompany(title, company).isPresent()) {
            log.info("Duplicate Job Posting: Title={}, Company={}", title, company.getName());
        } else {
            JobPosting jobPosting = JobPosting.builder()
                    .title(title)
                    .company(company)
                    .location(location)
                    .salary(Salary)
                    .employmentType(employmentType)
                    .viewCount(0)
                    .description("No description provided") // 추가 정보
                    .postedAt(LocalDateTime.now())
                    .build();

            jobPostingRepository.save(jobPosting);
            log.info("Saved new Job Posting: Title={}, Company={}", title, company.getName());
        }
    }

}
