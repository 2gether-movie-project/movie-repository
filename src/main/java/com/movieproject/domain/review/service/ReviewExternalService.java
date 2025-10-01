package com.movieproject.domain.review.service;

import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.review.exception.ReviewErrorCode;
import com.movieproject.domain.review.exception.ReviewException;
import com.movieproject.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewExternalService {

    private final ReviewRepository reviewRepository;

    public Review findByReviewId(Long reviewId) {
        return reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }
}
