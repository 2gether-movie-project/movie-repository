package com.movieproject.domain.review.service;

import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.service.MovieExternalService;
import com.movieproject.domain.review.dto.request.ReviewRequest;
import com.movieproject.domain.review.dto.response.ReviewResponse;
import com.movieproject.domain.review.dto.response.ReviewWithMovieResponse;
import com.movieproject.domain.review.dto.response.ReviewWithUserResponse;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.review.exception.ReviewErrorCode;
import com.movieproject.domain.review.exception.ReviewException;
import com.movieproject.domain.review.repository.ReviewRepository;
import com.movieproject.domain.user.entity.User;
import com.movieproject.domain.user.service.external.UserExternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewInternalService {

    private final ReviewRepository reviewRepository;
    private final MovieExternalService movieService;
    private final UserExternalService userService;

    @Caching(evict = {
            @CacheEvict(value = "topReviewPageCache", key = "#movieId"),
            @CacheEvict(value = "myReviewPageCache", key = "#userId")
    })
    @Transactional
    public ReviewResponse createReview(Long movieId, ReviewRequest.Create request, Long userId) {
        Movie movie = movieService.findMovieById(movieId);

        if (reviewRepository.existsByMovieIdAndUserId(movieId, userId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }

        User user = userService.findUserById(userId);

        Review review = Review.builder()
                .content(request.content())
                .rating(request.rating())
                .movie(movie)
                .user(user)
                .build();
        Review savedReview = reviewRepository.save(review);

        return ReviewResponse.from(savedReview);
    }

    @Cacheable(value = "topReviewPageCache", key = "#movieId", condition = "#pageable.pageNumber == 0")
    @Transactional(readOnly = true)
    public Page<ReviewWithUserResponse> getReviews(Long movieId, Pageable pageable) {
        movieService.existsByMovieId(movieId);

        log.info("DB에서 리뷰 조회중: movieId={}, page={}", movieId, pageable.getPageNumber()); // 로그 최초 1회만 출력 시 캐시 적용 성공

        Page<Review> reviewPage = reviewRepository.findAllByMovieId(movieId, pageable);

        return reviewPage.map(ReviewWithUserResponse::from);
    }

    @Caching(evict = {
            @CacheEvict(value = "topReviewPageCache", key = "#movieId"),
            @CacheEvict(value = "myReviewPageCache", key = "#userId")
    })
    @Transactional
    public ReviewResponse updateReview(Long movieId, Long reviewId, ReviewRequest.Update request, Long userId) {

        Review review = validateReviewAccess(movieId, reviewId, userId);
        review.updateReview(request.content(), request.rating());

        return ReviewResponse.from(review);
    }

    @Caching(evict = {
            @CacheEvict(value = "topReviewPageCache", key = "#movieId"),
            @CacheEvict(value = "myReviewPageCache", key = "#userId")
    })
    @Transactional
    public void deleteReview(Long movieId, Long reviewId, Long userId) {

        Review review = validateReviewAccess(movieId, reviewId, userId);

        review.delete();
    }

    private Review validateReviewAccess(Long movieId, Long reviewId, Long userId) {

        movieService.existsByMovieId(movieId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!review.getMovie().getMovieid().equals(movieId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_MOVIE_MISMATCH);
        }

        if (!review.getUser().getUserId().equals(userId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_FORBIDDEN);
        }

        return review;
    }

    @Cacheable(value = "myReviewPageCache", key = "#userId", condition = "#pageable.pageNumber == 0")
    @Transactional(readOnly = true)
    public Page<ReviewWithMovieResponse> getMyReviews(Long userId, Pageable pageable) {
        Page<Review> myReviewPage = reviewRepository.findAllByUserId(userId, pageable);

        return myReviewPage.map(ReviewWithMovieResponse::from);
    }
}
