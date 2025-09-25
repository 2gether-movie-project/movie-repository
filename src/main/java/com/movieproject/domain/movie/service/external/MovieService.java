package com.movieproject.domain.movie.service.external;

import com.movieproject.domain.movie.dto.MovieRequestDto;
import com.movieproject.domain.movie.dto.MovieResponseDto;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.service.internal.MovieInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieInternalService movieInternalService;

    public MovieResponseDto createMovie(MovieRequestDto.Create requestDto) {
        Movie movie = movieInternalService.createMovie(requestDto);

        return MovieResponseDto.from(movie);
    }
}