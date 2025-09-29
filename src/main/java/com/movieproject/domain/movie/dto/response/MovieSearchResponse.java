package com.movieproject.domain.movie.dto.response;

import com.movieproject.domain.movie.entity.Movie;

import java.time.LocalDate;
import java.util.stream.Collectors;

public record MovieSearchResponse(Long id, String title, LocalDate releaseDate, String director, String actor, String genre, Double rating) {
    public static MovieSearchResponse from(Movie movie) {
        //그냥 호출하면 NullPointerException이 발생할수 있으므로 null아면 null로 반환하는 코드
        //값이 존재하면 출력 / 값이 없으면 정보없음 출력
        String directorName = movie.getDirector() != null ? movie.getDirector().getName() : null;
        //cast가 비어있거나 null이면 null, 아니면 이름들을 , 로 연결
        String actorNames = (movie.getCastMembers() != null && !movie.getCastMembers().isEmpty()) ? movie.getCastMembers().stream().map(cast -> cast.getActor().getName()).collect(Collectors.joining(", ")) : null;

        String genreName = movie.getGenre() != null ? movie.getGenre() : null;

        return new MovieSearchResponse(movie.getMovieId(), movie.getTitle(), movie.getReleaseDate(), directorName, actorNames, genreName, movie.getRating());
    }
}