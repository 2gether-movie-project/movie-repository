package com.movieproject.director;


import com.movieproject.domain.director.dto.response.DirectorInfoDto;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.exception.DirectorErrorCode;
import com.movieproject.domain.director.exception.DirectorException;
import com.movieproject.domain.director.repository.DirectorRepository;
import com.movieproject.domain.director.service.DirectorExternalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExternalDirectorServiceTest {

    @Mock
    private DirectorRepository directorRepository;

    @InjectMocks
    private DirectorExternalService externalDirectorService;

    @Test
    void 외부도메인에서감독정보를필요로할때_정상반환() {
        // given
        Director director = Director.of(
                "봉준호",
                "대한민국",
                LocalDate.of(1969, 9, 14)
        );

        when(directorRepository.findById(1L))
                .thenReturn(Optional.of(director));

        // when
        DirectorInfoDto dto = externalDirectorService.getDirectorInfo(1L);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.directorId()).isEqualTo(director.getDirectorId());
        assertThat(dto.name()).isEqualTo("봉준호");
        assertThat(dto.nationality()).isEqualTo("대한민국");
        assertThat(dto.birthDate()).isEqualTo(LocalDate.of(1969, 9, 14));
    }

    @Test
    void 만약ID가없다면_예외발생() {
        // given
        when(directorRepository.findById(99L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> externalDirectorService.getDirectorInfo(99L))
                .isInstanceOf(DirectorException.class)
                .hasMessage(DirectorErrorCode.DIRECTOR_NOT_FOUND.getMessage());
    }
}
