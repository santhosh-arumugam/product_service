package com.swiftcart.product_service.mapper;

import com.swiftcart.product_service.dto.CreateProductDTO;
import com.swiftcart.product_service.dto.PagedProductResponseDTO;
import com.swiftcart.product_service.dto.ProductResponseDTO;
import com.swiftcart.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProductEntity(CreateProductDTO dto);

    @Mapping(source = "id", target = "productId")
    Product toUpdateEntity(CreateProductDTO dto, Long id);

    ProductResponseDTO toProductResponseDTO(Product product);

    PagedProductResponseDTO toPagedProductResponseDTO(Product products);
}
