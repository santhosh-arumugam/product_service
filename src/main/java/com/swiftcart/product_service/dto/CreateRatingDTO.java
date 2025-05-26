package com.swiftcart.product_service.dto;

import com.swiftcart.product_service.enums.StarRating;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRatingDTO {
    private Long customerId;
    private StarRating starRating;
}
