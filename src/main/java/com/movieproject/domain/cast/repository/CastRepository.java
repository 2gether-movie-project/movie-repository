package com.movieproject.domain.cast.repository;

import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.cast.entity.Cast;
import com.movieproject.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CastRepository extends JpaRepository<Cast, Long> {
    boolean existsByActorAndMovie(Actor actor, Movie movie);
}
