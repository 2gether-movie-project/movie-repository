package com.movieproject.movie;

import com.movieproject.common.exception.GlobalException;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.service.DirectorExternalService;
import com.movieproject.domain.movie.dto.request.MovieRequestDto;
import com.movieproject.domain.movie.dto.response.MovieResponseDto;
import com.movieproject.domain.movie.dto.response.MovieListDto;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.exception.MovieErrorCode;
import com.movieproject.domain.movie.repository.MovieRepository;
import com.movieproject.domain.movie.service.MovieInternalService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        MovieRequestDto.Create requestDto = new MovieRequestDto.Create(
                "테스트 영화", LocalDate.of(2025, 1, 1), 120, "KOREA", "테스트 장르", directorId
        );
        Director testDirector = Director.of("테스트 감독", "KOREA", LocalDate.now());
        Movie mockMovie = Movie.builder().director(testDirector).title(requestDto.title()).build();

        when(directorExternalService.findDirectorById(directorId)).thenReturn(testDirector);
        when(movieRepository.save(any(Movie.class))).thenReturn(mockMovie);

        // when
        MovieResponseDto responseDto = movieInternalService.createMovie(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.title()).isEqualTo("테스트 영화");
    }

    @Test
    @DisplayName("영화 상세 정보 조회 성공 테스트")
    void getMovieInfo_success() {
        // given
        Long movieId = 1L;
        Director testDirector = Director.of("테스트 감독", "KOREA", LocalDate.now());
        Movie mockMovie = Movie.builder().title("테스트 영화").director(testDirector).build();
        ReflectionTestUtils.setField(mockMovie, "id", movieId);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));

        // when
        MovieResponseDto responseDto = movieInternalService.getMovieInfo(movieId);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.movieId()).isEqualTo(movieId);
        assertThat(responseDto.title()).isEqualTo("테스트 영화");
    }

    @Test
    @DisplayName("영화 상세 정보 조회(v2) 성공 테스트")
    void getMovieInfoV2_success() {
        // given
        Long movieId = 1L;
        Director testDirector = Director.of("테스트 감독", "KOREA", LocalDate.now());
        Movie mockMovie = Movie.builder().title("테스트 영화").director(testDirector).build();
        ReflectionTestUtils.setField(mockMovie, "id", movieId);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));

        // when
        MovieResponseDto responseDto = movieInternalService.getMovieInfoV2(movieId);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.movieId()).isEqualTo(movieId);
        assertThat(responseDto.title()).isEqualTo("테스트 영화");
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
        Page<MovieListDto> resultPage = movieInternalService.getMovies(pageable);

        // then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        assertThat(resultPage.getContent().get(0).title()).isEqualTo("영화1");
    }

    @Test
    @DisplayName("영화 수정 성공 테스트")
    void updateMovie_success() {
        // given
        Long movieId = 1L;
        MovieRequestDto.Update requestDto = new MovieRequestDto.Update(
                "수정된 영화 제목",
                LocalDate.of(2025, 12, 25),
                150,
                "USA",
                "수정된 장르"
        );

        Director director = Director.of("기존 감독", "KOREA", LocalDate.now());
        Movie existingMovie = Movie.builder().title("원본 영화").director(director).build();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));

        // when
        MovieResponseDto responseDto = movieInternalService.updateMovie(movieId, requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.title()).isEqualTo("수정된 영화 제목");
        assertThat(responseDto.duration()).isEqualTo(150);
        assertThat(responseDto.genre()).isEqualTo("수정된 장르");
    }

    @Test
    @DisplayName("영화 삭제 성공 테스트")
    void deleteMovie_softDelete_success() {
        // given
        Long movieId = 1L;

        Director director = Director.of("감독", "국적", LocalDate.now());
        Movie realMovie = Movie.builder()
                .title("테스트 영화")
                .director(director)
                .build();

        assertThat(realMovie.isDeleted()).isFalse();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(realMovie));

        // when
        movieInternalService.deleteMovie(movieId);

        // then
        verify(movieRepository, times(1)).findById(movieId);

        assertThat(realMovie.isDeleted()).isTrue();
        assertThat(realMovie.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 영화 삭제 시 예외 발생 테스트")
    void deleteMovie_notFound_fail() {
        // given
        Long nonExistentMovieId = 999L;
        when(movieRepository.findById(nonExistentMovieId)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            movieInternalService.deleteMovie(nonExistentMovieId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(MovieErrorCode.MOVIE_NOT_FOUND);
    }
}