package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.domain.users;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class bookMarkService {

    private final BookMarkRepository bookMarkRepository;
    private final JobPostingRepository jobPostingRepository;
    private final com.nodove.WSD_Assignment_03.repository.usersRepository usersRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ResponseEntity<?> searchBookmarks(BookmarkSearchRequestDto bookmarkSearchRequestDto, principalDetails principalDetails) {
        // 사용자 ID 조회
        users user = usersRepository.findByUserId(principalDetails.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        // 관리자 권한 확인하기
        List<UserBookmark> userBookmarkList = null;
        if (user.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            log.error("User is an admin");
            // 관리자는 모든 사용자 북마크 조회 가능 (사용자 별로 정렬)
            Pageable pageable = Pageable.ofSize(bookmarkSearchRequestDto.getPageSize()).withPage(bookmarkSearchRequestDto.getPageNumber());

            userBookmarkList = bookMarkRepository.searchUserBookmarksWithPagingAndSortingUserByUserOrderByDescInOrderToBookmarkedDate(pageable);
            return ResponseEntity.ok().body(ApiResponseDto.<List<BookmarkResponseDto>>builder()
                    .status("success")
                    .message("Bookmarks retrieved successfully")
                    .code("BOOKMARKS_RETRIEVED")
                    .data(Objects.requireNonNull(userBookmarkList).stream().map(bookmark -> BookmarkResponseDto.builder()
                            .jobPostingId(bookmark.getJobPosting().getId())
                            .note(bookmark.getNote())
                            .bookmarkedAt(bookmark.getBookmarkedAt())
                            .build()).collect(Collectors.toList()))
                    .build());
        } else {
            log.error("User is not an admin");
            // 사용자 별 북마크 조회
            userBookmarkList = bookMarkRepository.searchUserBookmarks(user.getId(), bookmarkSearchRequestDto);
            return ResponseEntity.ok().body(ApiResponseDto.<List<BookmarkResponseDto>>builder()
                    .status("success")
                    .message("Bookmarks retrieved successfully")
                    .code("BOOKMARKS_RETRIEVED")
                    .data(Objects.requireNonNull(userBookmarkList).stream().map(bookmark -> BookmarkResponseDto.builder()
                            .jobPostingId(bookmark.getJobPosting().getId())
                            .note(bookmark.getNote())
                            .bookmarkedAt(bookmark.getBookmarkedAt())
                            .build()).collect(Collectors.toList()))
                    .build());
        }
    }


    @Transactional
    public ResponseEntity<?> addOrDeleteBookmarks(principalDetails principalDeatails, BookmarkDto bookmarkDto) {
        // check if user exists in principalDetails
        if (principalDeatails == null) {
            log.error("principalDetails is null");
            return ResponseEntity.status(401).body(ApiResponseDto.<Void>builder()
                    .status("error")
                    .message("principalDetails is null or invalid")
                    .code("UNAUTHORIZED")
                    .build());
        }

        users user = usersRepository.findByUserId(principalDeatails.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        // check if bookmark exists and delete if it does
        bookMarkRepository.findByUserAndJobPostingId(user , bookmarkDto.getJobPostingId())
                .ifPresentOrElse(
                        bookMarkRepository::delete,                             // delete bookmark if it exists
                        () -> bookMarkRepository.save(UserBookmark.builder()    // create bookmark if it does not exist
                                .user(user)
                                .jobPosting(jobPostingRepository.findById(bookmarkDto.getJobPostingId()).orElseThrow(() -> new RuntimeException("Job Posting not found")))
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
