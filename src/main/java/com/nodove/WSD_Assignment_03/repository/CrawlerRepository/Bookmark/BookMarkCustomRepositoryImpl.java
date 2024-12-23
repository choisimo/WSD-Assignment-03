package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.Bookmark;

import com.nodove.WSD_Assignment_03.domain.SaramIn.QJobPosting_Sector;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QSector;
import com.nodove.WSD_Assignment_03.domain.SaramIn.QUserBookmark;
import com.nodove.WSD_Assignment_03.domain.SaramIn.UserBookmark;
import com.nodove.WSD_Assignment_03.dto.Crawler.BookMark.BookmarkSearchRequestDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nodove.WSD_Assignment_03.domain.Qusers.users;

// This class is used to search user bookmarks. It implements BookMarkCustomRepository.

@Repository
public class BookMarkCustomRepositoryImpl implements BookMarkCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public BookMarkCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<UserBookmark> searchUserBookmarks(Long userId, BookmarkSearchRequestDto requestDto) {
/*
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
*/
        return null; // Todo : 왜 만든거지 ㄹㅇㅋㅋ 이거 안쓰는데
    }

    // Todo : 이름 길게 쓰지마세요 ㅠㅠ 히헤헤ㅏ헤헤헤 JSON 직렬화 박살 왜 남 ㅠㅠ 으어앙엉 ㅠㅠ
    @Override
    public List<UserBookmark> searchUserBookmarksWithPagingAndSortingUserByUserOrderByDescInOrderToBookmarkedDate(Pageable pageable) {
        // Todo : 이건 씀 ㅇㅇ ㅋ 으엉 이게 맞나 ㅠㅠ
        QUserBookmark userBookmark = QUserBookmark.userBookmark;
        return jpaQueryFactory.selectFrom(userBookmark)
                .join(userBookmark.user, users).fetchJoin()
                .orderBy(users.userId.asc(), userBookmark.bookmarkedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression containsKeyword(StringPath path, String keyword) {
        return keyword != null && !keyword.isEmpty() ? path.containsIgnoreCase(keyword) : null;
    }
}