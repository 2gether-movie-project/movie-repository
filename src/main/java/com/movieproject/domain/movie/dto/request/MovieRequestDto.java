package com.movieproject.domain.movie.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MovieRequestDto() {

    public record Create(
            @NotBlank(message = "영화 제목은 필수입니다.")
            String title,

            @NotNull(message = "개봉일은 필수입니다.")
            LocalDate releaseDate,

            @NotNull(message = "상영 시간은 필수입니다.")
            Integer duration,

            @NotBlank(message = "국가는 필수입니다.")
            String nationality,

            @NotBlank(message = "장르는 필수입니다.")
            String genre,

            @NotNull(message = "감독 ID는 필수입니다.")
            Long directorId
    ) {}
}