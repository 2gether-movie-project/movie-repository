package com.movieproject.domain.actor.controller;

import com.movieproject.common.response.ApiResponse;
import com.movieproject.domain.actor.dto.request.ActorRequest;
import com.movieproject.domain.actor.dto.response.ActorResponse;
import com.movieproject.domain.actor.service.ActorInternalService;
import com.movieproject.domain.director.dto.request.DirectorRequest;
import com.movieproject.domain.director.dto.response.DirectorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorInternalService actorInternalService;
    //배우 등록
    @PostMapping("")
    public ResponseEntity<ApiResponse<ActorResponse>> createActors(@Valid @RequestBody ActorRequest actorRequest) {

        ActorResponse actorResponse = actorInternalService.createActors(actorRequest);

        return ApiResponse.created(actorResponse,"배우가 성공적으로 등록되었습니다.");
    }

}
