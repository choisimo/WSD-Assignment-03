package com.nodove.WSD_Assignment_03.controller;

import com.nodove.WSD_Assignment_03.configuration.token.principalDetails.principalDetails;
import com.nodove.WSD_Assignment_03.domain.SaramIn.Comment;
import com.nodove.WSD_Assignment_03.dto.ApiResponse.ApiResponseDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.Comment.CommentDto;
import com.nodove.WSD_Assignment_03.dto.Crawler.Comment.CommentWriteDto;
import com.nodove.WSD_Assignment_03.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "새 댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Unauthorized", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    })
    @PostMapping("/protected/comments")
    public ResponseEntity<?> createComment(@AuthenticationPrincipal principalDetails principalDetails,
                                           @RequestBody CommentWriteDto commentDto) {
        commentService.createComment(commentDto, principalDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.<Comment>builder()
                .status("success")
                .message("Comment Created")
                .code("COMMENT_CREATED")
                .build());
    }

    @Operation(summary = "댓글 조회", description = "게시물의 댓글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid Page or Size", content = @Content(mediaType = "application/json"))
    })
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

        return ResponseEntity.ok(ApiResponseDto.<List<CommentDto>>builder()
                .status("success")
                .message("Comment Retrieved")
                .code("COMMENT_RETRIEVED")
                .data(commentService.getCommentsByPostId(id, pageable, sort))
                .build());
    }

    @Operation(summary = "댓글 조회", description = "특정 댓글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid Page or Size", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/protected/comments/{id}/{cid}")
    public ResponseEntity<?> getComment(@PathVariable Long id, @PathVariable Long cid){
        return ResponseEntity.ok(ApiResponseDto.<CommentDto>builder()
                .status("success")
                .message("Comment Retrieved")
                .code("COMMENT_RETRIEVED")
                .data(commentService.getComment(id, cid))
                .build());
    }


    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid Page or Size", content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/protected/comments/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .status("success")
                .message("Comment Updated")
                .code("COMMENT_UPDATED")
                .build());
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid Page or Size", content = @Content(mediaType = "application/json"))
    })
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
