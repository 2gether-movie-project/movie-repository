package com.movieproject.search;

import com.movieproject.domain.actor.service.ActorInternalService;
import com.movieproject.domain.director.service.DirectorInternalService;
import com.movieproject.domain.movie.dto.response.MovieSearchResponse;
import com.movieproject.domain.movie.service.MovieInternalService;
import com.movieproject.domain.search.entity.Search;
import com.movieproject.domain.search.exception.InvalidSearchException;
import com.movieproject.domain.search.exception.SearchErrorCode;
import com.movieproject.domain.search.repository.SearchRepository;
import com.movieproject.domain.search.service.SearchExternalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchExternalServiceTest {

    @Mock
    private MovieInternalService internalMovieService;

    @Mock
    private ActorInternalService internalActorService;

    @Mock
    private DirectorInternalService internalDirectorService;

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchExternalService externalSearchService;

    private MovieSearchResponse sampleResponse(String title) {
        return new MovieSearchResponse(
                1L,
                title,
                LocalDate.of(2019, 5, 30),
                "봉준호",
                "송강호",
                "드라마",
                9.0
        );
    }

    @Test
    void 영화제목검색_신규키워드_정상처리() {
        // given
        String title = "기생충";
        Page<MovieSearchResponse> mockPage =
                new PageImpl<>(List.of(sampleResponse(title)), PageRequest.of(0, 10), 1);

        when(internalMovieService.searchByKeyword(eq(title), anyInt(), anyInt())).thenReturn(mockPage);
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(0);
        when(searchRepository.save(any(Search.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Page<MovieSearchResponse> result = externalSearchService.searchTitle(title, 0, 10);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo(title);

        verify(searchRepository).incrementCount(eq(title.toLowerCase()), eq(title));
        verify(searchRepository).save(any(Search.class));
    }

    @Test
    void 영화배우검색_신규키워드_정상처리() {
        // given
        String actorName = "송강호";
        Page<MovieSearchResponse> mockPage =
                new PageImpl<>(List.of(sampleResponse("기생충")), PageRequest.of(0, 10), 1);

        when(internalActorService.searchByKeyword(eq(actorName), anyInt(), anyInt())).thenReturn(mockPage);
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(0);
        when(searchRepository.save(any(Search.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Page<MovieSearchResponse> result = externalSearchService.searchActor(actorName, 0, 10);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(searchRepository).incrementCount(eq(actorName.toLowerCase()), eq(actorName));
        verify(searchRepository).save(any(Search.class));
    }

    @Test
    void 감독검색_신규키워드_정상처리() {
        // given
        String directorName = "봉준호";
        Page<MovieSearchResponse> mockPage =
                new PageImpl<>(List.of(sampleResponse("기생충")), PageRequest.of(0, 10), 1);

        when(internalDirectorService.searchByKeyword(eq(directorName), anyInt(), anyInt())).thenReturn(mockPage);
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(0);
        when(searchRepository.save(any(Search.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Page<MovieSearchResponse> result = externalSearchService.searchDirector(directorName, 0, 10);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(searchRepository).incrementCount(eq(directorName.toLowerCase()), eq(directorName));
        verify(searchRepository).save(any(Search.class));
    }

    @Test
    void 영화제목검색_기존키워드_카운트증가() {
        // given
        String title = "기생충";
        Page<MovieSearchResponse> mockPage =
                new PageImpl<>(List.of(sampleResponse(title)), PageRequest.of(0, 10), 1);

        when(internalMovieService.searchByKeyword(eq(title), anyInt(), anyInt())).thenReturn(mockPage);
        when(searchRepository.incrementCount(anyString(), anyString())).thenReturn(1);

        // when
        externalSearchService.searchTitle(title, 0, 10);

        // then
        verify(searchRepository).incrementCount(eq(title.toLowerCase()), eq(title));
        verify(searchRepository, never()).save(any(Search.class));
    }

    @Test
    void 영화제목검색_신규키워드_동시성충돌_처리() {
        // given
        String title = "기생충";
        Page<MovieSearchResponse> mockPage =
                new PageImpl<>(List.of(sampleResponse(title)), PageRequest.of(0, 10), 1);

        when(internalMovieService.searchByKeyword(eq(title), anyInt(), anyInt())).thenReturn(mockPage);
        when(searchRepository.incrementCount(anyString(), anyString()))
                .thenReturn(0) // 첫 호출 → 신규 키워드
                .thenReturn(1); // save 실패 후 재시도 → 성공

        when(searchRepository.save(any(Search.class))).thenThrow(DataIntegrityViolationException.class);

        // when
        externalSearchService.searchTitle(title, 0, 10);

        // then
        verify(searchRepository, times(2)).incrementCount(eq(title.toLowerCase()), eq(title));
        verify(searchRepository, times(1)).save(any(Search.class));
    }

    @Test
    void 검색어_null이면_예외발생() {
        assertThatThrownBy(() -> externalSearchService.searchTitle(null, 0, 10))
                .isInstanceOf(InvalidSearchException.class)
                .hasMessage(SearchErrorCode.INVALID_KEYWORD.getMessage());
    }

    @Test
    void 검색어_공백이면_예외발생() {
        assertThatThrownBy(() -> externalSearchService.searchActor(" ", 0, 10))
                .isInstanceOf(InvalidSearchException.class)
                .hasMessage(SearchErrorCode.INVALID_KEYWORD.getMessage());
    }
}
