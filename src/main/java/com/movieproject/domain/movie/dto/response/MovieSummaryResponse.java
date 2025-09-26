package com.movieproject.domain.movie.dto.response;

import com.movieproject.domain.movie.entity.Movie;

import java.time.LocalDate;

//감독 상세정보에 들어갈 영화들
public record MovieSummaryResponse(
        Long movieId,
        String title,
        LocalDate releaseDate,
        String genre
) {

    public static MovieSummaryResponse from(Movie movie) {
        return new MovieSummaryResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getGenre()
        );
    }
}
