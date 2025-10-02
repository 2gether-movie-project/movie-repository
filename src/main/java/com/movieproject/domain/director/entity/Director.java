package com.movieproject.domain.director.entity;

import com.movieproject.common.entity.BaseEntity;
import com.movieproject.domain.director.dto.request.DirectorUpdateRequest;
import com.movieproject.domain.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Director extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long directorId;

    @Column(nullable = false)
    private String name;

    private String nationality;

    @Column(nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = "director")
    private List<Movie> movies = new ArrayList<>();

    @Builder
    private Director(String name,
                     String nationality,
                     LocalDate birthDate) {
        this.name = name;
        this.nationality = nationality;
        this.birthDate = birthDate;
    }

    public static Director of(String name,
                              String nationality,
                              LocalDate birthDate) {
        return new Director(
                name,
                nationality,
                birthDate);
    }

    public void updateDirector(DirectorUpdateRequest directorUpdateRequest) {
        this.name = directorUpdateRequest.name();
        this.nationality = directorUpdateRequest.nationality();
        this.birthDate = directorUpdateRequest.birthDate();
    }
}
