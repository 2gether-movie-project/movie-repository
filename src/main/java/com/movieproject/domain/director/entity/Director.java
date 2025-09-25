package com.movieproject.domain.director.entity;

import com.movieproject.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Director extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long directorId;

    @NotBlank
    String name;

    String nationality;

    private Director(String name, String nationality) {
        this.name = name;
        this.nationality = nationality;
    }

    // 정적 팩토리 메서드
    public static Director of(String name, String nationality) {
        return new Director(name, nationality);
    }
}