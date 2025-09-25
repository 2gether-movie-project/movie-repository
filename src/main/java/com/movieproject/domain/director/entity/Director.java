package com.movieproject.domain.director.entity;

import com.movieproject.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Director extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long directorId;

    @NotBlank
    String name;

    String nationality;

    @NotNull
    LocalDate birthDate;

    private Director(String name, String nationality, LocalDate birthDate) {
        this.name = name;
        this.nationality = nationality;
        this.birthDate = birthDate;
    }

    public static Director of(String name, String nationality, LocalDate birthDate) {
        return new Director(name, nationality, birthDate);
    }
}
