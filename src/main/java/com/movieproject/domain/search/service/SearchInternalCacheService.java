package com.movieproject.domain.search.service;

import com.movieproject.domain.search.entity.Search;
import com.movieproject.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchInternalCacheService {
    private final SearchRepository searchRepository;

    //검색어 기록 및 카운트 증가 비동기 처리
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = "popularSearchCache", key = "'top10'")
    public void recordSearch(String keyword) {
        String normalizedKeyword = keyword.toLowerCase();

        Optional<Search> optionalSearch = searchRepository.findByKeywordForUpdate(normalizedKeyword);

        if (optionalSearch.isPresent()) {
            // 이미 존재하면 카운트 증가
            Search search = optionalSearch.get();
            search.incrementCount(keyword);
        } else {
            // 없으면 새로 저장
            try {
                searchRepository.save(Search.of(normalizedKeyword, keyword));
            } catch (Exception e) {
                // 다른 스레드가 먼저 insert 했을 경우 재시도
                Optional<Search> retrySearch = searchRepository.findByKeywordForUpdate(normalizedKeyword);
                retrySearch.ifPresent(search -> search.incrementCount(keyword));
            }
        }
    }
}
