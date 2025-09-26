package com.movieproject.domain.review.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.review.dto.request.ReviewRequest;
import com.movieproject.domain.review.dto.response.ReviewResponse;
import com.movieproject.domain.review.dto.response.ReviewWithUserResponse;
import com.movieproject.domain.review.service.ReviewInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies/{movieId}/reviews")
public class ReviewController {

    private final ReviewInternalService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewRequest.Create request,
            Long userId
    ) {
        ReviewResponse savedReview = reviewService.createReview(movieId, request, userId);

        return ApiResponse.created(savedReview, "리뷰가 성공적으로 작성되었습니다.");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReviewWithUserResponse>>> getReviews(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ReviewWithUserResponse> reviews = reviewService.getReviews(movieId, pageable);

        return ApiResponse.pageSuccess(reviews, "리뷰 목록이 조회되었습니다.");
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long movieId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest.Update request,
            Long userId
            ) {
        ReviewResponse updatedReview = reviewService.updateReview(movieId, reviewId, request, userId);

        return ApiResponse.success(updatedReview, "리뷰가 수정되었습니다.");
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long movieId,
            @PathVariable Long reviewId,
            Long userId
    ) {
        reviewService.deleteReview(movieId, reviewId, userId);

        return ApiResponse.deleteSuccess("리뷰가 삭제되었습니다.");
    }
}
