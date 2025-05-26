package com.swiftcart.product_service.dto;

import com.swiftcart.product_service.entity.Product;
import com.swiftcart.product_service.enums.StarRating;
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
    private StarRating starRating;

}
