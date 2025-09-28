package com.movieproject.domain.cast.dto.request;

import com.movieproject.domain.cast.enums.CastRole;
import jakarta.validation.constraints.NotNull;

public record CastRequest(
        @NotNull(message = "actorId 필수값입니다")
        Long actorId,
        @NotNull(message = "movieId 필수값입니다.")
        Long movieId,
        @NotNull(message = "역할은 필수값입니다.")
        CastRole role
) {
}
