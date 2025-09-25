package com.movieproject.domain.movie.dto;

import com.movieproject.domain.director.dto.DirectorDto;
import com.movieproject.domain.movie.entity.Movie;
import java.time.LocalDate;

public record MovieResponseDto(
        Long movieId,
        String title,
        LocalDate releaseDate,
        Integer duration,
        String nationality,
        String genre,
        Double rating,
        DirectorDto director
) {
    public static MovieResponseDto from(Movie movie) {
        return new MovieResponseDto(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getDuration(),
                movie.getNationality(),
                movie.getGenre(),
                movie.getRating(),
                DirectorDto.from(movie.getDirector())
        );
    }
}