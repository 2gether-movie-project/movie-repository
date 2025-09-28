package com.movieproject.domain.actor.service;

import com.movieproject.common.exception.GlobalException;
import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.exception.ActorErrorCode;
import com.movieproject.domain.actor.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActorExternalService {

    private final ActorRepository actorRepository;

    public Actor findActorById(Long actorId) {
        return actorRepository.findById(actorId)
                .orElseThrow(() -> new GlobalException(ActorErrorCode.ACTOR_NOT_FOUND));
    }
}
