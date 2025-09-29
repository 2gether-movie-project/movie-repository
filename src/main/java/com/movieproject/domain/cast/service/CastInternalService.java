package com.movieproject.domain.cast.service;

import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.service.ActorExternalService;
import com.movieproject.domain.cast.dto.request.CastRequest;
import com.movieproject.domain.cast.dto.response.CastResponse;
import com.movieproject.domain.cast.entity.Cast;
import com.movieproject.domain.cast.exception.CastErrorCode;
import com.movieproject.domain.cast.exception.CastException;
import com.movieproject.domain.cast.repository.CastRepository;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.service.MovieExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CastInternalService {

    private final CastRepository castRepository;
    private final MovieExternalService movieExternalService;
    private final ActorExternalService actorExternalService;

    @Transactional
    public CastResponse createCast(CastRequest request) {
        Actor actor = actorExternalService.findActorById(request.actorId());

        Movie movie = movieExternalService.findMovieById(request.movieId());

        // 중복 체크
        if (castRepository.existsByActorAndMovie(actor, movie)) {
            throw new CastException(CastErrorCode.ALREADY_EXIST_CAST);
        }

        Cast cast = Cast.builder()
                .role(request.role())
                .actor(actor)
                .movie(movie)
                .build();

        castRepository.save(cast);

        return CastResponse.from(cast);
    }

    @Transactional(readOnly = true)
    public List<CastResponse> getCasts() {
        return castRepository.findAll().stream()
                .map(CastResponse::from)
                .toList();
    }

    @Transactional
    public void deleteCast(Long castId) {
        Cast cast = castRepository.findById(castId)
                .orElseThrow(() -> new CastException(CastErrorCode.CAST_NOT_FOUND));
        castRepository.delete(cast);
    }
}
