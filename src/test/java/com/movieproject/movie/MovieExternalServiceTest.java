package com.movieproject.movie;

import com.movieproject.common.exception.GlobalException;
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
import org.springframework.test.util.ReflectionTestUtils;

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
    @DisplayName("[엔티티 반환] 영화 조회 성공 테스트")
    void findMovieById_success() {
        // given
        Long movieId = 1L;
        Movie mockMovie = Movie.builder().build();
        ReflectionTestUtils.setField(mockMovie, "id", movieId);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));

        // when
        Movie foundMovie = movieExternalService.findMovieById(movieId);

        // then
        assertThat(foundMovie).isNotNull();
        assertThat(foundMovie.getId()).isEqualTo(movieId);
    }

    @Test
    @DisplayName("영화 조회 실패 - 존재하지 않는 ID")
    void findMovieById_notFound_fail() {
        // given
        Long nonExistentMovieId = 999L;
        when(movieRepository.findById(nonExistentMovieId)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            movieExternalService.findMovieById(nonExistentMovieId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(MovieErrorCode.MOVIE_NOT_FOUND);
    }
}