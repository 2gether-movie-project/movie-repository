package com.movieproject.domain.search.repository;

import com.movieproject.domain.search.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SearchRepository extends JpaRepository<Search, Long> {
    Optional<Search> findByKeyword(String keyword);
    List<Search> findTop10ByOrderByCountDesc(); //내림차순 정렬 후 상위 10개 조회

    @Modifying //데이터 수정하는 쿼리
    @Query("update Search s set s.count = s.count + 1, s.originalKeyword = :origin where s.keyword = :normal") //originalKeyword의 count가 1만큼 증가하는 JPQL
    //키워드를 읽고, 수정하고, 다시 저장하는 과정 사이에 다른 스레드가 끼어들어 데이터를 덮어쓰게 되면 카운트가 정확하지 않게 기록된다.
    int incrementCount(@Param("normal") String normalizedKeyword,@Param("origin") String originalKeyword);
}
