package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Comment;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.CommentDto;
import com.nodove.WSD_Assignment_03.service.CommentService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/protected/comments")
    public ResponseEntity<?> createComment(@RequestBody CommentDto commentDto) {
        commentService.createComment(commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.<Comment>builder()
                .status("success")
                .message("Comment Created")
                .code("COMMENT_CREATED")
                .build());
    }

    @GetMapping("/protected/comments/{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id,
                                        @Parameter(description = "Page number", example = "0")
                                        @RequestParam(required = true) int page,
                                        @Parameter(description = "Number of items per page", example = "10")
                                        @RequestParam(required = true) int size,
                                        @Parameter(description = "Sort order", example = "asc")
                                        @RequestParam(required = true) String sort){
        if (page < 0 || size < 0) {
            return ResponseEntity.badRequest().body(ApiResponseDto.<Void>builder()
                    .status("fail")
                    .message("Invalid Page or Size")
                    .code("INVALID_PAGE_SIZE")
                    .build());
        }

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        return ResponseEntity.ok(ApiResponseDto.<List<Comment>>builder()
                .status("success")
                .message("Comment Retrieved")
                .code("COMMENT_RETRIEVED")
                .data(commentService.getCommentsByPostId(id, pageable, sort))
                .build());
    }

    @GetMapping("/protected/comments/{id}/{cid}")
    public ResponseEntity<?> getComment(@PathVariable Long id, @PathVariable Long cid){
        return ResponseEntity.ok(ApiResponseDto.<CommentDto>builder()
                .status("success")
                .message("Comment Retrieved")
                .code("COMMENT_RETRIEVED")
                .data(commentService.getComment(id, cid))
                .build());
    }


    @PutMapping("/protected/comments/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .status("success")
                .message("Comment Updated")
                .code("COMMENT_UPDATED")
                .build());
    }

    @DeleteMapping("/protected/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().body(ApiResponseDto.<Void>builder()
                .status("success")
                .message("Comment Deleted")
                .code("COMMENT_DELETED")
                .build());
    }
}
