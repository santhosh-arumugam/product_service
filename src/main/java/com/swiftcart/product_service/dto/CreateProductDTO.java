package com.swiftcart.product_service.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductDTO {

    @Size(max = 250)
    @NotBlank(message = "Product Name should not be blank")
    private String name;

    @Size(max = 1250)
    @NotBlank(message = "Product Description should not be blank")
    private String description;

    @Positive(message = "Product Price should always be positive numbers")
    @NotNull(message = "Product Price should not be null")
    private BigDecimal price;

    @NotBlank(message = "Product Category should not be blank")
    private String category;

    private Map<String, Object> variants;

    @NotEmpty(message = "Product image should not be empty")
    private String imageUrl;
}
