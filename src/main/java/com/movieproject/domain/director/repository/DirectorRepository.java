package com.movieproject.domain.director.repository;

import com.movieproject.domain.director.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DirectorRepository extends JpaRepository<Director, Long> {

    Optional<Director> findByNameAndBirthDate(String name, LocalDate birthDate);


    @Query("select d from Director d left join fetch d.movies where d.directorId = :directorId")
    Optional<Director> findByIdWithMovies(@Param("directorId") Long directorId);

}
