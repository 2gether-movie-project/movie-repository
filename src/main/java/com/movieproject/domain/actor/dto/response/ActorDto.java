package com.movieproject.domain.actor.dto.response;

import com.movieproject.domain.actor.entity.Actor;

public record ActorDto(
        Long actorId,
        String name
) {
    public static ActorDto from(Actor actor) {
        return new ActorDto(actor.getActorId(), actor.getName());
    }
}