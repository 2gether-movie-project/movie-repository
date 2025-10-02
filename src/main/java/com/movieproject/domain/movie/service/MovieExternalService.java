package com.movieproject.domain.movie.service;

import com.movieproject.common.exception.GlobalException;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.exception.MovieErrorCode;
import com.movieproject.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieExternalService {

    private final MovieRepository movieRepository;

    public Movie findMovieById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new GlobalException(MovieErrorCode.MOVIE_NOT_FOUND));
    }

    public void existsByMovieId(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new GlobalException(MovieErrorCode.MOVIE_NOT_FOUND);
        }
    }
}
