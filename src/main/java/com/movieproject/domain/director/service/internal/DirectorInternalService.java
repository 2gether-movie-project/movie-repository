package com.movieproject.domain.director.service.internal;

import com.movieproject.common.exception.GlobalException;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.exception.DirectorErrorCode;
import com.movieproject.domain.director.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectorInternalService {

    private final DirectorRepository directorRepository;

    public Director findDirectorById(Long directorId) {
        return directorRepository.findById(directorId)
                .orElseThrow(() -> new GlobalException(DirectorErrorCode.DIRECTOR_NOT_FOUND));
    }
}