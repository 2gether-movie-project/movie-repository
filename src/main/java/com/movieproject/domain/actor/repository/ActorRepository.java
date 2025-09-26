package com.movieproject.domain.actor.repository;

import com.movieproject.domain.actor.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    boolean existsByNameAndBirthDate(String name, LocalDate birthDate);
}
