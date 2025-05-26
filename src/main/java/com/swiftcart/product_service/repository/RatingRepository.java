package com.swiftcart.product_service.repository;

import com.swiftcart.product_service.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT r FROM Rating r WHERE r.customerId = :customerId AND r.product.id = :productId")
    Optional<Rating> findByCustomerIdAndProductId(@Param("customerId") Long customerId, @Param("productId") Long productId);

}
