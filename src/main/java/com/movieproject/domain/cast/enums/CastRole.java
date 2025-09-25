package com.movieproject.domain.cast.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CastRole {
    LEAD("주연"),
    SUPPORTING("조연");

    private final String description;
}