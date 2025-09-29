package com.movieproject.domain.review.entity;

import com.movieproject.common.entity.BaseEntity;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private BigDecimal rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int likeCount = 0;

    @Builder
    public Review(String content, BigDecimal rating, Movie movie, User user) {
        this.content = content;
        this.rating = rating;
        this.movie = movie;
        this.user = user;
    }

    public void updateReview(String content, BigDecimal rating) {
        this.content = content;
        if (rating != null) {
            this.rating = rating;
        }
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }
}
