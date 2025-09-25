package com.movieproject.domain.movie.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.movie.dto.response.MovieListDto;
import com.movieproject.domain.movie.dto.request.MovieRequestDto;
import com.movieproject.domain.movie.dto.response.MovieResponseDto;
import com.movieproject.domain.movie.service.MovieExternalService;
import com.movieproject.domain.movie.service.MovieInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MovieListDto>>> getMovies(Pageable pageable) {
        Page<MovieListDto> moviesPage = movieExternalService.getMovies(pageable);
        return ApiResponse.pageSuccess(moviesPage, "영화 목록 조회가 완료되었습니다.");
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDto>> getMovieInfo(
            @PathVariable Long movieId) {
        MovieResponseDto responseDto = movieExternalService.getMovieInfo(movieId);
        return ApiResponse.success(responseDto, "영화 상세 정보 조회가 완료되었습니다.");
    }
}