package com.movieproject.domain.review.service;

import com.movieproject.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewExternalService {

    private final ReviewRepository reviewRepository;
}
