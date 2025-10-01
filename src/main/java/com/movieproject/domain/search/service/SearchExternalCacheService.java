package com.movieproject.domain.search.service;

import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.actor.service.ActorInternalService;
import com.movieproject.domain.director.service.DirectorInternalService;
import com.movieproject.domain.movie.dto.response.MovieSearchResponse;
import com.movieproject.domain.movie.service.MovieInternalService;
import com.movieproject.domain.search.entity.Search;
import com.movieproject.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchExternalCacheService {
    private final MovieInternalService internalMovieService;
    private final DirectorInternalService internalDirectorService;
    private final ActorInternalService internalActorService;
    private final SearchRepository searchRepository;

    @Cacheable(cacheNames = "searchTitleCache", key = "#keyword + '_' + #page + '_' + #size")
    @Transactional
    public PageResponse<MovieSearchResponse> searchTitle(String keyword, int page, int size) {
        Page<MovieSearchResponse> result = internalMovieService.searchByKeyword(keyword, page, size);
        return PageResponse.fromPage(result);
    }

    @Cacheable(cacheNames = "searchActorCache", key = "#keyword + '_' + #page + '_' + #size")
    @Transactional(readOnly = true)
    public PageResponse<MovieSearchResponse> searchActor(String keyword, int page, int size) {
        Page<MovieSearchResponse> result = internalActorService.searchByKeyword(keyword, page, size);
        return PageResponse.fromPage(result);
    }


    @Cacheable(cacheNames = "searchDirectorCache", key = "#keyword + '_' + #page + '_' + #size")
    @Transactional
    public PageResponse<MovieSearchResponse> searchDirector(String keyword, int page, int size) {
        Page<MovieSearchResponse> result = internalDirectorService.searchByKeyword(keyword, page, size);
        return PageResponse.fromPage(result);
    }

    @Cacheable(cacheNames = "popularSearchCache", key = "'top10'")
    @Transactional
    public List<String> getPopularSearches() {
        return searchRepository.findTop10ByOrderByCountDesc()
                .stream()
                .map(Search::getOriginalKeyword)
                .toList();
    }
}