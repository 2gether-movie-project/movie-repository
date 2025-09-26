package com.movieproject.domain.director.dto.response;

import com.movieproject.domain.director.entity.Director;

public record DirectorDto(
        Long directorId,
        String name,
        String nationality
) {
    public static DirectorDto from(Director director) {
        return new DirectorDto(
                director.getDirectorId(),
                director.getName(),
                director.getNationality()
        );
    }
}