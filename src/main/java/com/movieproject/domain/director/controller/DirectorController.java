package com.movieproject.domain.director.controller;


import com.movieproject.common.response.ApiResponse;
import com.movieproject.domain.director.dto.request.DirectorRequest;
import com.movieproject.domain.director.dto.response.DirectorResponse;
import com.movieproject.domain.director.service.InternalDirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/directors")
public class DirectorController {

    private final InternalDirectorService internalDirectorService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<DirectorResponse>> createDirector(@Valid @RequestBody DirectorRequest directorRequest) {

        DirectorResponse directorResponse = internalDirectorService.createDirectors(directorRequest);

        return ApiResponse.created(directorResponse,"감독이 성공적으로 등록되었습니다.");
    }


}
