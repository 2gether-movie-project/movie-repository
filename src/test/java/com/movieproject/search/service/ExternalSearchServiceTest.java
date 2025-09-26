package com.movieproject.search.service;

import com.movieproject.domain.search.entity.Search;
import com.movieproject.domain.search.exception.InvalidSearchException;
import com.movieproject.domain.search.exception.SearchErrorCode;
import com.movieproject.domain.search.repository.SearchRepository;
import com.movieproject.domain.search.service.ExternalSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExternalSearchServiceTest {
    @Mock
    private InternalMovieService internalMovieService;

    @Mock
    private InternalActorService internalActorService;

    @Mock
    private InternalDirectorService internalDirectorService;

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private ExternalSearchService externalSearchService;


    @Test
    void 영화제목검색_신규키워드_정상처리() {
        // given
        String title = "기생충";
        // 1. 실제 검색 Mock
        when(internalMovieService.searchByKeyword(title)).thenReturn(List.of(new MovieDto(title)));

        // 2. recordSearch Mocking: DB에 키워드가 없으므로 update는 0 반환 -> save 로직 실행
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(0);
        when(searchRepository.save(any(Search.class))).thenAnswer(i -> i.getArgument(0));

        // when
        List<MovieDto> result = externalSearchService.searchTitle(title);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(title);

        // recordSearch 검증: incrementCount 호출 후, save가 호출되었는지 확인
        verify(searchRepository).incrementCount(eq(title.toLowerCase()), eq(title));
        verify(searchRepository).save(any(Search.class));
    }

    @Test
    void 영화배우검색_신규키워드_정상처리() {
        // given
        String actorName = "송강호";
        when(internalActorService.searchByKeyword(actorName)).thenReturn(List.of(new MovieDto("기생충")));
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(0);
        when(searchRepository.save(any(Search.class))).thenAnswer(i -> i.getArgument(0));

        // when
        List<MovieDto> result = externalSearchService.searchActor(actorName);

        // then
        assertThat(result).hasSize(1);
        verify(searchRepository).incrementCount(eq(actorName.toLowerCase()), eq(actorName));
        verify(searchRepository).save(any(Search.class));
    }

    @Test
    void 감독검색_신규키워드_정상처리() {
        // given
        String directorName = "봉준호";
        when(internalDirectorService.searchByKeyword(directorName)).thenReturn(List.of(new MovieDto("기생충")));
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(0);
        when(searchRepository.save(any(Search.class))).thenAnswer(i -> i.getArgument(0));

        // when
        // 이전 오류 수정: search 대신 searchDirector 호출
        List<MovieDto> result = externalSearchService.searchDirector(directorName);

        // then
        assertThat(result).hasSize(1);
        verify(searchRepository).incrementCount(eq(directorName.toLowerCase()), eq(directorName));
        verify(searchRepository).save(any(Search.class));
    }

    @Test
    void 영화제목검색_기존키워드_카운트증가() {
        // given
        String title = "기생충";
        when(internalMovieService.searchByKeyword(title)).thenReturn(List.of(new MovieDto(title)));

        // recordSearch Mocking: DB에 키워드가 있으므로 update는 1 반환 -> save는 호출되면 안 됨!
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(1);

        // when
        externalSearchService.searchTitle(title);

        // then
        // incrementCount는 호출되어야 함
        verify(searchRepository).incrementCount(eq(title.toLowerCase()), eq(title));
        // save는 호출되지 않아야 함
        verify(searchRepository, never()).save(any(Search.class));
    }

    @Test
    void 영화제목검색_신규키워드_동시성충돌_처리() {
        // given
        String title = "기생충";
        when(internalMovieService.searchByKeyword(title)).thenReturn(List.of(new MovieDto(title)));

        // 1. 처음 update는 0 반환
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(0)
                // 4. save 실패 후 재시도하는 incrementCount는 1 반환 (성공 처리)
                .thenReturn(1);

        // 2. save 시 DataIntegrityViolationException 발생 Mocking (충돌 시뮬레이션)
        when(searchRepository.save(any(Search.class))).thenThrow(DataIntegrityViolationException.class);

        // when
        externalSearchService.searchTitle(title);

        // then
        // 3. incrementCount가 총 2번 호출되었는지 확인 (처음 시도 + 충돌 후 재시도)
        verify(searchRepository, times(2)).incrementCount(eq(title.toLowerCase()), eq(title));
        verify(searchRepository, times(1)).save(any(Search.class)); // save는 1번 호출됨
    }

    @Test
    void 검색어_null이면_예외발생() {
        assertThatThrownBy(() -> externalSearchService.searchTitle(null))
                .isInstanceOf(InvalidSearchException.class)
                .hasMessage(SearchErrorCode.INVALID_KEYWORD.getMessage());
    }

    @Test
    void 검색어_공백이면_예외발생() {
        assertThatThrownBy(() -> externalSearchService.searchActor(" "))
                .isInstanceOf(InvalidSearchException.class)
                .hasMessage(SearchErrorCode.INVALID_KEYWORD.getMessage());
    }
}