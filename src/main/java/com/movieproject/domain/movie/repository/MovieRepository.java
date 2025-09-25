package com.movieproject.domain.movie.repository;

import com.movieproject.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
