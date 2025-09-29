package com.movieproject.search;

import com.movieproject.domain.actor.service.ActorInternalService;
import com.movieproject.domain.director.service.DirectorInternalService;
import com.movieproject.domain.movie.dto.response.MovieSearchResponse;
import com.movieproject.domain.movie.service.MovieInternalService;
import com.movieproject.domain.search.entity.Search;
import com.movieproject.domain.search.repository.SearchRepository;
import com.movieproject.domain.search.service.SearchExternalCacheService;
import com.movieproject.domain.search.service.SearchInternalCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchExternalCacheServiceTest {

    @Mock
    private MovieInternalService internalMovieService;

    @Mock
    private DirectorInternalService internalDirectorService;

    @Mock
    private ActorInternalService internalActorService;

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private SearchInternalCacheService searchInternalCacheService;

    @InjectMocks
    private SearchExternalCacheService externalCacheService;

    private MovieSearchResponse sampleResponse(String title) {
        return new MovieSearchResponse(
                1L, title, LocalDate.of(2019, 5, 30),
                "봉준호", "송강호", "드라마", 9.0
        );
    }

    @Test
    void 영화제목검색_MovieService_호출() {
        // given
        String title = "기생충";
        Page<MovieSearchResponse> mockPage =
                new PageImpl<>(List.of(sampleResponse(title)), PageRequest.of(0, 10), 1);

        // 실제 DB 조회 없이 미리 만든 가짜 결과를 반환
        when(internalMovieService.searchByKeyword(eq(title), anyInt(), anyInt())).thenReturn(mockPage);

        // when
        Page<MovieSearchResponse> result = externalCacheService.searchTitle(title, 0, 10);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(internalMovieService, times(1)).searchByKeyword(eq(title), eq(0), eq(10));
    }

    @Test
    void 인기검색어조회_Repository_호출() {
        // given
        Search search1 = Search.of("기생충", "기생충");
        Search search2 = Search.of("송강호", "송강호");

        when(searchRepository.findTop10ByOrderByCountDesc()).thenReturn(List.of(search1, search2));

        // when
        List<String> popularSearches = externalCacheService.getPopularSearches();

        // then
        assertThat(popularSearches).containsExactly("기생충", "송강호");
        verify(searchRepository, times(1)).findTop10ByOrderByCountDesc();
    }
}