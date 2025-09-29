package com.movieproject.domain.search.service;

import com.movieproject.domain.search.entity.Search;
import com.movieproject.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchInternalCacheService {
    private final SearchRepository searchRepository;

    //검색어 기록 및 카운트 증가 비동기 처리
    //새로운 트랙잭션 생성
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSearch(String keyword) {
        String normalizedKeyword = keyword.toLowerCase();

        int updated = searchRepository.incrementCount(normalizedKeyword, keyword);
        if (updated == 0) {
            try {
                searchRepository.save(Search.of(normalizedKeyword, keyword));
            } catch (Exception e) {
                // 동시성 문제로 인해 insert에 실패하면 update 재시도
                searchRepository.incrementCount(normalizedKeyword, keyword);
            }
        }
    }
}
