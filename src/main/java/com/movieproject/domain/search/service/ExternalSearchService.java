package com.movieproject.domain.search.service;

import com.movieproject.domain.search.entity.Search;
import com.movieproject.domain.search.exception.InvalidSearchException;
import com.movieproject.domain.search.exception.SearchErrorCode;
import com.movieproject.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExternalSearchService {

    private final InternalMovieService internalMovieService;
    private final InternalDirectorService internalDirectorService;
    private final InternalActorService internalActorService;
    private final SearchRepository searchRepository;

    @Transactional
    protected void recordSearch(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(); // 소문자 변환

        // JPQL update 먼저 시도
        int updated = searchRepository.incrementCount(normalizedKeyword, keyword);

        if (updated == 0) {
            try {
                Search search = Search.of(normalizedKeyword, keyword);
                searchRepository.save(search);
            } catch (DataIntegrityViolationException e) {
                // 다른 스레드가 insert 했으면 update 재시도
                searchRepository.incrementCount(normalizedKeyword, keyword);
            }
        }
    }

    private void validateSearch(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new InvalidSearchException(SearchErrorCode.INVALID_KEYWORD);
        }
    }

    public List<MovieDto> searchTitle(String keyword) {
        validateSearch(keyword);
        recordSearch(keyword);
        return internalMovieService.searchByKeyword(keyword);
    }

    public List<MovieDto> searchActor(String keyword) {
        validateSearch(keyword);
        recordSearch(keyword);
        return internalActorService.searchByKeyword(keyword);
    }

    public List<MovieDto> searchDirector(String keyword) {
        validateSearch(keyword);
        recordSearch(keyword);
        return internalDirectorService.searchByKeyword(keyword);
    }

    public List<String> getPopularSearches() {
        return searchRepository.findTop10ByOrderByCountDesc()
                .stream()
                .map(Search::getOriginalKeyword)
                .toList();
    }
}
