package com.movieproject.domain.review.dto.response;

import com.movieproject.domain.movie.dto.response.MovieInReviewResponse;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.user.dto.response.UserInReviewResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewWithUserResponse(
        Long reviewId,
        String content,
        BigDecimal rating,
        UserInReviewResponseDto user,
        int likeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewWithUserResponse from(Review review) {
        return new ReviewWithUserResponse(
                review.getReviewId(),
                review.getContent(),
                review.getRating(),
                UserInReviewResponseDto.from(review.getUser()),
                review.getLikeCount(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
