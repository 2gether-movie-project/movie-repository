package com.movieproject.domain.movie.entity;

import com.movieproject.common.entity.BaseEntity;
import com.movieproject.domain.director.entity.Director;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    @Builder
    private Movie(String title, LocalDate releaseDate, Integer duration, String nationality, String genre, Director director) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.nationality = nationality;
        this.genre = genre;
        this.rating = 0.0;
        this.director = director;
    }
}