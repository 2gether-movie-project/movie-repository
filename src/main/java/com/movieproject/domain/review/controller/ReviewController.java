package com.movieproject.domain.review.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.review.dto.ReviewCreateRequest;
import com.movieproject.domain.review.dto.ReviewResponse;
import com.movieproject.domain.review.service.ReviewInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies/{movieId}/reviews")
public class ReviewController {

    private final ReviewInternalService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewCreateRequest request,
            Long userId // 추후 로그인한 사용자로부터 가져올 예정
    ) {
        ReviewResponse review = reviewService.createReview(movieId, request, userId);
        return ApiResponse.created(review, "리뷰가 성공적으로 작성되었습니다.");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviews(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ReviewResponse> reviews = reviewService.getReviews(movieId, pageable);

        return ApiResponse.success(reviews, "리뷰 목록이 조회되었습니다.");
    }
}
