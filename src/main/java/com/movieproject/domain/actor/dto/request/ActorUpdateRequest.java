package com.movieproject.domain.actor.dto.request;

import java.time.LocalDate;

//수정같은 경우에는 값 여부가 필수가 아니라고 생각하기때문에 조건을 걸지는 않음
public record ActorUpdateRequest
        (
                String name,
                String nationality,
                LocalDate birthDate) {

}
