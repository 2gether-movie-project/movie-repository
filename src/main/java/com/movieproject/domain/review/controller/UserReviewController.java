package com.movieproject.domain.review.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.response.PageResponse;
import com.movieproject.common.security.SecurityUtils;
import com.movieproject.domain.review.dto.response.ReviewWithMovieResponse;
import com.movieproject.domain.review.service.ReviewInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserReviewController {

    private final ReviewInternalService reviewService;

    @GetMapping("/api/users/me/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewWithMovieResponse>>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        Page<ReviewWithMovieResponse> myReviews = reviewService.getMyReviews(userId, pageable);

        return ApiResponse.pageSuccess(myReviews, "내가 작성한 리뷰 목록이 조회되었습니다.");
    }
}
