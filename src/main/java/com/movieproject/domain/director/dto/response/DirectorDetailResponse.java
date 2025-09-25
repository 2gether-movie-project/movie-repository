package com.movieproject.domain.director.dto.response;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.movie.entity.Movie;

import java.util.ArrayList;
import java.util.List;

public record DirectorDetailResponse(
        Long directorId,
        String name,
        String nationality,
        List<MovieSummaryResponse> movies
) {

    public static DirectorDetailResponse from(Director director) {

        List<MovieSummaryResponse> movieDtos = new ArrayList<>();
        for (Movie movie : director.getMovies()) {
            movieDtos.add(MovieSummaryResponse.from(movie));
        }

        return new DirectorDetailResponse(
                director.getDirectorId(),
                director.getName(),
                director.getNationality(),
                movieDtos
        );
    }
}
