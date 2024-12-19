package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.domain.SaramIn.*;
import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingsDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.CompanyRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingSectorRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.SectorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlerService {

    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final redisService redisService;
    private final SectorRepository sectorRepository; // Sector 저장소 추가
    private final JobPostingSectorRepository jobPostingSectorRepository; // 중간 테이블 저장소


    @Transactional
    public void saveJobPosting(JobPostingsDto jobPostingsDto) {

        log.info("Saving new Job Posting: {}", jobPostingsDto);

        // 채용 공고 정보 추출
        String companyName = jobPostingsDto.getCompanyName();
        if (companyName == null || companyName.isBlank()){
            log.error("Company name is missing for job posting: {}", jobPostingsDto.getCompanyName());
            companyName = jobPostingsDto.getTitle();
            log.info("Company name is set to title: {}", companyName);
        }


        String title = jobPostingsDto.getTitle();
        String location = jobPostingsDto.getLocation();
        String salary = jobPostingsDto.getSalary();
        String employmentType = jobPostingsDto.getEmploymentType();
        String link = jobPostingsDto.getLink();
        String logo = jobPostingsDto.getLogo();
        String experience = jobPostingsDto.getExperience();
        String education = jobPostingsDto.getEducation();
        String deadline = jobPostingsDto.getDeadline();
        List<String> sectors = List.of(jobPostingsDto.getSector().split(","));


        String finalCompanyName = companyName;
        // 회사 이름 캐싱 확인 및 저장
        Company company = companyRepository.findByName(companyName)
                .orElseGet(() -> {
                    Company newCompany = Company.builder()
                            .name(finalCompanyName)
                            .location(location)
                            .build();
                    return companyRepository.save(newCompany);
                });
            if (!redisService.isCompanyNameExists(companyName)) {
                redisService.saveCompanyName(companyName);
                log.info("Company name cached: {}", companyName);
         }


        // 채용 공고 중복 확인
        Optional<JobPosting> existingJobPostingOpt = jobPostingRepository.findByTitleAndCompany(title, company);

        JobPosting jobPosting;
        if (existingJobPostingOpt.isPresent()) {
            jobPosting = existingJobPostingOpt.get();
            log.info("Updating existing Job Posting: Title={}, Company={}", title, companyName);
            updateJobPosting(jobPosting, location, salary, deadline, employmentType, experience, education);
        } else {
            jobPosting = JobPosting.builder()
                    .title(title)
                    .company(company)
                    .location(location)
                    .salary(salary)
                    .deadline(deadline)
                    .link(link)
                    .logo(logo)
                    .experience(experience)
                    .education(education)
                    .employmentType(employmentType)
                    .viewCount(0)
                    .postedAt(LocalDateTime.now())
                    .build();
            jobPostingRepository.save(jobPosting);
            log.info("Saved new Job Posting: Title={}, Company={}", title, companyName);
        }
        saveSectors(jobPosting, sectors);
        // 섹터 저장 및 관계 설정
    }


    private void updateJobPosting(JobPosting jobPosting, String location, String salary, String deadline,
                                  String employmentType, String experience, String education) {
        boolean isUpdated = false;

        if (!Objects.equals(jobPosting.getLocation(), location)) {
            jobPosting.setLocation(location);
            isUpdated = true;
        }
        if (!Objects.equals(jobPosting.getSalary(), salary)) {
            jobPosting.setSalary(salary);
            isUpdated = true;
        }
        if (!Objects.equals(jobPosting.getDeadline(), deadline)) {
            jobPosting.setDeadline(deadline);
            isUpdated = true;
        }
        if (!Objects.equals(jobPosting.getEmploymentType(), employmentType)) {
            jobPosting.setEmploymentType(employmentType);
            isUpdated = true;
        }
        if (!Objects.equals(jobPosting.getExperience(), experience)) {
            jobPosting.setExperience(experience);
            isUpdated = true;
        }
        if (!Objects.equals(jobPosting.getEducation(), education)) {
            jobPosting.setEducation(education);
            isUpdated = true;
        }

        if (isUpdated) {
            jobPostingRepository.save(jobPosting);
        }
    }

    private void saveSectors(JobPosting jobPosting, List<String> sectors) {
        // 기존 관계 삭제
        jobPostingSectorRepository.deleteByJobPosting(jobPosting);

        log.info("trying to save sectors : " + sectors.toString());

        // 새로운 관계 저장
        for (String s : sectors) {
            Sector sector = sectorRepository.findByName(s)
                    .orElseGet(() -> {
                        log.info("Creating new sector: {}", s);
                        return sectorRepository.save(Sector.builder().name(s).build());
                    });

            JobPosting_Sector jobPostingSector = JobPosting_Sector.builder()
                    .id(new JobPosting_SectorId(jobPosting.getId(), sector.getId()))
                    .jobPosting(jobPosting)
                    .sector(sector)
                    .build();

            jobPostingSectorRepository.save(jobPostingSector);
            log.info("Linked Job Posting with Sector: Title={}, Sector={}", jobPosting.getTitle(), sector.getName());
        }
    }

}
