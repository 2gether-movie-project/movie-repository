package com.movieproject.domain.movie.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.movie.dto.request.MovieRequestDto;
import com.movieproject.domain.movie.dto.response.MovieListDto;
import com.movieproject.domain.movie.dto.response.MovieResponseDto;
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

    @PostMapping("/api/movies")
    public ResponseEntity<ApiResponse<MovieResponseDto>> createMovie(
            @Valid @RequestBody MovieRequestDto.Create requestDto) {
        MovieResponseDto responseDto = movieInternalService.createMovie(requestDto);
        return ApiResponse.created(responseDto, "영화가 성공적으로 등록되었습니다.");
    }

    @GetMapping("/api/v1/movies/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDto>> getMovieInfo(
            @PathVariable Long movieId) {
        MovieResponseDto responseDto = movieInternalService.getMovieInfo(movieId);
        return ApiResponse.success(responseDto, "영화 상세 정보 조회가 완료되었습니다.");
    }

    @GetMapping("/api/movies")
    public ResponseEntity<ApiResponse<PageResponse<MovieListDto>>> getMovies(Pageable pageable) {
        Page<MovieListDto> moviesPage = movieInternalService.getMovies(pageable);
        return ApiResponse.pageSuccess(moviesPage, "영화 목록 조회가 완료되었습니다.");
    }

    @GetMapping("/api/v2/movies/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDto>> getMovieInfoV2(
            @PathVariable Long movieId) {
        MovieResponseDto responseDto = movieInternalService.getMovieInfoV2(movieId);
        return ApiResponse.success(responseDto, "영화 상세 정보(캐시) 조회가 완료되었습니다.");
    }

    @PutMapping("/api/movies/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDto>> updateMovie(
            @PathVariable Long movieId,
            @Valid @RequestBody MovieRequestDto.Update requestDto) {

        MovieResponseDto responseDto = movieInternalService.updateMovie(movieId, requestDto);
        return ApiResponse.success(responseDto, "영화 정보가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/api/movies/{movieId}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long movieId) {
        movieInternalService.deleteMovie(movieId);
        return ApiResponse.success(null, "영화가 성공적으로 삭제되었습니다.");
    }
}