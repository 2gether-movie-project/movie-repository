package com.movieproject.review.unit;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.repository.DirectorRepository;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.review.entity.Review;
import com.movieproject.domain.review.repository.ReviewRepository;
import com.movieproject.domain.user.entity.Role;
import com.movieproject.domain.user.entity.User;
import com.movieproject.domain.movie.repository.MovieRepository;
import com.movieproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Test
    @DisplayName("영화 ID로 리뷰 목록 조회")
    void findAllByMovieId() {

        // given
        Movie movie = createMovie();
        User user = createUser();
        Review review = reviewRepository.save(createReview(movie, user));

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<Review> reviewPage = reviewRepository.findAllByMovieId(movie.getId(), pageable);

        // then
        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getContent()).hasSize(1);
        assertThat(reviewPage.getContent().get(0).getContent()).isEqualTo("최고의 영화!");
    }

    @Test
    @DisplayName("삭제된 리뷰 조회 여부 확인")
    void findAllByMovieId_NotIncludeDeletedReview() {

        // given
        Movie movie = createMovie();
        User user = createUser();
        Review review = reviewRepository.save(createReview(movie, user));
        review.delete();

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<Review> reviewPage = reviewRepository.findAllByMovieId(movie.getId(), pageable);

        // then
        assertThat(reviewPage.getContent()).isEmpty();
    }

    @Test
    @DisplayName("영화 ID와 사용자 ID로 리뷰 존재 여부 확인")
    void existsByMovieIdAndUserId() {

        // given
        Movie movie = createMovie();
        User user = createUser();
        Review review = reviewRepository.save(createReview(movie, user));

        // when
        boolean exists = reviewRepository.existsByMovieIdAndUserId(movie.getId(), user.getUserId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("사용자 ID로 리뷰 목록 조회")
    void findAllByUserId() {

        // given
        Movie movie = createMovie();
        User user = createUser();
        Review review = reviewRepository.save(createReview(movie, user));

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<Review> reviewPage = reviewRepository.findAllByUserId(user.getUserId(), pageable);

        // then
        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getContent()).hasSize(1);
        assertThat(reviewPage.getContent().get(0).getUser().getUsername()).isEqualTo("김철수");
    }

    private User createUser() {

        User user = User.builder()
                .username("김철수")
                .email("john@example.com")
                .password("abcd1234!@#$")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

        return userRepository.save(user);
    }

    private Director createDirector() {

        Director director = Director.builder()
                .name("크리스 벅")
                .birthDate(LocalDate.of(1960, 10, 25))
                .build();
        ReflectionTestUtils.setField(director, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(director, "updatedAt", LocalDateTime.now());

        return directorRepository.save(director);
    }

    private Movie createMovie() {

        Movie movie = Movie.builder()
                .title("겨울왕국")
                .releaseDate(LocalDate.of(2013, 11, 27))
                .duration(102)
                .nationality("미국")
                .genre("애니메이션")
                .director(createDirector())
                .build();
        ReflectionTestUtils.setField(movie, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(movie, "updatedAt", LocalDateTime.now());

        return movieRepository.save(movie);
    }

    private Review createReview(Movie movie, User user) {

        Review review = Review.builder()
                .content("최고의 영화!")
                .rating(new BigDecimal("4.9"))
                .movie(movie)
                .user(user)
                .build();
        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(review, "updatedAt", LocalDateTime.now());

        return review;
    }
}
