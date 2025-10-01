package com.movieproject.domain.search.repository;

import com.movieproject.domain.search.entity.Search;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SearchRepository extends JpaRepository<Search, Long> {

    //비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Search s where s.keyword = :keyword")
    Optional<Search> findByKeywordForUpdate(@Param("keyword") String keyword);

    List<Search> findTop10ByOrderByCountDesc();

    @Modifying
    @Query("update Search s set s.count = s.count + 1, s.originalKeyword = :origin where s.keyword = :normal")
    int incrementCount(@Param("normal") String normalizedKeyword, @Param("origin") String originalKeyword);
}