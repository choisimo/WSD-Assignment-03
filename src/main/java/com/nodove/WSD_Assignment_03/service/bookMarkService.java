package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QJobPosting;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QUserBookmark;
import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.Bookmark.BookMarkRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingRepository;
import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
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


    @Transactional
    public ResponseEntity<?> searchBookmarks(BookmarkSearchRequestDto bookmarkSearchRequestDto, principalDetails principalDetails) {
        if (principalDetails == null) {
            log.error("principalDetails is null");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // QueryDSL 사용하여 필터링 된 북마크 리스트 조회
        QUserBookmark qUserBookmark = QUserBookmark.userBookmark;
        // JPAQueryFactory를 사용하여 QueryDSL을 사용할 수 있도록 설정
        JPAQueryFactory queryFactory = new JPAQueryFactory(bookMarkRepository.getEntityManager());

        // keyword, location, experience, salary, sector 으로 추천 목록 조회
        List<UserBookmark> filteredBookmarks = queryFactory.selectFrom(qUserBookmark)
                .where(qUserBookmark.user.eq(principalDetails.getUser())
                        .and(qUserBookmark.jobPosting.title.contains(bookmarkSearchRequestDto.getKeyword())
                                .or(qUserBookmark.jobPosting.company.name.contains(bookmarkSearchRequestDto.getKeyword()))
                                .or(qUserBookmark.jobPosting.location.contains(bookmarkSearchRequestDto.getLocation()))
                                .or(qUserBookmark.jobPosting.experience.contains(bookmarkSearchRequestDto.getExperience()))
                                .or(qUserBookmark.jobPosting.salary.contains(bookmarkSearchRequestDto.getSalary()))
                                .or(qUserBookmark.jobPosting.sector.contains(bookmarkSearchRequestDto.getSector()))
                                .or(qUserBookmark.note.contains(bookmarkSearchRequestDto.getNote())))
                )
                .fetch();

        // 필터링 된 북마크 리스트를 BookmarkResponseDto로 변환
        List<BookmarkResponseDto> response = filteredBookmarks.stream()
                .map(bookmark -> BookmarkResponseDto.builder()
                        .jobPostingId(bookmark.getJobPosting().getId())
                        .title(bookmark.getJobPosting().getTitle())
                        .companyName(bookmark.getJobPosting().getCompany().getName())
                        .location(bookmark.getJobPosting().getLocation())
                        .experience(bookmark.getJobPosting().getExperience())
                        .salary(bookmark.getJobPosting().getSalary())
                        .sector(Collections.singletonList(bookmark.getJobPosting().getSector()))
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
