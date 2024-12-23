package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping("/protected/search-history")
    public ResponseEntity<?> getSearchHistory(@AuthenticationPrincipal principalDetails principalDetails) {
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Search History Retrieved")
                .code("SEARCH_HISTORY_RETRIEVED")
                .data(searchHistoryService.getSearchHistory(principalDetails.getUserId()))
                .build());
    }

    @GetMapping("/protected/search-history/{id}")
    public ResponseEntity<?> getSearchHistoryById(@AuthenticationPrincipal principalDetails principalDetails, Long id) {
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Search History Retrieved")
                .code("SEARCH_HISTORY_RETRIEVED")
                .data(searchHistoryService.getSearchHistoryById(principalDetails.getUserId(), id))
                .build());
    }

    @DeleteMapping("/protected/search-history")
    public ResponseEntity<?> deleteSearchHistory(@AuthenticationPrincipal principalDetails principalDetails) {
        searchHistoryService.deleteSearchHistory(principalDetails.getUserId());
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Search History Deleted")
                .code("SEARCH_HISTORY_DELETED")
                .build());
    }

    @DeleteMapping("/protected/search-history/{id}")
    public ResponseEntity<?> deleteSearchHistoryById(@AuthenticationPrincipal principalDetails principalDetails, Long id) {
        searchHistoryService.deleteSearchHistoryById(principalDetails.getUserId(), id);
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Search History Deleted")
                .code("SEARCH_HISTORY_DELETED")
                .build());
    }
}
