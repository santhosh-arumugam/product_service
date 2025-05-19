package com.swiftcart.product_service.repository;

import com.swiftcart.product_service.entity.ProductEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductEventLogRepository extends JpaRepository<ProductEventLog, Long> {
}
