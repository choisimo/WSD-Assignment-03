package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Company;
import com.nodove.WSD_Assignment_03.dto.Crawler.CompanyDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public Company createCompany(CompanyDto companyDto) {
        Company company = Company.builder()
                .name(companyDto.getName())
                .location(companyDto.getLocation())
                .build();
        return companyRepository.save(company);
    }

    @Transactional
    public Company getCompany(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보를 찾을 수 없습니다. ID: " + id));
    }

    @Transactional
    public Company updateCompany(Long id, CompanyDto companyDto) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보를 찾을 수 없습니다. ID: " + id));

        existingCompany.setName(companyDto.getName());
        existingCompany.setLocation(companyDto.getLocation());

        return companyRepository.save(existingCompany);
    }

    @Transactional
    public void deleteCompany(Long id) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회사 정보를 찾을 수 없습니다. ID: " + id));
        companyRepository.delete(existingCompany);
    }

    @Transactional
    public List<Company> searchCompanyByName(String name) {
        return companyRepository.findByNameContaining(name);
    }


    @Transactional
    public List<Company> findCompaniesByLocation(String location) {
        return companyRepository.findByLocation(location);
    }

    @Transactional
    public List<Company> getAllCompanies(int page, int size, String sort) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return companyRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

}
