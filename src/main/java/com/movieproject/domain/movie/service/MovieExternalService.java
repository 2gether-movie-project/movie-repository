package com.movieproject.domain.movie.service;

import com.movieproject.common.exception.GlobalException;
import com.movieproject.domain.movie.dto.response.MovieListDto;
import com.movieproject.domain.movie.dto.response.MovieResponseDto;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.exception.MovieErrorCode;
import com.movieproject.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieExternalService {

    private final MovieRepository movieRepository;

    // 엔티티 반환
    public Movie getMovieByMovieId(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new GlobalException(MovieErrorCode.MOVIE_NOT_FOUND));
    }

    // DTO 반환
    public MovieResponseDto getMovieInfo(Long movieId) {
        Movie movie = getMovieByMovieId(movieId);
        return MovieResponseDto.from(movie);
    }

    public Page<MovieListDto> getMovies(Pageable pageable) {
        Page<Movie> movies = movieRepository.findAll(pageable);
        return movies.map(MovieListDto::from);
    }
}