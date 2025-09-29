package com.movieproject.domain.search.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.movie.dto.response.MovieSearchResponse;
import com.movieproject.domain.search.service.SearchExternalCacheService;
import com.movieproject.domain.search.service.SearchInternalCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class SearchCacheController {
    private final SearchExternalCacheService searchCacheService;
    private final SearchInternalCacheService searchInternalCacheService;

    @GetMapping("/movies/search")
    public ResponseEntity<ApiResponse<PageResponse<MovieSearchResponse>>> searchMovies(
            @RequestParam("title") String title,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        searchInternalCacheService.recordSearch(title);
        Page<MovieSearchResponse> resultPage = searchCacheService.searchTitle(title, page, size);
        return ApiResponse.pageSuccess(resultPage, "영화 제목 검색 결과");
    }

    @GetMapping("/actors/search")
    public ResponseEntity<ApiResponse<PageResponse<MovieSearchResponse>>> searchActors(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        searchInternalCacheService.recordSearch(name);
        Page<MovieSearchResponse> resultPage = searchCacheService.searchActor(name, page, size);
        return ApiResponse.pageSuccess(resultPage, "영화 배우 검색 결과");
    }

    @GetMapping("/directors/search")
    public ResponseEntity<ApiResponse<PageResponse<MovieSearchResponse>>> searchDirectors(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        searchInternalCacheService.recordSearch(name);
        Page<MovieSearchResponse> resultPage = searchCacheService.searchDirector(name, page, size);
        return ApiResponse.pageSuccess(resultPage, "영화 감독 검색 결과");
    }

    @GetMapping("/search/popular")
    public ResponseEntity<ApiResponse<List<String>>> searchPopular() {
        return ApiResponse.success(searchCacheService.getPopularSearches(), "인기 검색어 조회");
    }
}
