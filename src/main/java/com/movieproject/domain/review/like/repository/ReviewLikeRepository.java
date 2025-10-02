package com.movieproject.domain.review.like.repository;

import com.movieproject.domain.review.like.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    @Query("SELECT COUNT(rl) > 0 FROM ReviewLike rl WHERE rl.review.reviewId = :reviewId AND rl.user.userId = :userId")
    boolean existsByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    @Query("SELECT rl FROM ReviewLike rl WHERE rl.review.reviewId = :reviewId AND rl.user.userId = :userId")
    Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long userId);

    long countByReview_ReviewId(Long reviewId);
}
