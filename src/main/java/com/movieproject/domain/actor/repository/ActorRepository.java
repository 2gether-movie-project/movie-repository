package com.movieproject.domain.actor.repository;

import com.movieproject.domain.actor.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    boolean existsByNameAndBirthDate(String name, LocalDate birthDate);

    @Query("select a from Actor a left join fetch a.casts c left join fetch c.movie where a.actorId = :actorId")
    Optional<Actor> findByIdWithMovies(@Param("actorId") Long actorId);
}
