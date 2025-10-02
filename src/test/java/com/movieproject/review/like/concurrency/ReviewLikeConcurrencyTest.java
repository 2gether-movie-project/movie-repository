package com.movieproject.review.like.concurrency;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.repository.DirectorRepository;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.repository.MovieRepository;
import com.movieproject.domain.movie.service.MovieExternalService;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.review.like.repository.ReviewLikeRepository;
import com.movieproject.domain.review.like.service.ReviewLikeService;
import com.movieproject.domain.review.repository.ReviewRepository;
import com.movieproject.domain.user.entity.Role;
import com.movieproject.domain.user.entity.User;
import com.movieproject.domain.user.repository.UserRepository;
import com.movieproject.domain.user.service.external.UserExternalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class ReviewLikeConcurrencyTest {

    @Autowired
    private ReviewLikeService reviewLikeService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private UserExternalService userService;

    @Autowired
    private MovieExternalService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DirectorRepository directorRepository;

    private Long reviewId;
    private final int THREAD_COUNT = 100;
    private final List<Long> testerIdList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        Director mockDirector = Director.builder()
                .name("영화감독")
                .birthDate(LocalDate.of(1980, 1, 1))
                .build();
        directorRepository.save(mockDirector);

        Movie mockMovie = Movie.builder()
                .title("영화")
                .releaseDate(LocalDate.of(2000, 4, 2))
                .duration(132)
                .nationality("KOREA")
                .genre("ACTION")
                .director(mockDirector)
                .build();
        movieRepository.save(mockMovie);

        User mockUser = User.builder()
                .username("리뷰 작성자")
                .email("abc@gmail.com")
                .password("qwueroiweuro123!@#")
                .role(Role.USER)
                .build();
        userRepository.save(mockUser);

        Review review = Review.builder()
                .content("리뷰")
                .rating(new BigDecimal("5"))
                .movie(mockMovie)
                .user(mockUser)
                .build();
        reviewRepository.save(review);

        Movie movie = movieService.findMovieById(mockMovie.getMovieId());
        User user = userService.findUserById(mockUser.getUserId());

        reviewId = review.getReviewId();

        for (int i = 0; i < THREAD_COUNT; i++) {
            User tester = User.builder()
                    .username("tester" + i)
                    .email("tester" + i + "@gmail.com")
                    .password("sajflkasjdif123!@#")
                    .role(Role.USER)
                    .build();
            User savedUser = userRepository.save(tester);
            testerIdList.add(savedUser.getUserId());
        }
    }

    @Test
    @DisplayName("동시에 100개의 좋아요 요청을 처리")
    void reviewLikes() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);

        for (Long testerId : testerIdList) {
            executorService.execute(() -> {
               reviewLikeService.likeReview(reviewId, testerId);
               countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        Review review = reviewRepository.findById(reviewId).orElseThrow();
        long actualLikes = reviewLikeRepository.countByReview_ReviewId((reviewId));

        assertEquals(THREAD_COUNT, review.getLikeCount());
        assertEquals(THREAD_COUNT, actualLikes);
    }
}
