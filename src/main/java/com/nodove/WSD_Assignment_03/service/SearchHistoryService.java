package com.nodove.WSD_Assignment_03.service;

import com.nodove.WSD_Assignment_03.domain.SaramIn.SearchHistory;
import com.nodove.WSD_Assignment_03.domain.users;
import com.nodove.WSD_Assignment_03.dto.Crawler.SearchHistoryDto;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.CommentRepository;
import com.nodove.WSD_Assignment_03.repository.CrawlerRepository.SearchHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final usersService usersService;
    private final CommentRepository commentRepository;
    private final redisService redisService;



    @Transactional
    public void saveSearchHistory(SearchHistoryDto searchHistoryDto) {
        SearchHistory searchHistory = SearchHistory.builder()
                .userId(usersService.getUser(searchHistoryDto.getUserId()))
                .searchKeyword(searchHistoryDto.getSearchKeyword())
                .searchDate(LocalDateTime.now())
                .build();
        searchHistoryRepository.save(searchHistory);
    }


    @Transactional
    public List<SearchHistoryDto> getSearchHistory(String userId) {
        users user =  usersService.getUser(userId);
        return searchHistoryRepository.findByUserId(user)
                .stream()
                .map(history -> new SearchHistoryDto(history.getId(), user.getUserId(), history.getSearchKeyword(), history.getSearchDate()))
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteSearchHistory(String userId) {
        users user = usersService.getUser(userId);
        searchHistoryRepository.deleteByUserId(user);
    }

    @Transactional
    public void deleteSearchHistoryById(String userId, Long id) {
        users user = usersService.getUser(userId);
        searchHistoryRepository.deleteByUserIdAndId(user, id);
    }


    @Transactional
    public Object getSearchHistoryById(String userId, Long id) {
        users user =  usersService.getUser(userId);
        return searchHistoryRepository.findByUserId(user)
                .stream()
                .map(history -> new SearchHistoryDto(history.getId(), user.getUserId(), history.getSearchKeyword(), history.getSearchDate()))
                .collect(Collectors.toList());
    }

}
