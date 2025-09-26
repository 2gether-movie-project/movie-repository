package com.movieproject.actor;

import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.actor.dto.request.ActorRequest;
import com.movieproject.domain.actor.dto.request.ActorUpdateRequest;
import com.movieproject.domain.actor.dto.response.ActorDetailResponse;
import com.movieproject.domain.actor.dto.response.ActorResponse;
import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.exception.ActorErrorCode;
import com.movieproject.domain.actor.exception.ActorException;
import com.movieproject.domain.actor.repository.ActorRepository;
import com.movieproject.domain.actor.service.ActorInternalService;
import com.movieproject.domain.cast.entity.Cast;
import com.movieproject.domain.cast.enums.CastRole;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.movie.entity.Movie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActorInternalServiceTest {

    @Mock
    private ActorRepository actorRepository;

    @InjectMocks
    private ActorInternalService actorInternalService;

    @Test
    void 배우생성시_정상등록() {

        // given
        ActorRequest request = new ActorRequest(
                "송강호",
                "대한민국",
                LocalDate.of(1967, 1, 17)
        );
        Actor mockActor = Actor.of(
                request.name(),
                request.nationality(),
                request.birthDate()
        );

        when(actorRepository.save(any(Actor.class)))
                .thenReturn(mockActor);
        //when
        ActorResponse response = actorInternalService.createActors(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(mockActor.getName());
        assertThat(response.nationality()).isEqualTo(mockActor.getNationality());
        assertThat(response.birthDate()).isEqualTo(mockActor.getBirthDate());
    }

    @Test
    void 이미존재하는경우_예외발생() {

        // given
        ActorRequest request = new ActorRequest(
                "송강호",
                "대한민국",
                LocalDate.of(1967, 1, 17)
        );

        Actor existing = Actor.of(request.name(), request.nationality(), request.birthDate());

        // 이미 같은 감독이 존재한다고 가정
        when(actorRepository.existsByNameAndBirthDate(request.name(), request.birthDate()))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> actorInternalService.createActors(request))
                .isInstanceOf(ActorException.class)
                .hasMessage(ActorErrorCode.ALREADY_EXIST_ACTOR.getMessage());
    }

    @Test
    void getActors_정상조회() {
        // given
        Pageable pageable = PageRequest.of(0, 2, Sort.by("directorId").descending());

        Actor actor1 = Actor.of("봉준호", "한국", LocalDate.of(1969, 9, 14));
        Actor actor2 = Actor.of("크리스토퍼 놀란", "영국", LocalDate.of(1970, 7, 30));

        List<Actor> actors = List.of(actor1, actor2);
        Page<Actor> page = new PageImpl<>(actors, pageable, actors.size());

        when(actorRepository.findAll(pageable)).thenReturn(page);

        // when
        PageResponse<ActorResponse> result = actorInternalService.getActors(pageable);

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
    void 배우상세조회_성공() {
        // given
        Director director = Director.builder()
                .name("봉준호")
                .nationality("대한민국")
                .birthDate(LocalDate.of(1970, 9, 14))
                .build();

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
                .director(director)
                .build();

        Cast cast = Cast.builder()
                .role(CastRole.LEAD)
                .movie(movie)
                .actor(actor)
                .build();

        // 연관관계 수동 설정
        actor.getCasts().add(cast);
        movie.getCastMembers().add(cast);

        when(actorRepository.findByIdWithMovies(1L)).thenReturn(Optional.of(actor));

        // when
        ActorDetailResponse response = actorInternalService.getActorDetail(1L);

        // then
        assertThat(response.name()).isEqualTo("송강호");
        assertThat(response.movies()).hasSize(1);
        assertThat(response.movies().get(0).title()).isEqualTo("기생충");
    }

    @Test
    void 배우상세조회_실패() {
        // given
        when(actorRepository.findByIdWithMovies(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> actorInternalService.getActorDetail(99L))
                .isInstanceOf(ActorException.class)
                .hasMessage(ActorErrorCode.ACTOR_NOT_FOUND.getMessage());
    }

    @Test
    void 감독수정_성공() throws Exception {

        // given
        Long actorId = 1L;
        Actor actor = Actor.of("봉준호", "대한민국", LocalDate.of(1970, 9, 14));
        setActorId(actor, actorId); // 리플렉션으로 ID 강제 세팅

        ActorUpdateRequest request = new ActorUpdateRequest(
                "봉준호-수정",
                "KOR",
                LocalDate.of(1971, 1, 1)
        );

        when(actorRepository.findById(actorId)).thenReturn(Optional.of(actor));

        // when
        ActorResponse response = actorInternalService.updateActor(actorId, request);

        // then
        assertThat(response.name()).isEqualTo("봉준호-수정");
        assertThat(response.nationality()).isEqualTo("KOR");
        assertThat(response.actorId()).isEqualTo(actorId);
        verify(actorRepository).findById(actorId);
    }

    //리플렉션 나중에 쓸까봐 만듬
    private void setActorId(Actor actor, Long id) throws Exception {
        Field field = Actor.class.getDeclaredField("actorId");
        field.setAccessible(true);
        field.set(actor, id);
    }
}
