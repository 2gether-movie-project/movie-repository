package com.movieproject.review.unit;

import com.movieproject.common.response.PageResponse;
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
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void getReviews_리뷰_목록_조회_성공() {

        // given
        Long movieId = 1L;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(0, 10, sort);

        Movie movie = Movie.builder().title("겨울왕국").build();
        User user = User.builder().username("올라프").build();
        List<Review> reviewList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Review review = Review.builder()
                    .content(i + 1 + ". 리뷰")
                    .rating(new BigDecimal("4.5"))
                    .movie(movie)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now().minusMinutes(15 - i));
            reviewList.add(review);
        }

        reviewList.sort(Comparator.comparing(Review::getCreatedAt).reversed());
        PageImpl<Review> reviewPage = new PageImpl<>(reviewList, pageable, reviewList.size());
        when(reviewRepository.findAllByMovie_Id(movieId, pageable)).thenReturn(reviewPage);

        // when
        PageResponse<ReviewResponse> reviews = reviewService.getReviews(movieId, pageable);

        // then
        assertThat(reviews).isNotNull();
        assertThat(reviews.getContent()).hasSize(15);
        assertThat(reviews.getTotalPages()).isEqualTo(2);

        List<ReviewResponse> responses = reviews.getContent();
        for (int i = 0; i < responses.size() - 1; i++) { // 최신순 정렬 검증
            assertThat(responses.get(i).createdAt()).isAfter(responses.get(i + 1).createdAt());
        }
    }
}
