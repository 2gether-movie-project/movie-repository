package com.movieproject.domain.cast.entity;

import com.movieproject.common.entity.BaseEntity;
import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.cast.enums.CastRole;
import com.movieproject.domain.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "cast",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"movie_id", "actor_id"})
        }
) //대리키 방식을 유지하면서 유니크 제약조건을 추가하는것!!
public class Cast extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CastRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private Actor actor;

    @Builder
    private Cast(CastRole role, Movie movie, Actor actor) {
        this.role = role;
        this.movie = movie;
        this.actor = actor;
    }
}