package com.nodove.WSD_Assignment_03.dto.Crawler.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentWriteDto {
    private Long postId;         // 게시물 ID (채용 공고 ID)
    private String content;      // 댓글 내용
    private int rating;
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
}
