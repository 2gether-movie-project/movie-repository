package com.movieproject.movie;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.service.DirectorExternalService;
import com.movieproject.domain.movie.dto.MovieRequestDto;
import com.movieproject.domain.movie.dto.MovieResponseDto;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.repository.MovieRepository;
import com.movieproject.domain.movie.service.MovieInternalService;
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
class MovieInternalServiceTest {

    @InjectMocks
    private MovieInternalService movieInternalService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private DirectorExternalService directorExternalService;

    @Test
    @DisplayName("영화 등록 성공 테스트")
    void createMovie_success() {
        // given
        Long directorId = 1L;
        Long movieId = 100L;
        MovieRequestDto.Create requestDto = new MovieRequestDto.Create(
                "테스트 영화", LocalDate.of(2025, 1, 1), 120, "KOREA", "테스트 장르", directorId
        );

        Director testDirector = Director.of("테스트 감독", "KOREA", LocalDate.of(2024, 1, 1));
        ReflectionTestUtils.setField(testDirector, "directorId", directorId);

        Movie mockMovie = Movie.builder().director(testDirector).title(requestDto.title()).build();
        ReflectionTestUtils.setField(mockMovie, "id", movieId);

        // Mockito 동작 정의
        when(directorExternalService.findDirectorById(directorId)).thenReturn(testDirector);
        when(movieRepository.save(any(Movie.class))).thenReturn(mockMovie);

        // when
        MovieResponseDto responseDto = movieInternalService.createMovie(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.movieId()).isEqualTo(movieId);
        assertThat(responseDto.title()).isEqualTo("테스트 영화");
        verify(directorExternalService, times(1)).findDirectorById(directorId);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }
}