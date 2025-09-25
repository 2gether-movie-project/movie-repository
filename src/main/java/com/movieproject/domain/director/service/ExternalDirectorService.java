package com.movieproject.domain.director.service;

import com.movieproject.common.exception.GlobalException;
import com.movieproject.domain.director.dto.response.DirectorInfoDto;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.exception.DirectorErrorCode;
import com.movieproject.domain.director.exception.DirectorException;
import com.movieproject.domain.director.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExternalDirectorService {

    private final DirectorRepository directorRepository;

    public DirectorInfoDto getDirectorInfo(Long directorId) {
        Director director = directorRepository.findById(directorId)
                .orElseThrow(() -> new DirectorException(DirectorErrorCode.DIRECTOR_NOT_FOUND));
        return DirectorInfoDto.from(director);
    }


}
