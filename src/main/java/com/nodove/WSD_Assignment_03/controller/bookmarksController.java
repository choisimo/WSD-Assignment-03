package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import com.nodove.WSD_Assignment_03.service.bookMarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class bookmarksController {

    private final bookMarkService bookMarkService;

    @Operation (summary = "북마크 조회", description = "북마크를 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "북마크 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/bookmarks")
    public ResponseEntity<?> searchBookmarks(@AuthenticationPrincipal principalDetails principalDetails, @RequestParam("pageSize") @DefaultValue("20") int pageSize, @RequestParam("pageNumber") @DefaultValue("1") int pageNumber, @RequestParam("keyword") String keyword, @RequestParam("location") String location, @RequestParam("experience") String experience, @RequestParam("salary") String salary, @RequestParam("sortedBy") @DefaultValue("latest") String sortedBy, @RequestParam("sector") String sector, @RequestParam("note") @DefaultValue("no content") String note) {
        if (principalDetails == null) {
            log.error("principalDetails is null");
            return ResponseEntity.status(401).body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("Unauthorized")
                            .message("there is no principalDetails")
                            .build()
            );
        }
        if (pageSize == 0 || pageNumber == 0) {
            log.error("pageSize or page is null");
            return ResponseEntity.status(400).body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("Bad Request")
                            .message("pageSize or page is null")
                            .build()
            );
        }

        BookmarkSearchRequestDto bookmarkSearchRequestDto = BookmarkSearchRequestDto.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .keyword(keyword)
                .location(location)
                .experience(experience)
                .salary(salary)
                .sortedBy(sortedBy)
                .sector(sector)
                .note(note)
                .build();

        return bookMarkService.searchBookmarks(bookmarkSearchRequestDto, principalDetails);
    }

    // swagger
    @Operation(summary = "북마크 추가/제거", description = "북마크를 추가하거나 제거합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "북마크 추가/제거 성공"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Failed to add or delete bookmark")
    })
    @PostMapping("/bookmarks")
    public ResponseEntity<?> addOrDeleteBookmarks(@AuthenticationPrincipal principalDetails principalDeatails, BookmarkDto bookmarkDto) {
        if (principalDeatails == null) {
            log.error("there is no principalDetails");
            return ResponseEntity.status(401).body(
                    ApiResponseDto.<Void>builder()
                            .status("error")
                            .code("Unauthorized")
                            .message("there is no principalDetails")
                            .build()
            );
        }
        return bookMarkService.addOrDeleteBookmarks(principalDeatails, bookmarkDto);
    }

}
