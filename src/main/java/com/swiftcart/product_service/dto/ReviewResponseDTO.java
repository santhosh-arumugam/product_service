package com.swiftcart.product_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReviewResponseDTO {
    private Long reviewId;
    private Long productId;
    private Long customerId;
    private String reviewText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
