package com.movieproject.domain.movie.service;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.movie.dto.MovieRequestDto;
import com.movieproject.domain.movie.dto.MovieResponseDto;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.service.external.MovieService;
import com.movieproject.domain.movie.service.internal.MovieInternalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieInternalService movieInternalService;

    @Test
    @DisplayName("영화 등록 성공 테스트")
    void createMovie_success() {
        // given
        MovieRequestDto.Create requestDto = new MovieRequestDto.Create(
                "테스트 영화",
                LocalDate.of(2025, 1, 1),
                120,
                "KOREA",
                "테스트 장르",
                1L
        );

        Director testDirector = Director.of("테스트 감독", "KOREA",LocalDate.of(1969, 9, 14));
        ReflectionTestUtils.setField(testDirector, "directorId", 1L); // ID 설정

        Movie mockMovie = Movie.builder()
                .title(requestDto.title())
                .releaseDate(requestDto.releaseDate())
                .duration(requestDto.duration())
                .nationality(requestDto.nationality())
                .genre(requestDto.genre())
                .director(testDirector)
                .build();
        ReflectionTestUtils.setField(mockMovie, "id", 100L); // ID 설정

        when(movieInternalService.createMovie(any(MovieRequestDto.Create.class)))
                .thenReturn(mockMovie);

        // when
        MovieResponseDto responseDto = movieService.createMovie(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.movieId()).isEqualTo(100L);
        assertThat(responseDto.title()).isEqualTo("테스트 영화");
        assertThat(responseDto.director().name()).isEqualTo("테스트 감독");
        verify(movieInternalService, times(1)).createMovie(requestDto);
    }
}