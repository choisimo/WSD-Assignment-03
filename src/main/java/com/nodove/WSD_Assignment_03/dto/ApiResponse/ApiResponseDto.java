package com.nodove.WSD_Assignment_03.dto.ApiResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponseDto<T> {
    private String status; // "success" or "error"
    private T data; // 실제 데이터
    private String message; // 에러 메시지 (실패 시)
    private String code; // 에러 코드 (실패 시)
    private Pagination pagination; // 페이지네이션 정보 (옵션)

    @Data
    @Builder
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalItems;
    }
}
