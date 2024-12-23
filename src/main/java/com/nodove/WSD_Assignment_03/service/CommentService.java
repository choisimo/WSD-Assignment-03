package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Comment;
import com.nodove.WSD_Assignment_03.dto.Crawler.CommentDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.CommentRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.JobPosting.JobPostingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final jobsService jobService;
    private final usersService userService;
    private final redisService redisService;

    @Transactional
    public void createComment(CommentDto commentDto) {
        Comment comment = Comment.builder()
                .jobPosting(jobService.getJobPostingById(commentDto.getPostId()))
                .userId(userService.getUserById(commentDto.getUserId()))
                .content(commentDto.getContent())
                .build();
        log.info("new comment with id {} created", comment.getId());
        commentRepository.save(comment);
    }

    @Transactional
    public List<Comment> getCommentsByPostId(Long postId, Pageable pageable, String sort) {
        return commentRepository.findByJobPosting(jobService.getJobPostingById(postId), pageable)
                .stream()
                .map(comment -> Comment.builder()
                        .id(comment.getId())
                        .jobPosting(comment.getJobPosting())
                        .userId(comment.getUserId())
                        .content(comment.getContent())
                        .createdDate(comment.getCreatedDate())
                        .rating(comment.getRating())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto getComment(Long id, Long commentId) {
        Comment comment = commentRepository.findByJobPostingAndId(jobService.getJobPostingById(id), commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found for the given post and comment ID."));
        return CommentDto.builder()
                .postId(comment.getJobPosting().getId())
                .userId(comment.getUserId().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedDate())
                .rating(comment.getRating())
                .build(
        );
    }


    @Transactional
    public void updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found."));
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
    }


    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
