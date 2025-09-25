package com.movieproject.director;

import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.director.dto.request.DirectorRequest;
import com.movieproject.domain.director.dto.response.DirectorDetailResponse;
import com.movieproject.domain.director.dto.response.DirectorResponse;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.exception.DirectorErrorCode;
import com.movieproject.domain.director.exception.DirectorException;
import com.movieproject.domain.director.repository.DirectorRepository;
import com.movieproject.domain.director.service.DirectorInternalService;
import com.movieproject.domain.movie.entity.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternalDirectorServiceTest  {

    @Mock
    private DirectorRepository directorRepository;

    @InjectMocks
    private DirectorInternalService internalDirectorService;

    @Test
    void 감독생성시_정상등록() {

        // given
        DirectorRequest request = new DirectorRequest(
                "봉준호",
                "대한민국",
                LocalDate.of(1969, 9, 14)
        );
        Director mockDirector = Director.of(
                request.name(),
                request.nationality(),
                request.birthDate()
        );

        when(directorRepository.save(any(Director.class)))
                .thenReturn(mockDirector);
        //when
        DirectorResponse response = internalDirectorService.createDirectors(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(mockDirector.getName());
        assertThat(response.nationality()).isEqualTo(mockDirector.getNationality());
        assertThat(response.birthDate()).isEqualTo(mockDirector.getBirthDate());
    }

    @Test
    void 이미존재하는경우_예외발생() {
        // given
        DirectorRequest request = new DirectorRequest(
                "봉준호",
                "대한민국",
                LocalDate.of(1969, 9, 14)
        );

        Director existing = Director.of(request.name(), request.nationality(), request.birthDate());

        // 이미 같은 감독이 존재한다고 가정
        when(directorRepository.findByNameAndBirthDate(request.name(), request.birthDate()))
                .thenReturn(Optional.of(existing));

        // when & then
        assertThatThrownBy(() -> internalDirectorService.createDirectors(request))
                .isInstanceOf(DirectorException.class)
                .hasMessage(DirectorErrorCode.ALREADY_EXIST_DIRECTOR.getMessage());
    }

    @Test
    void getDirectors_정상조회() {
        // given
        Pageable pageable = PageRequest.of(0, 2, Sort.by("directorId").descending());

        Director director1 = Director.of("봉준호", "한국", LocalDate.of(1969, 9, 14));
        Director director2 = Director.of("크리스토퍼 놀란", "영국", LocalDate.of(1970, 7, 30));

        List<Director> directors = List.of(director1, director2);
        Page<Director> page = new PageImpl<>(directors, pageable, directors.size());

        when(directorRepository.findAll(pageable)).thenReturn(page);

        // when
        PageResponse<DirectorResponse> result = internalDirectorService.getDirectors(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);

        // DTO 변환까지 검증
        assertThat(result.getContent().get(0).name()).isEqualTo("봉준호");
        assertThat(result.getContent().get(0).nationality()).isEqualTo("한국");
        assertThat(result.getContent().get(1).name()).isEqualTo("크리스토퍼 놀란");
        assertThat(result.getContent().get(1).nationality()).isEqualTo("영국");
    }

    @Test
    void 감독상세조회_성공() {
        // given
        Director director = Director.of("봉준호", "대한민국", LocalDate.of(1969, 9, 14));

        Movie movie1 = Movie.builder()
                .title("기생충")
                .releaseDate(LocalDate.of(2019, 5, 30))
                .duration(132)
                .nationality("대한민국")
                .genre("드라마")
                .director(director)
                .build();

        Movie movie2 = Movie.builder()
                .title("괴물")
                .releaseDate(LocalDate.of(2006, 7, 27))
                .duration(119)
                .nationality("대한민국")
                .genre("스릴러")
                .director(director)
                .build();


        director.getMovies().add(movie1);
        director.getMovies().add(movie2);

        when(directorRepository.findByIdWithMovies(1L)).thenReturn(Optional.of(director));

        // when
        DirectorDetailResponse response = internalDirectorService.getDirectorDetail(1L);

        // then
        assertThat(response.directorId()).isEqualTo(director.getDirectorId());
        assertThat(response.name()).isEqualTo("봉준호");
        assertThat(response.movies()).hasSize(2);
        assertThat(response.movies().get(0).title()).isEqualTo("기생충");
        assertThat(response.movies().get(1).title()).isEqualTo("괴물");

    }
    @Test
    void 감독상세조회실패_감독없어() {
        // given
        when(directorRepository.findByIdWithMovies(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> internalDirectorService.getDirectorDetail(99L))
                .isInstanceOf(DirectorException.class)
                .hasMessage(DirectorErrorCode.DIRECTOR_NOT_FOUND.getMessage());
    }

}
