package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import com.nodove.WSD_Assignment_03.service.bookMarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class bookmarksController {

    private final bookMarkService bookMarkService;

    @GetMapping("/bookmarks")
    public ResponseEntity<?> searchBookmarks(BookmarkSearchRequestDto requestDto, @AuthenticationPrincipal principalDetails principalDetails) {
        return bookMarkService.searchBookmarks(requestDto, principalDetails);
    }

    // swagger
    @PostMapping("/bookmarks")
    public ResponseEntity<?> addOrDeleteBookmarks(@AuthenticationPrincipal principalDetails principalDeatails, BookmarkDto bookmarkDto) {
        return bookMarkService.addOrDeleteBookmarks(principalDeatails, bookmarkDto);
    }

}
