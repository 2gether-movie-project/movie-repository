package com.movieproject.domain.actor.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.common.response.PageResponse;
import com.movieproject.domain.actor.dto.request.ActorRequest;
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
}
