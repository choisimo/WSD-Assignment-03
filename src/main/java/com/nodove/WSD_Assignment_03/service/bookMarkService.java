package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QJobPosting;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QUserBookmark;
import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.Bookmark.BookMarkRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingRepository;
import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class bookMarkService {

    private final BookMarkRepository bookMarkRepository;
    private final JobPostingRepository jobPostingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ResponseEntity<?> searchBookmarks(BookmarkSearchRequestDto bookmarkSearchRequestDto, principalDetails principalDetails) {
        if (principalDetails == null) {
            log.error("principalDetails is null");
            return ResponseEntity.status(401).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("Unauthorized")
                    .code("UNAUTHORIZED")
                    .build());
        }

        // 사용자 ID 조회
        Long userId = principalDetails.getUser().getId();

        // QueryDSL 사용하여 동적 쿼리 실행하기 위해 BookMarkRepository에서 searchUserBookmarks 메소드 호출
        List<UserBookmark> filteredBookmarks = bookMarkRepository.searchUserBookmarks(userId, bookmarkSearchRequestDto);

        // 필터링 된 북마크 리스트를 BookmarkResponseDto로 변환
        List<BookmarkResponseDto> response = filteredBookmarks.stream()
                .map(bookmark -> BookmarkResponseDto.builder()
                        .jobPostingId(bookmark.getJobPosting().getId())
                        .title(bookmark.getJobPosting().getTitle())
                        .companyName(bookmark.getJobPosting().getCompany().getName())
                        .location(bookmark.getJobPosting().getLocation())
                        .experience(bookmark.getJobPosting().getExperience())
                        .salary(bookmark.getJobPosting().getSalary())
                        .note(bookmark.getNote())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(ApiResponseDto.<List<BookmarkResponseDto>>builder()
                .status("success")
                .message("Bookmarks retrieved successfully")
                .code("BOOKMARKS_RETRIEVED")
                .data(response)
                .build());
    }


    @Transactional
    public ResponseEntity<?> addOrDeleteBookmarks(principalDetails principalDeatails, BookmarkDto bookmarkDto) {
        // check if user exists in principalDetails
        if (principalDeatails == null) {
            log.error("principalDetails is null");
            return ResponseEntity.status(401).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("Unauthorized")
                    .code("UNAUTHORIZED")
                    .build());
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

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponseDto.<Void>builder()
                .status("success")
                .message("Bookmark added or deleted successfully")
                .code("BOOKMARK_ADDED_OR_DELETED")
                .build());
    }
}
