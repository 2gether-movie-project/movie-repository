package com.movieproject.domain.actor.dto.response;


import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.director.entity.Director;

import java.time.LocalDate;

public record ActorResponse(
        Long actorId,
        String name,
        String nationality,
        LocalDate birthDate
) {
    public static ActorResponse from(Actor actor) {
        return new ActorResponse(
                actor.getActorId(),
                actor.getName(),
                actor.getNationality(),
                actor.getBirthDate()
        );
    }
}
