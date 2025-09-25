package com.movieproject.domain.movie.service;

import com.movieproject.common.exception.GlobalException;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.service.DirectorExternalService;
import com.movieproject.domain.movie.dto.request.MovieRequestDto;
import com.movieproject.domain.movie.dto.response.MovieListDto;
import com.movieproject.domain.movie.dto.response.MovieResponseDto;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.exception.MovieErrorCode;
import com.movieproject.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieInternalService {

    private final MovieRepository movieRepository;
    private final DirectorExternalService directorExternalService;

    @Transactional
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

    public MovieResponseDto getMovieInfo(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new GlobalException(MovieErrorCode.MOVIE_NOT_FOUND));
        return MovieResponseDto.from(movie);
    }

    public Page<MovieListDto> getMovies(Pageable pageable) {
        Page<Movie> movies = movieRepository.findAll(pageable);
        return movies.map(MovieListDto::from);
    }
}