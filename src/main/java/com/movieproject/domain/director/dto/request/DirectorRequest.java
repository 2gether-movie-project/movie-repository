package com.movieproject.domain.director.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;


public record DirectorRequest
        (
                @NotBlank(message = "이름을 필수값입니다.") String name,
                String nationality,
                @NotNull(message = "생년월일은 필수값입니다. ") LocalDate birthDate) {

}
