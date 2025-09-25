package com.movieproject.movie;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.service.DirectorExternalService;
import com.movieproject.domain.movie.dto.MovieRequestDto;
import com.movieproject.domain.movie.dto.MovieResponseDto;
import com.movieproject.domain.movie.entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieExternalService {

    private final MovieInternalService movieInternalService;
    private final DirectorExternalService directorExternalService;

    @Transactional
    public MovieResponseDto createMovie(MovieRequestDto.Create requestDto) {
        Director director = directorExternalService.findDirectorById(requestDto.directorId());
        Movie movie = movieInternalService.createMovie(requestDto, director);
        return MovieResponseDto.from(movie);
    }

    public Movie getMovieByMovieId(Long movieId) {
        return movieInternalService.findMovieById(movieId);
    }
}