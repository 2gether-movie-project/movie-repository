package com.movieproject.domain.actor.dto.response;

import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.cast.entity.Cast;
import com.movieproject.domain.movie.dto.response.MovieSummaryResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record ActorDetailResponse(
        Long actorId,
        String name,
        String nationality,
        LocalDate birthDate,
        List<MovieSummaryResponse> movies
) {
    public static ActorDetailResponse from(Actor actor) {
        List<MovieSummaryResponse> movies = new ArrayList<>();
        for (Cast cast : actor.getCasts()) {
            movies.add(MovieSummaryResponse.from(cast.getMovie()));
        }
        return new ActorDetailResponse(
                actor.getActorId(),
                actor.getName(),
                actor.getNationality(),
                actor.getBirthDate(),
                movies
        );
    }
}

