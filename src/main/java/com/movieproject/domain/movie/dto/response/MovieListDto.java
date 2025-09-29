package com.movieproject.domain.movie.dto.response;

import com.movieproject.domain.movie.entity.Movie;

import java.time.LocalDate;

public record MovieListDto(
        Long movieId,
        String title,
        LocalDate releaseDate,
        Double rating
) {
    public static MovieListDto from(Movie movie) {
        return new MovieListDto(
                movie.getMovieid(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getRating()
        );
    }
}
