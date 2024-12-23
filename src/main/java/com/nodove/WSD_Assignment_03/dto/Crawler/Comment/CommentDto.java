package com.nodove.WSD_Assignment_03.dto.Crawler.Comment;

import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import com.nodove.WSD_Assignment_03.domain.users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;             // 댓글 ID
    private Long postId;         // 게시물 ID (채용 공고 ID)
    private String userName;
    private String content;      // 댓글 내용
    private int rating;
    private JobPosting jobPosting;
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간

}
