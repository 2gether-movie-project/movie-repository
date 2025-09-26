package com.movieproject.actor;

import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.actor.dto.request.ActorRequest;
import com.movieproject.domain.actor.dto.response.ActorResponse;
import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.exception.ActorErrorCode;
import com.movieproject.domain.actor.exception.ActorException;
import com.movieproject.domain.actor.repository.ActorRepository;
import com.movieproject.domain.actor.service.ActorInternalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

}
