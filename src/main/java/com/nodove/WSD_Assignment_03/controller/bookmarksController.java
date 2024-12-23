package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import com.nodove.WSD_Assignment_03.service.bookMarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    public ResponseEntity<?> searchBookmarks(@AuthenticationPrincipal principalDetails principalDetails,
                                                @Parameter(description = "한 페이지에 표시할 북마크 수", example = "20")
                                                @RequestParam("pageSize") @DefaultValue("20") int pageSize,
                                                @Parameter(description = "페이지 번호", example = "1")
                                                @RequestParam("pageNumber") @DefaultValue("1") int pageNumber,
                                                @Parameter(description = "메모 기록")
                                                @RequestParam(value = "search", required = false) String note) {

        BookmarkSearchRequestDto bookmarkSearchRequestDto = BookmarkSearchRequestDto.builder()
                .note(note)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
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
    public ResponseEntity<?> addOrDeleteBookmarks(@AuthenticationPrincipal principalDetails principalDeatails,
                                                  @RequestBody(description = "") BookmarkDto bookmarkDto) {
        return bookMarkService.addOrDeleteBookmarks(principalDeatails, bookmarkDto);
    }

}
