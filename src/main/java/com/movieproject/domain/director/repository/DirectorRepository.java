package com.movieproject.domain.director.repository;

import com.movieproject.domain.director.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectorRepository extends JpaRepository<Director, Long> {
}
