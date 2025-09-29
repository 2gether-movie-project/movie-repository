package com.movieproject.domain.movie.dto.response;

import com.movieproject.domain.actor.dto.response.ActorDto;
import com.movieproject.domain.director.dto.response.DirectorDto;
import com.movieproject.domain.movie.entity.Movie;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record MovieResponseDto(
        Long movieId,
        String title,
        LocalDate releaseDate,
        Integer duration,
        String nationality,
        String genre,
        Double rating,
        DirectorDto director,
        List<ActorDto> cast
) {
    public static MovieResponseDto from(Movie movie) {
        List<ActorDto> castDtos = movie.getCastMembers().stream()
                .map(cast -> ActorDto.from(cast.getActor()))
                .collect(Collectors.toList());

        return new MovieResponseDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getDuration(),
                movie.getNationality(),
                movie.getGenre(),
                movie.getRating(),
                DirectorDto.from(movie.getDirector()),
                castDtos
        );
    }
}