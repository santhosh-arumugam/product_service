package com.swiftcart.product_service.repository;

import com.swiftcart.product_service.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
