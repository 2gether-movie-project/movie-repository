package com.movieproject.domain.review.like.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.security.SecurityUtils;
import com.movieproject.domain.review.like.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/likes")
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> likeReview(@PathVariable("reviewId") Long reviewId) {
        Long userId = SecurityUtils.getCurrentUserId();

        reviewLikeService.likeReview(reviewId, userId);

        return ApiResponse.success(null, "리뷰에 좋아요를 눌렀습니다.");
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> unlikeReview(@PathVariable("reviewId") Long reviewId) {
        Long userId = SecurityUtils.getCurrentUserId();

        reviewLikeService.unlikeReview(reviewId, userId);

        return ApiResponse.success(null, "좋아요가 취소 되었습니다.");
    }
}
