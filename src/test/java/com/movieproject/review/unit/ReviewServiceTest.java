package com.movieproject.review.unit;

import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.service.MovieExternalService;
import com.movieproject.domain.review.dto.ReviewCreateRequest;
import com.movieproject.domain.review.dto.ReviewResponse;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.review.repository.ReviewRepository;
import com.movieproject.domain.review.service.ReviewInternalService;
import com.movieproject.domain.user.entity.User;
import com.movieproject.domain.user.service.UserExternalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewInternalService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieExternalService movieService;

    @Mock
    private UserExternalService userService;

    @Test
    void createReview_리뷰_생성_성공() {

        //given
        Long movieId = 1L;
        Long userId = 2L;
        ReviewCreateRequest request = new ReviewCreateRequest("리뷰 내용", new BigDecimal("4.5"));

        Movie movie = Movie.builder().title("겨울왕국").build();
        User user = User.builder().username("올라프").build();
        Review review = Review.builder()
                .content(request.content())
                .rating(request.rating())
                .movie(movie)
                .user(user)
                .build();

        when(movieService.getMovieByMovieId(movieId)).thenReturn(movie);
        when(userService.getUserById(userId)).thenReturn(user);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        //when
        ReviewResponse response = reviewService.createReview(movieId, request, userId);

        //then
        assertNotNull(response);
        assertEquals("리뷰 내용", response.content());
        assertEquals("올라프", response.user().username());
    }
}
