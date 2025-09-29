package com.movieproject.domain.actor.entity;

import com.movieproject.common.entity.BaseEntity;
import com.movieproject.domain.actor.dto.request.ActorUpdateRequest;
import com.movieproject.domain.cast.entity.Cast;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLRestriction("is_deleted = false")
public class Actor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actorId;

    @Column(nullable = false)
    private String name;

    private String nationality;

    @Column(nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = "actor")
    private List<Cast> casts = new ArrayList<>();

    @Builder
    private Actor(String name, String nationality, LocalDate birthDate) {
        this.name = name;
        this.nationality = nationality;
        this.birthDate = birthDate;
    }

    public static Actor of(String name, String nationality, LocalDate birthDate) {
        return new Actor(name, nationality, birthDate);
    }

    public void updateActor(ActorUpdateRequest actorUpdateRequest) {
        this.name = actorUpdateRequest.name();
        this.nationality = actorUpdateRequest.nationality();
        this.birthDate = actorUpdateRequest.birthDate();
    }
}
