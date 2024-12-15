package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
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


    @GetMapping("/bookmarks")
    public ResponseEntity<?> getBookmarks() {
        return ResponseEntity.ok("Bookmarks");
    }

    // swagger
    @PostMapping("/bookmarks")
    public ResponseEntity<?> addOrDeleteBookmarks(@AuthenticationPrincipal principalDetails principalDeatails) {
        return ResponseEntity.ok("Bookmarks");
    }


}
