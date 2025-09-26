package com.movieproject.domain.actor.repository;

import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.cast.entity.Cast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    boolean existsByNameAndBirthDate(String name, LocalDate birthDate);

    @Query("select a from Actor a left join fetch a.casts c left join fetch c.movie where a.actorId = :actorId")
    Optional<Actor> findByIdWithMovies(@Param("actorId") Long actorId);

    //배우 이름에 키워드가 포함된 배우가 출연한 캐스팅 기록을 페이지네이션하여 조회
    @Query(value = "SELECT c FROM Cast c JOIN c.actor a WHERE a.name LIKE %:keyword%",
            countQuery = "SELECT COUNT(c) FROM Cast c JOIN c.actor a WHERE a.name LIKE %:keyword%")
    Page<Cast> searchCastsByActor(@Param("keyword") String keyword, Pageable pageable);
}
