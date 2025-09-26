package com.movieproject.domain.actor.service;

import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.actor.dto.request.ActorRequest;
import com.movieproject.domain.actor.dto.request.ActorUpdateRequest;
import com.movieproject.domain.actor.dto.response.ActorDetailResponse;
import com.movieproject.domain.actor.dto.response.ActorResponse;
import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.exception.ActorErrorCode;
import com.movieproject.domain.actor.exception.ActorException;
import com.movieproject.domain.actor.repository.ActorRepository;
import com.movieproject.domain.director.exception.DirectorException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActorInternalService {

    private final ActorRepository actorRepository;

    @Transactional
    public ActorResponse createActors(@Valid ActorRequest actorRequest) {

        if (actorRepository.existsByNameAndBirthDate(actorRequest.name(), actorRequest.birthDate())) {
            throw new ActorException(ActorErrorCode.ALREADY_EXIST_ACTOR);
        }


        Actor actor = actorRepository.save(Actor.of(actorRequest.name(), actorRequest.nationality(), actorRequest.birthDate()));

        ActorResponse actorResponse = ActorResponse.from(actor);

        return actorResponse;
    }

    @Transactional(readOnly = true)
    public PageResponse<ActorResponse> getActors(Pageable pageable) {

        Page<Actor> page = actorRepository.findAll(pageable);

        List<ActorResponse> responses = new ArrayList<>();
        for (Actor actor : page.getContent()) {
            responses.add(ActorResponse.from(actor));
        }

        Page<ActorResponse> dtoPage = new PageImpl<>(responses, pageable, page.getTotalElements());
        return PageResponse.fromPage(dtoPage);
    }

    @Transactional(readOnly = true)
    public ActorDetailResponse getActorDetail(Long actorId) {
        Actor actor = actorRepository.findByIdWithMovies(actorId)
                .orElseThrow(() -> new ActorException(ActorErrorCode.ACTOR_NOT_FOUND));

        ActorDetailResponse actorDetailResponse = ActorDetailResponse.from(actor);

        return actorDetailResponse;
    }

    @Transactional
    public ActorResponse updateActor(Long actorId, ActorUpdateRequest actorUpdateRequest) {

        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new DirectorException(ActorErrorCode.ACTOR_NOT_FOUND));

        actor.updateActor(actorUpdateRequest);

        ActorResponse actorResponse = ActorResponse.from(actor);

        return actorResponse;

    }
}
