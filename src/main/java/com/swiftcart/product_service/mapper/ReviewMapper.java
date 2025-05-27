package com.swiftcart.product_service.mapper;

import com.swiftcart.product_service.dto.CreateReviewDTO;
import com.swiftcart.product_service.dto.ReviewResponseDTO;
import com.swiftcart.product_service.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "productId", target = "product.productId")
    Review toEntity(CreateReviewDTO dto, Long productId);

    @Mapping(source = "product.productId", target = "productId")
    ReviewResponseDTO toResponseDTO(Review review);
}
