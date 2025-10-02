package com.movieproject.domain.review.like.service;

import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.review.like.entity.ReviewLike;
import com.movieproject.domain.review.like.exception.ReviewLikeErrorCode;
import com.movieproject.domain.review.like.exception.ReviewLikeException;
import com.movieproject.domain.review.like.repository.ReviewLikeRepository;
import com.movieproject.domain.review.service.ReviewExternalService;
import com.movieproject.domain.user.entity.User;
import com.movieproject.domain.user.service.external.UserExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewExternalService reviewService;
    private final UserExternalService userService;

    @Caching(evict = {
            @CacheEvict(value = "topReviewPageCache", allEntries = true),
            @CacheEvict(value = "myReviewPageCache", key = "#userId")
    })
    @Transactional
    public void likeReview(Long reviewId, Long userId) {
        Review review = reviewService.findByReviewId(reviewId);
        User user = userService.findUserById(userId);

        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new ReviewLikeException(ReviewLikeErrorCode.REVIEW_LIKE_ALREADY_EXISTS);
        }

        reviewLikeRepository.save(ReviewLike.builder()
                .review(review)
                .user(user)
                .build()
        );

        review.incrementLikeCount();
    }

    @Caching(evict = {
            @CacheEvict(value = "topReviewPageCache", allEntries = true),
            @CacheEvict(value = "myReviewPageCache", key = "#userId")
    })
    @Transactional
    public void unlikeReview(Long reviewId, Long userId) {
        Review review = reviewService.findByReviewId(reviewId);
        User user = userService.findUserById(userId);

        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ReviewLikeException(ReviewLikeErrorCode.REVIEW_LIKE_NOT_FOUND));
        reviewLikeRepository.delete(reviewLike);

        review.decrementLikeCount();
    }
}
