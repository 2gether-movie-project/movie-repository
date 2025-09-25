package com.movieproject.domain.movie.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.domain.movie.dto.MovieRequestDto;
import com.movieproject.domain.movie.dto.MovieResponseDto;
import com.movieproject.domain.movie.service.MovieExternalService;
import com.movieproject.domain.movie.service.MovieInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieInternalService movieInternalService;
    private final MovieExternalService movieExternalService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponseDto>> createMovie(
            @Valid @RequestBody MovieRequestDto.Create requestDto) {

        MovieResponseDto responseDto = movieInternalService.createMovie(requestDto);

        return ApiResponse.created(responseDto, "영화가 성공적으로 등록되었습니다.");
    }
}