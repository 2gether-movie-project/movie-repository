package com.movieproject.domain.search.service;

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

    @Cacheable(value = "searchTitleCache", key = "#keyword + '_' + #page + '_' + #size")
    @Transactional(readOnly = true)
    public Page<MovieSearchResponse> searchTitle(String keyword, int page, int size) {
        return internalMovieService.searchByKeyword(keyword, page, size);
    }

    @Cacheable(value = "searchActorCache", key = "#keyword + '_' + #page + '_' + #size")
    @Transactional(readOnly = true)
    public Page<MovieSearchResponse> searchActor(String keyword, int page, int size) {
        return internalActorService.searchByKeyword(keyword, page, size);
    }

    @Cacheable(value = "searchDirectorCache", key = "#keyword + '_' + #page + '_' + #size")
    @Transactional(readOnly = true)
    public Page<MovieSearchResponse> searchDirector(String keyword, int page, int size) {
        return internalDirectorService.searchByKeyword(keyword, page, size);
    }

    @Cacheable(value = "popularSearchCache", key = "'top10'")
    @Transactional(readOnly = true)
    public List<String> getPopularSearches() {
        return searchRepository.findTop10ByOrderByCountDesc()
                .stream()
                .map(Search::getOriginalKeyword)
                .toList();
    }
}