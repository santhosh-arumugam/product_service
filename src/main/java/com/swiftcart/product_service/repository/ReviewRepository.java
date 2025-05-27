package com.swiftcart.product_service.repository;

import com.swiftcart.product_service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.customerId = :customerId")
    Optional<Review> findByCustomerIdAndProductId(@Param("customerId") Long customerId, @Param("productId") Long productId);
}
