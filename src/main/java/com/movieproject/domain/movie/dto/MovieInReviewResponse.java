package com.movieproject.domain.movie.dto;

import com.movieproject.domain.movie.entity.Movie;

public record MovieInReviewResponse(
        Long movieId,
        String title
) {
    public static MovieInReviewResponse from(Movie movie) {
        return new MovieInReviewResponse(
                movie.getId(),
                movie.getTitle()
        );
    }
}