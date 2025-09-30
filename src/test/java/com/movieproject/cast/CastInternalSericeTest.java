package com.movieproject.cast;

import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.service.ActorExternalService;
import com.movieproject.domain.cast.dto.request.CastRequest;
import com.movieproject.domain.cast.dto.response.CastResponse;
import com.movieproject.domain.cast.entity.Cast;
import com.movieproject.domain.cast.enums.CastRole;
import com.movieproject.domain.cast.exception.CastErrorCode;
import com.movieproject.domain.cast.exception.CastException;
import com.movieproject.domain.cast.repository.CastRepository;
import com.movieproject.domain.cast.service.CastInternalService;
import com.movieproject.domain.movie.entity.Movie;
import com.movieproject.domain.movie.service.MovieExternalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CastInternalSericeTest {

    @Mock
    private CastRepository castRepository;

    @Mock
    private MovieExternalService movieExternalService;

    @Mock
    private ActorExternalService actorExternalService;

    @InjectMocks
    private CastInternalService castInternalService;

    @Test
    void 캐스트생성_성공() {
        // given
        Actor actor = Actor.builder()
                .name("송강호")
                .nationality("대한민국")
                .birthDate(LocalDate.of(1967, 1, 17))
                .build();

        Movie movie = Movie.builder()
                .title("기생충")
                .releaseDate(LocalDate.of(2019, 5, 30))
                .duration(132)
                .nationality("대한민국")
                .genre("드라마")
                .director(null) // 감독은 테스트 편의상 null 처리
                .build();

        CastRequest request = new CastRequest(movie.getMovieId(), actor.getActorId(), CastRole.LEAD);

        when(actorExternalService.findActorById(request.actorId())).thenReturn(actor);
        when(movieExternalService.findMovieById(request.movieId())).thenReturn(movie);
        when(castRepository.existsByActorAndMovie(actor, movie)).thenReturn(false);

        Cast cast = Cast.builder()
                .role(CastRole.LEAD)
                .actor(actor)
                .movie(movie)
                .build();

        when(castRepository.save(any(Cast.class))).thenReturn(cast);

        // when
        CastResponse response = castInternalService.createCast(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.role()).isEqualTo(CastRole.LEAD);
        assertThat(response.actorName()).isEqualTo("송강호");
        assertThat(response.movieTitle()).isEqualTo("기생충");
    }

    @Test
    void 캐스트생성_실패() {
        // given
        Actor actor = mock(Actor.class);
        Movie movie = mock(Movie.class);

        CastRequest request = new CastRequest(1L, 1L, CastRole.LEAD);

        when(actorExternalService.findActorById(request.actorId())).thenReturn(actor);
        when(movieExternalService.findMovieById(request.movieId())).thenReturn(movie);
        when(castRepository.existsByActorAndMovie(actor, movie)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> castInternalService.createCast(request))
                .isInstanceOf(CastException.class)
                .hasMessage(CastErrorCode.ALREADY_EXIST_CAST.getMessage());
    }

    @Test
    void 캐스트조회_성공() {
        // given
        Actor actor = Actor.builder()
                .name("송강호")
                .nationality("대한민국")
                .birthDate(LocalDate.of(1967, 1, 17))
                .build();

        Movie movie = Movie.builder()
                .title("괴물")
                .releaseDate(LocalDate.of(2006, 7, 27))
                .duration(120)
                .nationality("대한민국")
                .genre("스릴러")
                .director(null)
                .build();

        Cast cast = Cast.builder()
                .role(CastRole.SUPPORTING)
                .actor(actor)
                .movie(movie)
                .build();

        when(castRepository.findAll()).thenReturn(List.of(cast));

        // when
        List<CastResponse> responses = castInternalService.getCasts();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).movieTitle()).isEqualTo("괴물");
    }

    @Test
    void 캐스트삭제_성공() {
        // given
        Cast cast = mock(Cast.class);
        when(castRepository.findById(1L)).thenReturn(Optional.of(cast));

        // when
        castInternalService.deleteCast(1L);

        // then
        verify(castRepository).delete(cast);
    }
}
