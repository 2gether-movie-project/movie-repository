package com.movieproject.domain.movie.dto.response;

import com.movieproject.domain.movie.entity.Movie;

public record MovieInReviewResponse(
        Long movieId,
        String title
) {
    public static MovieInReviewResponse from(Movie movie) {
        return new MovieInReviewResponse(
                movie.getMovieId(),
                movie.getTitle()
        );
    }
}