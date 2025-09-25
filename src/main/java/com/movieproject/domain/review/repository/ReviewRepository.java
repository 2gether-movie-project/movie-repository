package com.movieproject.domain.review.repository;

import com.movieproject.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    @Query()
    Page<Review> findAllByMovie_Id(Long movieId, Pageable pageable);
}
