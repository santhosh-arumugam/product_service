package com.swiftcart.product_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeleteReviewDTO {

    @NotNull(message = "Customer ID should not be null")
    private Long customerId;

}
