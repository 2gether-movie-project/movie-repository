package com.movieproject.domain.review.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ReviewRequest {

    public record Create(
            @NotBlank(message = "리뷰 내용은 필수입니다.")
            @Size(max = 300, message = "리뷰는 최대 300자까지 작성 가능합니다.")
            String content,

            @NotNull(message = "평점은 필수입니다.")
            @DecimalMin(value = "0.0", message = "평점은 0.0 이상이어야 합니다.")
            @DecimalMax(value = "5.0", message = "평점은 5.0 이하이어야 합니다.")
            BigDecimal rating
    ) {}

    public record Update(
            @NotBlank(message = "리뷰 내용은 필수입니다.")
            @Size(max = 300, message = "리뷰는 최대 300자까지 작성 가능합니다.")
            String content,

            @DecimalMin(value = "0.0", message = "평점은 0.0 이상이어야 합니다.")
            @DecimalMax(value = "5.0", message = "평점은 5.0 이하이어야 합니다.")
            BigDecimal rating
    ) {}
}
