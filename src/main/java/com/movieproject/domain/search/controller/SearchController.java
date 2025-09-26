package com.movieproject.domain.search.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.domain.search.service.ExternalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {
    private final ExternalSearchService searchService;

    //page로 구현하자
    @GetMapping("/movies/search")
    public ResponseEntity<ApiResponse<List<MovieDto>>> searchMovies(@RequestParam("title") String title){
        return ApiResponse.success(searchService.searchTitle(title), "영화 제목 검색");
    }

    @GetMapping("/actors/search")
    public ResponseEntity<ApiResponse<List<MovieDto>>> searchActors(@RequestParam("name") String name){
        return ApiResponse.success(searchService.searchActor(name), "영화 배우 검색");
    }

    @GetMapping("/directors/search")
    public ResponseEntity<ApiResponse<List<MovieDto>>> searchDirectors(@RequestParam("name") String name){
        return ApiResponse.success(searchService.searchDirector(name), "영화 감독 검색");
    }

    @GetMapping("/search/popular")
    public ResponseEntity<ApiResponse<List<String>>> searchPopular() {
        return ApiResponse.success(searchService.getPopularSearches(), "인기 검색어 조회");
    }
}
