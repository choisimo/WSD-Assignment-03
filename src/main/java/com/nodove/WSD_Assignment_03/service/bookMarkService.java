package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.BookMarkRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPostingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class bookMarkService {

    private final BookMarkRepository bookMarkRepository;
    private final JobPostingRepository jobPostingRepository;

    public ResponseEntity<?> searchBookmarks(BookmarkSearchRequestDto bookmarkSearchRequestDto, principalDetails principalDetails) {
        if (principalDetails == null) {
            log.error("principalDetails is null");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long userId = principalDetails.getUser().getId();

        List<UserBookmark> bookmarks = bookMarkRepository.searchUserBookmarks(userId, bookmarkSearchRequestDto);

        List<BookmarkResponseDto> response = bookmarks.stream()
                .map(bookmark -> BookmarkResponseDto.builder()
                        .id(bookmark.getId())
                        .jobPostingId(bookmark.getJobPosting().getId())
                        .jobTitle(bookmark.getJobPosting().getTitle())
                        .companyName(bookmark.getJobPosting().getCompany().getName())
                        .location(bookmark.getJobPosting().getLocation())
                        .salary(bookmark.getJobPosting().getSalary())
                        .experience(bookmark.getJobPosting().getExperience())
                        .bookmarkedAt(bookmark.getBookmarkedAt())
                        .note(bookmark.getNote())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @Transactional
    public ResponseEntity<?> addOrDeleteBookmarks(principalDetails principalDeatails, BookmarkDto bookmarkDto) {
        // check if user exists in principalDetails
        if (principalDeatails == null) {
            log.error("principalDetails is null");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // check if bookmark exists and delete if it does
        bookMarkRepository.findByUserAndJobPostingId(principalDeatails.getUser() , bookmarkDto.getJobPostingId())
                .ifPresentOrElse(
                        bookMarkRepository::delete,                             // delete bookmark if it exists
                        () -> bookMarkRepository.save(UserBookmark.builder()    // create bookmark if it does not exist
                                .user(principalDeatails.getUser())
                                .jobPosting(jobPostingRepository.findById(bookmarkDto.getJobPostingId()).orElseThrow())
                                .note(bookmarkDto.getNote() != null ? bookmarkDto.getNote() : "no note provided")
                                .build())
                );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Bookmark added or deleted successfully");
    }
}
