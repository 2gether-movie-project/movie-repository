package com.movieproject.domain.director.service;

import com.movieproject.domain.director.dto.request.DirectorRequest;
import com.movieproject.domain.director.dto.response.DirectorResponse;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.exception.DirectorException;
import com.movieproject.domain.director.exception.DirectorErrorCode;
import com.movieproject.domain.director.repository.DirectorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DirectorInternalService {

    private final DirectorRepository directorRepository;

    @Transactional
    public DirectorResponse createDirectors(DirectorRequest directorRequest) {

        //이름과 생일이 같은 감독은 없을것이라고 가정했습니다!
        directorRepository.findByNameAndBirthDate(
                directorRequest.name(),
                directorRequest.birthDate()
        ).ifPresent(d -> {
            throw new DirectorException(DirectorErrorCode.ALREADY_EXIST_DIRECTOR);
        });

        Director director = directorRepository.save(Director.of(directorRequest.name(), directorRequest.nationality(),  directorRequest.birthDate()));

        DirectorResponse directorResponse = DirectorResponse.from(director);

        return directorResponse;

    }
}
