package com.movieproject.review.unit;

import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.service.MovieExternalService;
import com.movieproject.domain.review.dto.request.ReviewRequest;
import com.movieproject.domain.review.dto.response.ReviewResponse;
import com.movieproject.domain.review.dto.response.ReviewWithMovieResponse;
import com.movieproject.domain.review.dto.response.ReviewWithUserResponse;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.review.exception.ReviewException;
import com.movieproject.domain.review.repository.ReviewRepository;
import com.movieproject.domain.review.service.ReviewInternalService;
import com.movieproject.domain.user.entity.User;
import com.movieproject.domain.user.service.external.UserExternalService;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    @DisplayName("리뷰 생성 성공")
    void createReview() {

        //given
        Long movieId = 1L;
        Long userId = 2L;
        ReviewRequest.Create request = new ReviewRequest.Create("리뷰 내용", new BigDecimal("4.5"));

        Movie movie = Movie.builder().title("겨울왕국").build();
        User user = User.builder().username("올라프").build();
        Review review = Review.builder()
                .content(request.content())
                .rating(request.rating())
                .movie(movie)
                .user(user)
                .build();

        when(movieService.findMovieById(movieId)).thenReturn(movie);
        when(userService.findUserById(userId)).thenReturn(user);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        //when
        ReviewResponse response = reviewService.createReview(movieId, request, userId);

        //then
        assertNotNull(response);
        assertEquals("리뷰 내용", response.content());
        assertEquals("올라프", response.user().username());
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공")
    void getReviews() {

        // given
        Long movieId = 1L;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(0, 10, sort);

        Movie movie = Movie.builder().title("겨울왕국").build();
        User user = User.builder().username("올라프").build();
        List<Review> reviewList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Review review = Review.builder()
                    .content((i + 1) + ". 리뷰")
                    .rating(new BigDecimal("4.5"))
                    .movie(movie)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now().minusMinutes(15 - i));
            reviewList.add(review);
        }

        reviewList.sort(Comparator.comparing(Review::getCreatedAt).reversed());
        PageImpl<Review> reviewPage = new PageImpl<>(reviewList, pageable, reviewList.size());
        when(reviewRepository.findAllByMovieId(movieId, pageable)).thenReturn(reviewPage);

        // when
        Page<ReviewWithUserResponse> reviews = reviewService.getReviews(movieId, pageable);

        // then
        assertThat(reviews).isNotNull();
        assertThat(reviews.getContent()).hasSize(15);
        assertThat(reviews.getTotalPages()).isEqualTo(2);

        List<ReviewWithUserResponse> responses = reviews.getContent();
        for (int i = 0; i < responses.size() - 1; i++) { // 최신순 정렬 검증
            assertThat(responses.get(i).createdAt()).isAfter(responses.get(i + 1).createdAt());
        }
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview() {

        // given
        Long movieId = 1L;
        Long userId = 2L;
        Long reviewId = 3L;
        ReviewRequest.Update request = new ReviewRequest.Update("수정 내용", new BigDecimal("4.9"));

        Movie movie = Movie.builder().title("겨울왕국").build();
        ReflectionTestUtils.setField(movie, "id", movieId);
        User user = User.builder().username("올라프").build();
        ReflectionTestUtils.setField(user, "userId", userId);
        Review review = Review.builder()
                .content(request.content())
                .rating(request.rating())
                .movie(movie)
                .user(user)
                .build();
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        // when
        ReviewResponse response = reviewService.updateReview(movieId, reviewId, request, userId);

        // then
        assertNotNull(response);
        assertEquals("수정 내용", response.content());
        assertEquals(new BigDecimal("4.9"), response.rating());
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 리뷰가 존재하지 않는 경우")
    void updateReview_ReviewNotFound() {

        // given
        ReviewRequest.Update request = new ReviewRequest.Update("수정 내용", new BigDecimal("4.9"));

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ReviewException.class,
                () -> reviewService.updateReview(1L, 2L, request, 3L));
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 작성자가 아닌 경우")
    void updateReview_UserIsNotAuthor() {

        // given
        Long movieId = 1L;
        Long userId = 2L;
        Long reviewId = 3L;
        ReviewRequest.Update request = new ReviewRequest.Update("수정 내용", new BigDecimal("4.9"));

        Movie movie = Movie.builder().title("겨울왕국").build();
        ReflectionTestUtils.setField(movie, "id", movieId);
        User user = User.builder().username("올라프").build();
        ReflectionTestUtils.setField(user, "userId", 100L);
        Review review = Review.builder()
                .content("리뷰 내용")
                .rating(new BigDecimal("4.5"))
                .movie(movie)
                .user(user)
                .build();
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        // when & then
        assertThrows(ReviewException.class,
                () -> reviewService.updateReview(movieId, reviewId, request, userId));
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview() {

        // given
        Long movieId = 1L;
        Long userId = 2L;
        Long reviewId = 3L;

        Movie movie = Movie.builder().title("겨울왕국").build();
        ReflectionTestUtils.setField(movie, "id", movieId);
        User user = User.builder().username("올라프").build();
        ReflectionTestUtils.setField(user, "userId", userId);
        Review review = Review.builder()
                .content("리뷰 내용")
                .rating(new BigDecimal("4.5"))
                .movie(movie)
                .user(user)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when
        reviewService.deleteReview(movieId, reviewId, userId);

        // then
        assertTrue(review.isDeleted());
        assertNotNull(review.getDeletedAt());
    }

    @Test
    @DisplayName("내가 작성한 리뷰 목록 조회 성공")
    void getMyReviews() {

        // given
        Long userId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        Movie movie = Movie.builder().title("겨울왕국").build();
        User user = User.builder().username("올라프").build();

        List<Review> reviewList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Review review = Review.builder()
                    .content((i + 1) + ". 리뷰")
                    .rating(new BigDecimal("4.0"))
                    .movie(movie)
                    .user(user)
                    .build();
            reviewList.add(review);
        }

        PageImpl<Review> reviewPage = new PageImpl<>(reviewList, pageable, reviewList.size());

        when(reviewRepository.findAllByUserId(userId, pageable)).thenReturn(reviewPage);

        // when
        Page<ReviewWithMovieResponse> result = reviewService.getMyReviews(userId, pageable);

        // then
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        assertEquals("3. 리뷰", (result.getContent().get(2)).content());
    }
}
