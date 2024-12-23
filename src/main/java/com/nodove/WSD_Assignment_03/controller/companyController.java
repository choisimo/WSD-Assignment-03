package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.Company;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.CompanyDto;
import com.nodove.WSD_Assignment_03.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class companyController {

    private final CompanyService companyService;


    @Operation(summary = "회사 등록", description = "새 회사를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회사 등록 성공"),
            @ApiResponse(responseCode = "400", description = "Unauthorized")
    })
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

    @Operation(summary = "회사 조회", description = "회사를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회사 조회 성공"),
            @ApiResponse(responseCode = "400", description = "Unauthorized")
    })
    @GetMapping("/protected/company/{id}")
    public ResponseEntity<?> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.<Company>builder()
                .status("success")
                .message("Company Retrieved")
                .code("COMPANY_RETRIEVED")
                .data(companyService.getCompany(id))
                .build());
    }

    @Operation(summary = "회사 수정", description = "회사를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회사 수정 성공"),
            @ApiResponse(responseCode = "400", description = "Unauthorized")
    })
    @PutMapping("/private/company/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDto) {
        return ResponseEntity.ok(ApiResponseDto.<Company>builder()
                .status("success")
                .message("Company Updated")
                .code("COMPANY_UPDATED")
                .data(companyService.updateCompany(id, companyDto))
                .build());
    }

    @Operation(summary = "회사 삭제", description = "회사를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회사 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "Unauthorized")
    })
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
