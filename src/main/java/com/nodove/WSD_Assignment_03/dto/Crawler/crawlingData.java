package com.nodove.WSD_Assignment_03.dto.Crawler;

import lombok.Data;

import java.util.List;

@Data
public class crawlingData {
    private List<String> keywords;// 검색 키워드
    private int totalPage; // 총 페이지 수
}
