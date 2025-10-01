package com.movieproject.search;

import com.movieproject.domain.search.entity.Search;
import com.movieproject.domain.search.repository.SearchRepository;
import com.movieproject.domain.search.service.SearchInternalCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchInternalCacheServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchInternalCacheService internalCacheService;

    private Search createMockSearch(String originalKeyword) {
        Search mockSearch = Search.of(originalKeyword.toLowerCase(), originalKeyword);
        return mock(Search.class);
    }

    @Test
    void 기존키워드_검색시_카운트증가() {
        // given
        String keyword = "극한직업";
        String normalizedKeyword = keyword.toLowerCase();
        Search mockSearch = createMockSearch(keyword);

        //findByKeywordForUpdate 호출 시 엔티티 반환
        when(searchRepository.findByKeywordForUpdate(eq(normalizedKeyword))).thenReturn(Optional.of(mockSearch));
        // when
        internalCacheService.recordSearch(keyword);

        // then
        verify(searchRepository, times(1)).findByKeywordForUpdate(eq(normalizedKeyword));
        verify(mockSearch, times(1)).incrementCount(eq(keyword));

        // save는 호출되지 않음
        verify(searchRepository, never()).save(any(Search.class));
        // incrementCount JPQL 메서드는 사용하지 않음
        verify(searchRepository, never()).incrementCount(anyString(), anyString());
    }

    @Test
    void 신규키워드_검색시_save호출() {
        // given
        String keyword = "신과함께";
        String normalizedKeyword = keyword.toLowerCase();

        // findByKeywordForUpdate 호출 시 Optional.empty 반환 (신규 키워드)
        when(searchRepository.findByKeywordForUpdate(eq(normalizedKeyword))).thenReturn(Optional.empty());
        // save 호출 시 인자 그대로 반환
        when(searchRepository.save(any(Search.class))).thenAnswer(i -> i.getArgument(0));

        // when
        internalCacheService.recordSearch(keyword);

        // then
        verify(searchRepository, times(1)).findByKeywordForUpdate(eq(normalizedKeyword));
        verify(searchRepository, times(1)).save(any(Search.class));
        verify(searchRepository, never()).incrementCount(anyString(), anyString());
    }

    @Test
    void 동시성_충돌시_save실패후_재시도() {
        // given
        String keyword = "동시성테스트";
        String normalizedKeyword = keyword.toLowerCase();
        Search mockSearch = createMockSearch(keyword);

        // 키워드 없을 시 재시도
        when(searchRepository.findByKeywordForUpdate(eq(normalizedKeyword)))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(mockSearch));

        // save 호출 시 동시성 충돌
        when(searchRepository.save(any(Search.class))).thenThrow(DataIntegrityViolationException.class);

        // when
        internalCacheService.recordSearch(keyword);

        // then
        // 충돌 후 재시도
        verify(searchRepository, times(2)).findByKeywordForUpdate(eq(normalizedKeyword));

        // save는 1번 호출되어 예외를 발생시킴
        verify(searchRepository, times(1)).save(any(Search.class));

        // 재시도
        verify(mockSearch, times(1)).incrementCount(eq(keyword));
    }
}