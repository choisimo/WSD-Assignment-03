package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.Company;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.CompanyDto;
import com.nodove.WSD_Assignment_03.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class companyController {

    private final CompanyService companyService;


    @PostMapping("/private/company")
    public ResponseEntity<?> createCompany(@RequestBody CompanyDto companyDto) {
        Company company = companyService.createCompany(companyDto);
        return ResponseEntity.ok(ApiResponseDto.<Company>builder()
                .status("success")
                .message("Company Created")
                .code("COMPANY_CREATED")
                .data(company)
                .build());
    }

    @GetMapping("/protected/company/{id}")
    public ResponseEntity<?> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.<Company>builder()
                .status("success")
                .message("Company Retrieved")
                .code("COMPANY_RETRIEVED")
                .data(companyService.getCompany(id))
                .build());
    }

    @PutMapping("/private/company/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDto) {
        return ResponseEntity.ok(ApiResponseDto.<Company>builder()
                .status("success")
                .message("Company Updated")
                .code("COMPANY_UPDATED")
                .data(companyService.updateCompany(id, companyDto))
                .build());
    }

    @DeleteMapping("/private/company/{id}")
    public ResponseEntity<?> deleteCompany (@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok().body(ApiResponseDto.<Void>builder()
                .status("success")
                .message("Company Deleted")
                .code("COMPANY_DELETED")
                .build());
    }

}
