package com.movieproject.domain.review.repository;

import com.movieproject.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    @EntityGraph(attributePaths = "user")
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId AND r.deleted = false ORDER BY r.createdAt DESC")
    Page<Review> findAllByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    Optional<Review> findByReviewIdAndDeletedFalse(Long reviewId);

    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.movie.id = :movieId AND r.user.userId = :userId AND r.deleted = false")
    boolean existsByMovieIdAndUserId(@Param("movieId") Long movieId, @Param("userId") Long userId);

    @EntityGraph(attributePaths = "movie")
    @Query("SELECT r FROM Review r WHERE r.user.userId = :userId AND r.deleted = false ORDER BY r.createdAt DESC")
    Page<Review> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
