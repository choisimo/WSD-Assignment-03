package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Search History", description = "Get all search history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search History Retrieved", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Unauthorized", content =  @Content(mediaType = "application/json"))
    })
    @GetMapping("/protected/search-history")
    public ResponseEntity<?> getSearchHistory(@AuthenticationPrincipal principalDetails principalDetails) {
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Search History Retrieved")
                .code("SEARCH_HISTORY_RETRIEVED")
                .data(searchHistoryService.getSearchHistory(principalDetails.getUserId()))
                .build());
    }

    
    @Operation(summary = "Search History", description = "Get a search history by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search History Retrieved", content =  @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Unauthorized", content =  @Content(mediaType = "application/json"))
    })
    @GetMapping("/protected/search-history/{id}")
    public ResponseEntity<?> getSearchHistoryById(@AuthenticationPrincipal principalDetails principalDetails, Long id) {
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Search History Retrieved")
                .code("SEARCH_HISTORY_RETRIEVED")
                .data(searchHistoryService.getSearchHistoryById(principalDetails.getUserId(), id))
                .build());
    }

    @Operation(summary = "Delete Search History", description = "Delete all search history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search History Deleted", content =  @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Unauthorized", content =  @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/protected/search-history")
    public ResponseEntity<?> deleteSearchHistory(@AuthenticationPrincipal principalDetails principalDetails) {
        searchHistoryService.deleteSearchHistory(principalDetails.getUserId());
        return ResponseEntity.ok(ApiResponseDto.builder()
                .status("success")
                .message("Search History Deleted")
                .code("SEARCH_HISTORY_DELETED")
                .build());
    }

    @Operation(summary = "Delete Search History", description = "Delete a search history by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search History Deleted", content =  @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Unauthorized", content =  @Content(mediaType = "application/json"))
    })
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
