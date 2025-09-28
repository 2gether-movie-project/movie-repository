package com.movieproject.domain.cast.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.domain.cast.dto.request.CastRequest;
import com.movieproject.domain.cast.dto.response.CastResponse;
import com.movieproject.domain.cast.service.CastInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/casts")
@RequiredArgsConstructor
public class CastController {

    private final CastInternalService castInternalService;

    @PostMapping
    public ResponseEntity<ApiResponse<CastResponse>> createCast(@RequestBody @Valid CastRequest request) {
        CastResponse response = castInternalService.createCast(request);
        return ApiResponse.success(response, "출연진 등록이 성공적으로 완료되었습니다.");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CastResponse>>> getCasts() {
        List<CastResponse> response = castInternalService.getCasts();
        return ApiResponse.success(response, "출연진 목록 조회가 성공적으로 완료되었습니다.");
    }

    @DeleteMapping("/{castId}")
    public ResponseEntity<ApiResponse<Void>> deleteCast(@PathVariable Long castId) {
        castInternalService.deleteCast(castId);
        return ApiResponse.success(null, "출연진 삭제가 성공적으로 완료되었습니다.");
    }
}
