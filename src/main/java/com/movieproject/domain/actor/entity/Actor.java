package com.movieproject.domain.actor.entity;

import com.movieproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Actor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actorId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nationality;

    @Builder
    private Actor(String name, String nationality) {
        this.name = name;
        this.nationality = nationality;
    }
}