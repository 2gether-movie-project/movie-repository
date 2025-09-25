package com.movieproject.domain.movie.service.internal;

import com.movieproject.common.exception.GlobalException;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.service.DirectorExternalService;
import com.movieproject.domain.movie.dto.MovieRequestDto;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.exception.MovieErrorCode;
import com.movieproject.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieInternalService {

    private final MovieRepository movieRepository;
    private final DirectorExternalService directorExternalService;

    @Transactional
    public Movie createMovie(MovieRequestDto.Create requestDto) {

        Director director = directorExternalService.findDirectorById(requestDto.directorId());

        Movie movie = Movie.builder()
                .title(requestDto.title())
                .releaseDate(requestDto.releaseDate())
                .duration(requestDto.duration())
                .nationality(requestDto.nationality())
                .genre(requestDto.genre())
                .director(director)
                .build();

        return movieRepository.save(movie);
    }

    /**
     * ID로 영화 엔티티를 조회하는 내부 메서드
     * @param movieId 조회할 영화의 ID
     * @return 찾아낸 Movie 엔티티
     * @throws GlobalException 해당 ID의 영화가 없을 경우 발생
     */
    public Movie getMovieByMovieId(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new GlobalException(MovieErrorCode.MOVIE_NOT_FOUND));
    }
}