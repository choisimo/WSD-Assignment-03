package com.nodove.WSD_Assignment_03.repository.CrawlerRepository.Bookmark;

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

        return jpaQueryFactory.selectFrom(userBookmark)
                .where(
                        userBookmark.user.id.eq(userId),
                        containsKeyword(userBookmark.jobPosting.title, requestDto.getKeyword())
                                .or(containsKeyword(userBookmark.jobPosting.company.name, requestDto.getKeyword()))
                                .or(containsKeyword(userBookmark.jobPosting.location, requestDto.getLocation()))
                                .or(containsKeyword(userBookmark.jobPosting.experience, requestDto.getExperience()))
                                .or(containsKeyword(userBookmark.jobPosting.salary, requestDto.getSalary()))
/*
                                .or(containsSector(userBookmark.jobPosting.sectors, requestDto.getSector())) // 수정된 부분
*/
                                .or(containsKeyword(userBookmark.note, requestDto.getNote()))
                )
                .fetch();
    }

    private BooleanExpression containsKeyword(StringPath path, String keyword) {
        return keyword != null && !keyword.isEmpty() ? path.containsIgnoreCase(keyword) : null;
    }
}