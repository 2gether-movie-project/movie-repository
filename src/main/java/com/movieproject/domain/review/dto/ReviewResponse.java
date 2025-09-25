package com.movieproject.domain.review.dto;

import com.movieproject.domain.movie.dto.MovieInReviewResponse;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.user.dto.UserInReviewResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        String content,
        BigDecimal rating,
        MovieInReviewResponse movie,
        UserInReviewResponse user,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getContent(),
                review.getRating(),
                MovieInReviewResponse.from(review.getMovie()),
                UserInReviewResponse.from(review.getUser()),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
