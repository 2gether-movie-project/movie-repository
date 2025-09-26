package com.movieproject.actor;

import com.movieproject.domain.actor.dto.request.ActorRequest;
import com.movieproject.domain.actor.dto.response.ActorResponse;
import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.exception.ActorErrorCode;
import com.movieproject.domain.actor.exception.ActorException;
import com.movieproject.domain.actor.repository.ActorRepository;
import com.movieproject.domain.actor.service.ActorInternalService;
import com.movieproject.domain.director.dto.request.DirectorRequest;
import com.movieproject.domain.director.dto.response.DirectorResponse;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.exception.DirectorErrorCode;
import com.movieproject.domain.director.exception.DirectorException;
import com.movieproject.domain.director.repository.DirectorRepository;
import com.movieproject.domain.director.service.DirectorInternalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

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
        when(actorRepository.findByNameAndBirthDate(request.name(), request.birthDate()))
                .thenReturn(Optional.of(existing));

        // when & then
        assertThatThrownBy(() -> actorInternalService.createActors(request))
                .isInstanceOf(ActorException.class)
                .hasMessage(ActorErrorCode.ALREADY_EXIST_ACTOR.getMessage());
    }
}
