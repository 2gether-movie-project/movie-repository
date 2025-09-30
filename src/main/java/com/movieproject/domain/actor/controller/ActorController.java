package com.movieproject.domain.actor.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.actor.dto.request.ActorRequest;
import com.movieproject.domain.actor.dto.request.ActorUpdateRequest;
import com.movieproject.domain.actor.dto.response.ActorDetailResponse;
import com.movieproject.domain.actor.dto.response.ActorResponse;
import com.movieproject.domain.actor.service.ActorInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorInternalService actorInternalService;

    //배우 등록
    @PostMapping("")
    public ResponseEntity<ApiResponse<ActorResponse>> createActors(@Valid @RequestBody ActorRequest actorRequest) {

        ActorResponse actorResponse = actorInternalService.createActors(actorRequest);

        return ApiResponse.created(actorResponse, "배우가 성공적으로 등록되었습니다.");
    }

    //배우 전체조회
    @GetMapping
    public ResponseEntity<PageResponse<ActorResponse>> getActors(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        PageResponse<ActorResponse> response = actorInternalService.getActors(pageable);

        return ResponseEntity.ok(response);
    }

    //배우 상세조회
    @GetMapping("/v1/{actorId}")
    public ResponseEntity<ApiResponse<ActorDetailResponse>> getDirectorDetail(
            @PathVariable Long actorId
    ) {
        ActorDetailResponse response = actorInternalService.getActorDetail(actorId);
        return ApiResponse.success(response, "배우 상세 정보가 성공적으로 조회되었습니다.");
    }

    //배우 상세조회
    @GetMapping("/v2/{actorId}")
    public ResponseEntity<ApiResponse<ActorDetailResponse>> getDirectorDetailByRedis(
            @PathVariable Long actorId
    ) {
        ActorDetailResponse response = actorInternalService.getActorDetail(actorId);
        return ApiResponse.success(response, "배우 상세 정보가 성공적으로 조회되었습니다.");
    }

    //배우 수정
    @PutMapping("/{actorId}")
    public ResponseEntity<ApiResponse<ActorResponse>> updateActor(
            @PathVariable Long actorId,
            @Valid @RequestBody ActorUpdateRequest actorUpdateRequest) {
        ActorResponse response = actorInternalService.updateActor(actorId, actorUpdateRequest);
        return ApiResponse.success(response, "배우 정보가 성공적으로 수정되었습니다.");
    }

    //배우 삭제
    @DeleteMapping("/{actorId}")
    public ResponseEntity<ApiResponse<Void>> deleteActor(@PathVariable Long actorId) {
        actorInternalService.deleteActor(actorId);
        return ApiResponse.success(null, "배우가 성공적으로 삭제되었습니다.");
    }
}
