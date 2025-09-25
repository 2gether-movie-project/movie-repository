package com.movieproject.domain.director.service;

import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.director.dto.request.DirectorRequest;
import com.movieproject.domain.director.dto.request.DirectorUpdateRequest;
import com.movieproject.domain.director.dto.response.DirectorDetailResponse;
import com.movieproject.domain.director.dto.response.DirectorResponse;
import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.exception.DirectorException;
import com.movieproject.domain.director.exception.DirectorErrorCode;
import com.movieproject.domain.director.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional(readOnly = true)
    public PageResponse<DirectorResponse> getDirectors(Pageable pageable) {

        Page<Director> page = directorRepository.findAll(pageable);

        List<DirectorResponse> responses = new ArrayList<>();
        for (Director director : page.getContent()) {
            responses.add(DirectorResponse.from(director));
        }

        return new PageResponse<>(
                responses,
                page.getTotalElements(),
                page.getTotalPages(),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
    }

    @Transactional(readOnly = true)
    public DirectorDetailResponse getDirectorDetail(Long directorId) {

        Director director = directorRepository.findByIdWithMovies(directorId)
                .orElseThrow(() -> new DirectorException(DirectorErrorCode.DIRECTOR_NOT_FOUND));

        DirectorDetailResponse directorDetailResponse = DirectorDetailResponse.from(director);

        return directorDetailResponse;
    }

    @Transactional
    public DirectorResponse updateDirector(Long directorId, DirectorUpdateRequest directorUpdateRequest) {

        Director director = directorRepository.findById(directorId)
                .orElseThrow(() -> new DirectorException(DirectorErrorCode.DIRECTOR_NOT_FOUND));

        director.updateDirector(directorUpdateRequest);

        DirectorResponse directorResponse = DirectorResponse.from(director);

        return directorResponse;

    }
}
