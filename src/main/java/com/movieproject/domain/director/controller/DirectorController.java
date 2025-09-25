package com.movieproject.domain.director.controller;


import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.director.dto.request.DirectorRequest;
import com.movieproject.domain.director.dto.response.DirectorDetailResponse;
import com.movieproject.domain.director.dto.response.DirectorResponse;
import com.movieproject.domain.director.service.DirectorInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/directors")
public class DirectorController {

    private final DirectorInternalService internalDirectorService;

    //감독 등록
    @PostMapping("")
    public ResponseEntity<ApiResponse<DirectorResponse>> createDirector(@Valid @RequestBody DirectorRequest directorRequest) {

        DirectorResponse directorResponse = internalDirectorService.createDirectors(directorRequest);

        return ApiResponse.created(directorResponse,"감독이 성공적으로 등록되었습니다.");
    }


    //감독 전체조회
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DirectorResponse>>> getDirectors(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        PageResponse<DirectorResponse> response = internalDirectorService.getDirectors(pageable);

        return ApiResponse.success(response,"감독 목록이 성공적으로 조회되었습니다.");
    }

    //감독 상세조회
    @GetMapping("/{directorId}")
    public ResponseEntity<ApiResponse<DirectorDetailResponse>> getDirectorDetail(
            @PathVariable Long directorId
    ) {
        DirectorDetailResponse response = internalDirectorService.getDirectorDetail(directorId);
        return ApiResponse.success(response,"감독 상세 정보가 성공적으로 조회되었습니다.");
    }

}
