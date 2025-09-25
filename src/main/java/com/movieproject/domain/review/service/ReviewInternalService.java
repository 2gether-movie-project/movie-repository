package com.movieproject.domain.review.service;

import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.service.MovieExternalService;
import com.movieproject.domain.review.dto.ReviewCreateRequest;
import com.movieproject.domain.review.dto.ReviewResponse;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.review.repository.ReviewRepository;
import com.movieproject.domain.user.entity.User;
import com.movieproject.domain.user.service.UserExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewInternalService {

    private final ReviewRepository reviewRepository;
    private final MovieExternalService movieService;
    private final UserExternalService userService;

    @Transactional
    public ReviewResponse createReview(
            Long movieId,
            ReviewCreateRequest request,
            Long userId
    ) {
        Movie movie = movieService.getMovieByMovieId(movieId);
        User user = userService.getUserById(userId);

        Review review = Review.builder()
                .content(request.content())
                .rating(request.rating())
                .movie(movie)
                .user(user)
                .build();
        Review savedReview = reviewRepository.save(review);

        return ReviewResponse.from(savedReview);
    }
}
