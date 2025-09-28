package com.movieproject.domain.cast.dto.response;

import com.movieproject.domain.cast.entity.Cast;
import com.movieproject.domain.cast.enums.CastRole;

public record CastResponse(
        Long id,
        String actorName,
        String movieTitle,
        CastRole role
) {
    public static CastResponse from(Cast cast) {
        return new CastResponse(
                cast.getId(),
                cast.getActor().getName(),
                cast.getMovie().getTitle(),
                cast.getRole()
        );
    }
}

