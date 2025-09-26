package com.movieproject.domain.actor.service;

import ch.qos.logback.core.joran.spi.ActionException;
import com.movieproject.domain.actor.dto.request.ActorRequest;
import com.movieproject.domain.actor.dto.response.ActorResponse;
import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.exception.ActorErrorCode;
import com.movieproject.domain.actor.exception.ActorException;
import com.movieproject.domain.actor.repository.ActorRepository;
import com.movieproject.domain.director.dto.response.DirectorResponse;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.exception.DirectorErrorCode;
import com.movieproject.domain.director.exception.DirectorException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActorInternalService {

    private final ActorRepository actorRepository;

    @Transactional
    public ActorResponse createActors(@Valid ActorRequest actorRequest) {

        actorRepository.findByNameAndBirthDate(
                actorRequest.name(),
                actorRequest.birthDate()
        ).ifPresent(d -> {
            throw new ActorException(ActorErrorCode.ALREADY_EXIST_ACTOR);
        });

        Actor actor = actorRepository.save(Actor.of(actorRequest.name(), actorRequest.nationality(),  actorRequest.birthDate()));

        ActorResponse actorResponse = ActorResponse.from(actor);

        return actorResponse;
    }
}
