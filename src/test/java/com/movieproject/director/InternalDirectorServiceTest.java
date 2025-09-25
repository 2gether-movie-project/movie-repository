package com.movieproject.director;

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
}
