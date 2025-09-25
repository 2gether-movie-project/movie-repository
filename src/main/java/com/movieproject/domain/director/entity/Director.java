package com.movieproject.domain.director.entity;

import com.movieproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Director extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long directorId;

    @Column(nullable = false)
    String name;

    String nationality;

    @Column(nullable = false)
    LocalDate birthDate;

    @Builder
    private Director(String name,
                     String nationality,
                     LocalDate birthDate) {
        this.name = name;
        this.nationality = nationality;
        this.birthDate = birthDate;
    }

    public static Director of(
              String name,
              String nationality,
              LocalDate birthDate) {
        return new Director(
                name,
                nationality,
                birthDate);
    }
}
