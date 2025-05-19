package com.swiftcart.product_service.repository;

import com.swiftcart.product_service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
