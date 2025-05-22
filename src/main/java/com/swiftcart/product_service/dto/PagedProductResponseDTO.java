package com.swiftcart.product_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PagedProductResponseDTO {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Map<String, Object> variants;
    private String imageUrl;

}
