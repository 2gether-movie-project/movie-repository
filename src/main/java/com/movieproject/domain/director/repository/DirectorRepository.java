package com.movieproject.domain.director.repository;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface DirectorRepository extends JpaRepository<Director, Long> {

    Optional<Director> findByNameAndBirthDate(String name, LocalDate birthDate);


    @Query("select d from Director d left join fetch d.movies where d.directorId = :directorId")
    Optional<Director> findByIdWithMovies(@Param("directorId") Long directorId);

    //감독 이름에 키워드가 포함된 감독이 연출한 영화 목록을 페이지네이션하여 조회
    @Query(value = "SELECT m FROM Movie m JOIN m.director d WHERE d.name LIKE %:keyword%",
            countQuery = "SELECT COUNT(m) FROM Movie m JOIN m.director d WHERE d.name LIKE %:keyword%")
    Page<Movie> searchMoviesByDirector(@Param("keyword") String keyword, Pageable pageable);
}
