package com.movieproject.domain.review.dto.response;

import com.movieproject.domain.movie.dto.response.MovieInReviewResponse;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.user.dto.response.UserInReviewResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewWithMovieResponse(
        Long reviewId,
        String content,
        BigDecimal rating,
        MovieInReviewResponse movie,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewWithMovieResponse from(Review review) {
        return new ReviewWithMovieResponse(
                review.getReviewId(),
                review.getContent(),
                review.getRating(),
                MovieInReviewResponse.from(review.getMovie()),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
