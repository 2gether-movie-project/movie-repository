package com.movieproject.domain.search.service;

import com.movieproject.domain.actor.service.ActorInternalService;
import com.movieproject.domain.director.service.DirectorInternalService;
import com.movieproject.domain.movie.dto.response.MovieSearchResponse;
import com.movieproject.domain.movie.service.MovieInternalService;
import com.movieproject.domain.search.entity.Search;
import com.movieproject.domain.search.exception.InvalidSearchException;
import com.movieproject.domain.search.exception.SearchErrorCode;
import com.movieproject.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchExternalService {
    private final MovieInternalService internalMovieService;
    private final DirectorInternalService internalDirectorService;
    private final ActorInternalService internalActorService;
    private final SearchRepository searchRepository;

    // 저장 및 카운트 증가
    @Transactional
    protected void recordSearch(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(); // 소문자 변환
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

    //유효성 검사
    private void validateSearch(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new InvalidSearchException(SearchErrorCode.INVALID_KEYWORD);
        }
    }

    //영화 제목 검색
    @Transactional
    public Page<MovieSearchResponse> searchTitle(String keyword, int page, int size) {
        validateSearch(keyword); //유효성 검증
        recordSearch(keyword); // 검색 기록 저장
        return internalMovieService.searchByKeyword(keyword, page, size);
    }

    //영화 배우 검색
    @Transactional
    public Page<MovieSearchResponse> searchActor(String keyword, int page, int size) {
        validateSearch(keyword);
        recordSearch(keyword);
        return internalActorService.searchByKeyword(keyword, page, size);
    }

    //영화 감독 검색
    @Transactional
    public Page<MovieSearchResponse> searchDirector(String keyword, int page, int size) {
        validateSearch(keyword);
        recordSearch(keyword);
        return internalDirectorService.searchByKeyword(keyword, page, size);
    }

    //인기검색어 조회(상위 10개)
    @Transactional
    public List<String> getPopularSearches() {
        return searchRepository.findTop10ByOrderByCountDesc() // count 내림차순 상위 10개
                .stream()
                .map(Search::getOriginalKeyword) // 원래 입력된 검색어 반환
                .toList();
    }
}
