package com.movieproject.domain.movie.service;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.service.DirectorExternalService;
import com.movieproject.domain.movie.dto.request.MovieRequestDto;
import com.movieproject.domain.movie.dto.response.MovieResponseDto;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieInternalService {

    private final MovieRepository movieRepository;
    private final DirectorExternalService directorExternalService;

    public MovieResponseDto createMovie(MovieRequestDto.Create requestDto) {
        Director director = directorExternalService.findDirectorById(requestDto.directorId());

        Movie movie = Movie.builder()
                .title(requestDto.title())
                .releaseDate(requestDto.releaseDate())
                .duration(requestDto.duration())
                .nationality(requestDto.nationality())
                .genre(requestDto.genre())
                .director(director)
                .build();

        Movie savedMovie = movieRepository.save(movie);

        return MovieResponseDto.from(savedMovie);
    }
}