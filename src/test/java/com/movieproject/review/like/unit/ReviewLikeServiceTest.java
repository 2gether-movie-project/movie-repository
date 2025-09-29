package com.movieproject.review.like.unit;

import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.review.like.entity.ReviewLike;
import com.movieproject.domain.review.like.exception.ReviewLikeErrorCode;
import com.movieproject.domain.review.like.exception.ReviewLikeException;
import com.movieproject.domain.review.like.repository.ReviewLikeRepository;
import com.movieproject.domain.review.like.service.ReviewLikeService;
import com.movieproject.domain.review.service.ReviewExternalService;
import com.movieproject.domain.user.entity.User;
import com.movieproject.domain.user.service.external.UserExternalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewLikeServiceTest {

    @InjectMocks
    private ReviewLikeService reviewLikeService;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private ReviewExternalService reviewService;

    @Mock
    private UserExternalService userService;

    @Test
    @DisplayName("리뷰 좋아요 성공")
    void likeReview() {

        // given
        Long reviewId = 1L;
        Long userId = 2L;

        Review review = Review.builder().build();
        User user = User.builder().build();

        when(reviewService.findByReviewId(reviewId)).thenReturn(review);
        when(userService.findUserById(userId)).thenReturn(user);

        // when
        reviewLikeService.likeReview(reviewId, userId);

        // then
        assertEquals(1, review.getLikeCount());
    }

    @Test
    @DisplayName("리뷰 좋아요 실패 - 이미 좋아요한 경우")
    void likeReview_alreadyExists() {

        // given
        Long reviewId = 1L;
        Long userId = 2L;

        Review review = Review.builder().build();
        User user = User.builder().build();

        when(reviewService.findByReviewId(reviewId)).thenReturn(review);
        when(userService.findUserById(userId)).thenReturn(user);
        when(reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)).thenReturn(true);

        // when & then
        ReviewLikeException exception = assertThrows(ReviewLikeException.class,
                () -> reviewLikeService.likeReview(reviewId, userId));

        assertEquals(ReviewLikeErrorCode.REVIEW_LIKE_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 좋아요 취소 성공")
    void unlikeReview() {

        // given
        Long reviewId = 1L;
        Long userId = 2L;

        Review review = Review.builder().build();
        review.incrementLikeCount();
        User user = User.builder().build();
        ReviewLike reviewLike = ReviewLike.builder().review(review).user(user).build();

        when(reviewService.findByReviewId(reviewId)).thenReturn(review);
        when(userService.findUserById(userId)).thenReturn(user);
        when(reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)).thenReturn(Optional.of(reviewLike));

        // when
        reviewLikeService.unlikeReview(reviewId, userId);

        // then
        assertEquals(0, review.getLikeCount());
    }

    @Test
    @DisplayName("리뷰 좋아요 취소 실패 - 좋아요 기록 없음")
    void unlikeReview_notFound() {

        // given
        Long reviewId = 1L;
        Long userId = 2L;

        Review review = Review.builder().build();
        User user = User.builder().build();

        when(reviewService.findByReviewId(reviewId)).thenReturn(review);
        when(userService.findUserById(userId)).thenReturn(user);
        when(reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)).thenReturn(Optional.empty());

        // when & then
        ReviewLikeException exception = assertThrows(ReviewLikeException.class,
                () -> reviewLikeService.unlikeReview(reviewId, userId));

        assertEquals(ReviewLikeErrorCode.REVIEW_LIKE_NOT_FOUND, exception.getErrorCode());
    }
}
