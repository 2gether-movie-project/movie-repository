package com.movieproject.domain.director.dto.response;

import com.movieproject.domain.director.entity.Director;

import java.time.LocalDate;

public record DirectorResponse(
        Long directorId,
        String name,
        String nationality,
        LocalDate birthDate
) {
    public static DirectorResponse from(Director director) {
        return new DirectorResponse(
                director.getDirectorId(),
                director.getName(),
                director.getNationality(),
                director.getBirthDate()
        );
    }
}
