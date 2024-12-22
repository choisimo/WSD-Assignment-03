package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.Bookmark;

import com.nodove.WSD_Assignment_03.domain.SaramIn.QJobPosting_Sector;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QSector;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QUserBookmark;
import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

// This class is used to search user bookmarks. It implements BookMarkCustomRepository.
@Repository
public class BookMarkCustomRepositoryImpl implements BookMarkCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public BookMarkCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<UserBookmark> searchUserBookmarks(Long userId, BookmarkSearchRequestDto requestDto) {
        QUserBookmark userBookmark = QUserBookmark.userBookmark;
        QJobPosting_Sector jobPostingSector = QJobPosting_Sector.jobPosting_Sector;
        QSector sector = QSector.sector;

        // QueryDSL을 사용하여 동적 쿼리 실행
        return jpaQueryFactory.selectFrom(userBookmark)
                .join(userBookmark.jobPosting, jobPostingSector.jobPosting) // JobPosting_Sector를 통해 조인
                .join(jobPostingSector.sector, sector) // Sector와 조인
                .where(
                        userBookmark.user.id.eq(userId),
                        containsKeyword(userBookmark.jobPosting.title, requestDto.getKeyword())
                                .or(containsKeyword(userBookmark.jobPosting.company.name, requestDto.getKeyword()))
                                .or(containsKeyword(userBookmark.jobPosting.location, requestDto.getLocation()))
                                .or(containsKeyword(userBookmark.jobPosting.experience, requestDto.getExperience()))
                                .or(containsKeyword(userBookmark.jobPosting.salary, requestDto.getSalary()))
                                .or(sector.name.containsIgnoreCase(requestDto.getSector())) // Sector 이름 부분 일치
                                .or(containsKeyword(userBookmark.note, requestDto.getNote()))
                )
                .orderBy(userBookmark.bookmarkedAt.desc())
                .fetch();
    }

    private BooleanExpression containsKeyword(StringPath path, String keyword) {
        return keyword != null && !keyword.isEmpty() ? path.containsIgnoreCase(keyword) : null;
    }
}