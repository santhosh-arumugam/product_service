package com.swiftcart.product_service.mapper;

import com.swiftcart.product_service.dto.CreateProductDTO;
import com.swiftcart.product_service.dto.ProductResponseDTO;
import com.swiftcart.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProductEntity(CreateProductDTO dto);

    ProductResponseDTO toProductResponseDTO(Product product);
}
