package com.swiftcart.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateReviewDTO {

    @NotNull(message = "Customer ID should not be null")
    private Long customerId;

    @NotBlank(message = "Review should not be blank")
    @Size(max = 250)
    private String reviewText;
}
