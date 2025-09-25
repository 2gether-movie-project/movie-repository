package com.movieproject.movie;

import com.movieproject.common.exception.GlobalException;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.movie.dto.response.MovieListDto;
import com.movieproject.domain.movie.dto.response.MovieResponseDto;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.exception.MovieErrorCode;
import com.movieproject.domain.movie.repository.MovieRepository;
import com.movieproject.domain.movie.service.MovieExternalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieExternalServiceTest {

    @InjectMocks
    private MovieExternalService movieExternalService;

    @Mock
    private MovieRepository movieRepository;

    @Test
    @DisplayName("영화 조회 성공 테스트")
    void getMovieByMovieId_success() {
        // given
        Long movieId = 1L;
        Movie mockMovie = Movie.builder().build();
        ReflectionTestUtils.setField(mockMovie, "id", movieId);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));

        // when
        Movie foundMovie = movieExternalService.getMovieByMovieId(movieId);

        // then
        assertThat(foundMovie).isNotNull();
        assertThat(foundMovie.getId()).isEqualTo(movieId);
    }

    @Test
    @DisplayName("영화 정보 조회 성공 테스트")
    void getMovieInfo_success() {
        // given
        Long movieId = 1L;
        Director testDirector = Director.of("테스트 감독", "KOREA", LocalDate.of(2024, 1, 1));
        Movie mockMovie = Movie.builder().title("테스트 영화").director(testDirector).build();
        ReflectionTestUtils.setField(mockMovie, "id", movieId);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));

        // when
        MovieResponseDto responseDto = movieExternalService.getMovieInfo(movieId);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.movieId()).isEqualTo(movieId);
        assertThat(responseDto.title()).isEqualTo("테스트 영화");
    }

    @Test
    @DisplayName("영화 조회 실패 - 존재하지 않는 ID")
    void getMovie_notFound_fail() {
        // given
        Long nonExistentMovieId = 999L;
        when(movieRepository.findById(nonExistentMovieId)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> movieExternalService.getMovieByMovieId(nonExistentMovieId));

        assertThat(exception.getErrorCode()).isEqualTo(MovieErrorCode.MOVIE_NOT_FOUND);
    }

    @Test
    @DisplayName("영화 목록 조회 (페이징) 성공 테스트")
    void getMovies_success() {
        // given
        Movie movie1 = Movie.builder().title("영화1").build();
        Movie movie2 = Movie.builder().title("영화2").build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> mockPage = new PageImpl<>(List.of(movie1, movie2), pageable, 2);

        when(movieRepository.findAll(pageable)).thenReturn(mockPage);

        // when
        Page<MovieListDto> resultPage = movieExternalService.getMovies(pageable);

        // then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent().get(0).title()).isEqualTo("영화1");
    }
}