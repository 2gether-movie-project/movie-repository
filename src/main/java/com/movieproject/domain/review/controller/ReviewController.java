package com.movieproject.domain.review.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.domain.review.dto.ReviewCreateRequest;
import com.movieproject.domain.review.dto.ReviewResponse;
import com.movieproject.domain.review.service.ReviewInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies/{movieId}/reviews")
public class ReviewController {

    private final ReviewInternalService reviewInternalService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewCreateRequest request,
            Long userId // 추후 로그인한 사용자로부터 가져올 예정
    ) {
        ReviewResponse review = reviewInternalService.createReview(movieId, request, userId);
        return ApiResponse.created(review, "리뷰가 성공적으로 작성되었습니다.");
    }
}
