package com.swiftcart.product_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RatingResponseDTO {

    private Long ratingId;
    private Long productId;
    private Long customerId;
    private Integer starRating;

}
