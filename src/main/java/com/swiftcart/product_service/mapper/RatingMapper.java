package com.swiftcart.product_service.mapper;

import com.swiftcart.product_service.dto.CreateRatingDTO;
import com.swiftcart.product_service.dto.RatingResponseDTO;
import com.swiftcart.product_service.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    Rating toCreateEntity(CreateRatingDTO dto);

    @Mapping(source = "product.productId", target = "productId")
    RatingResponseDTO toRatingResponseDTO(Rating rating);
}
