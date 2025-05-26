package com.swiftcart.product_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRatingDTO {
    @NotNull(message = "Customer ID should not be null")
    private Long customerId;
    @NotNull(message = "StarRating should not be null")
    @Min(1)
    @Max(5)
    private Integer starRating;
}
