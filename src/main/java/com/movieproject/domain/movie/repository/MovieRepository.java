package com.movieproject.domain.movie.repository;

import com.movieproject.domain.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    //영화 제목에 특정 키워드가 포함된 영화를 페이지네이션하여 조회
    @Query("SELECT m FROM Movie m WHERE m.title LIKE %:keyword%")
    Page<Movie> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
}
