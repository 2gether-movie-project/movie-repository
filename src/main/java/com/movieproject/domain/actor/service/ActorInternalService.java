package com.movieproject.domain.actor.service;

import com.movieproject.domain.actor.dto.request.ActorRequest;
import com.movieproject.domain.actor.dto.response.ActorResponse;
import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.exception.ActorErrorCode;
import com.movieproject.domain.actor.exception.ActorException;
import com.movieproject.domain.actor.repository.ActorRepository;
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
