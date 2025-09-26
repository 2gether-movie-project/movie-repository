package com.movieproject.domain.director.dto.response;

import com.movieproject.domain.director.entity.Director;

import java.time.LocalDate;

public record DirectorInfoDto(
        Long directorId,
        String name,
        String nationality,
        LocalDate birthDate
) {
    public static DirectorInfoDto from(Director director) {
        return new DirectorInfoDto(
                director.getDirectorId(),
                director.getName(),
                director.getNationality(),
                director.getBirthDate()
        );
    }
}
