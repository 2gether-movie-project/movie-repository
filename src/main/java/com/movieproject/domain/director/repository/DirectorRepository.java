package com.movieproject.domain.director.repository;

import com.movieproject.domain.director.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    Optional<Director> findByNameAndBirthDate(String name, LocalDate birthDate);
}
