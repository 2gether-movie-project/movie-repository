package com.movieproject.domain.review.dto.response;

import com.movieproject.domain.movie.dto.response.MovieInReviewResponse;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.user.dto.response.UserInReviewResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        String content,
        BigDecimal rating,
        MovieInReviewResponse movie,
        UserInReviewResponseDto user,
        int likeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getContent(),
                review.getRating(),
                MovieInReviewResponse.from(review.getMovie()),
                UserInReviewResponseDto.from(review.getUser()),
                review.getLikeCount(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
