package com.movieproject.domain.search.entity;

import com.movieproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="search")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Search extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 검색어 비교용 (소문자로 변환)
    @Column(nullable = false, unique = true)
    private String keyword;

    // 사용자가 입력한 검색어
    @Column(nullable = false)
    private String originalKeyword;

    @Column(nullable = false)
    private Long count = 0L;

    public static Search of(String keyword, String originalKeyword) {
        Search search = new Search();
        search.keyword = keyword;
        search.originalKeyword = originalKeyword;
        search.count = 1L;
        return search;
    }

    public void incrementCount(String originalKeyword) {
        this.count++;
        this.originalKeyword = originalKeyword; // 마지막 검색어 갱신
    }
}
