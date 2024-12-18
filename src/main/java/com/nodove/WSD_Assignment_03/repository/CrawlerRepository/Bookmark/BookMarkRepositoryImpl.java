/*
package com.nodove.WSD_Assignment_03.repository.CrawlerRepository;

import com.nodove.WSD_Assignment_03.domain.SaramIn.QJobPosting;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QUserBookmark;
import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookMarkRepositoryImpl implements BookMarkRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<UserBookmark> searchBookmarks(Long userId, BookmarkSearchRequestDto requestDto) {
        QUserBookmark userBookmark = QUserBookmark.userBookmark;
        QJobPosting jobPosting = QJobPosting.jobPosting;

        return jpaQueryFactory
                .selectFrom(userBookmark)
                .join(userBookmark.jobPosting, jobPosting)
                .where(
                        userBookmark.user.id.eq(userId)
                                .and(jobPosting.title.containsIgnoreCase(requestDto.getKeyword()))
                                .and(jobPosting.location.eq(requestDto.getLocation()))
                                .and(jobPosting.experience.eq(requestDto.getExperience()))
                                .and(jobPosting.salary.eq(requestDto.getSalary()))
                )
                .orderBy(jobPosting.postedAt.desc()) // 최신순 정렬
                .offset((long) requestDto.getPage() * requestDto.getSize())
                .limit(requestDto.getSize())
                .fetch();
    }
}
*/
