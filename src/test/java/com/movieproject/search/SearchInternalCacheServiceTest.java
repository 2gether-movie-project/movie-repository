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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchInternalCacheServiceTest {

    @Mock
    private SearchRepository searchRepository;

    // @Async는 단위 테스트에서는 동기적으로 실행됨
    @InjectMocks
    private SearchInternalCacheService internalCacheService;

    @Test
    void 신규키워드_검색시_카운트0_save호출() {
        // given
        String keyword = "극한직업";
        // count -> 0 반환
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(0);
        // save
        when(searchRepository.save(any(Search.class))).thenAnswer(i -> i.getArgument(0));

        // when
        internalCacheService.recordSearch(keyword);

        // then
        // incrementCount 호출
        verify(searchRepository, times(1)).incrementCount(eq(keyword.toLowerCase()), eq(keyword));
        // save가 호출되어 DB에 신규 엔티티를 저장
        verify(searchRepository, times(1)).save(any(Search.class));
    }

    @Test
    void 기존키워드_검색시_카운트만_증가하고_save_호출_안됨() {
        // given
        String keyword = "극한직업";
        // count -> 1 반환
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(1);

        // when
        internalCacheService.recordSearch(keyword);

        // then
        // incrementCount 호출
        verify(searchRepository, times(1)).incrementCount(eq(keyword.toLowerCase()), eq(keyword));
        // save는 호출되지 않음
        verify(searchRepository, never()).save(any(Search.class));
    }

    @Test
    void 신규키워드_동시성_충돌시_save실패후_incrementCount_재시도() {
        // given
        String keyword = "극한직업";

        // 1. incrementCount -> 0 반환 (신규 키워드로 판단)
        // 2. save 실패 (다른 스레드가 먼저 저장)
        // 3. incrementCount 재시도 -> 1 반환 (성공적으로 카운트 증가)
        when(searchRepository.incrementCount(anyString(), anyString()))
                .thenReturn(0)
                .thenReturn(1);

        when(searchRepository.save(any(Search.class))).thenThrow(DataIntegrityViolationException.class);

        // when
        internalCacheService.recordSearch(keyword);

        // then
        // incrementCount는 총 2번 호출 (최초 시도 + 동시성 충돌 후 재시도)
        verify(searchRepository, times(2)).incrementCount(eq(keyword.toLowerCase()), eq(keyword));
        // save는 1번 호출되어 예외를 발생시켰는지 검증
        verify(searchRepository, times(1)).save(any(Search.class));
    }
}