package com.movieproject.movie;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.repository.DirectorRepository;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.repository.MovieRepository;
import com.movieproject.domain.movie.service.MovieInternalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@SpringBootTest
class MovieInternalServiceCachingTest {


    @Autowired
    private MovieInternalService movieInternalService;

    @SpyBean
    private MovieRepository movieRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("movieDetailsCache").clear();
    }

    @Test
    @DisplayName("영화 상세 조회 v2 - 캐싱 동작 테스트")
    void getMovieInfoV2_caching_test() {
        // given: 테스트용 데이터 DB에 미리 저장
        Director director = directorRepository.save(Director.of("캐시용 감독", "국적", LocalDate.now()));
        Movie movieToSave = Movie.builder()
                .title("캐시 테스트 영화")
                .director(director)
                .releaseDate(LocalDate.now())
                .duration(120)
                .nationality("KOREA")
                .genre("테스트 장르")
                .build();
        Movie movie = movieRepository.save(movieToSave);
        Long movieId = movie.getMovieid();

        // when & then
        // 1. 첫 번째 호출
        System.out.println("첫 번째 호출 시작...");
        movieInternalService.getMovieInfoV2(movieId);
        verify(movieRepository, times(1)).findById(movieId); // DB 조회 검증
        System.out.println("첫 번째 호출 완료 (DB 조회 발생)");

        // 2. 두 번째 호출
        System.out.println("두 번째 호출 시작...");
        movieInternalService.getMovieInfoV2(movieId);
        verify(movieRepository, times(1)).findById(movieId); // 여전히 총 1번만 호출되었는지 검증 (캐시 사용)
        System.out.println("두 번째 호출 완료 (캐시 사용, DB 조회 없음)");
    }
}