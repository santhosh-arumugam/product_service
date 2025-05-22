package com.swiftcart.product_service.service;

import com.swiftcart.product_service.dto.CreateProductDTO;
import com.swiftcart.product_service.dto.PagedProductResponseDTO;
import com.swiftcart.product_service.dto.ProductResponseDTO;
import com.swiftcart.product_service.entity.Product;
import com.swiftcart.product_service.mapper.ProductMapper;
import com.swiftcart.product_service.repository.ProductRepository;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponseDTO createProduct(CreateProductDTO dto) {
        if (productRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateResourceException("Product name already exists");
        }
        Product savedProduct = productRepository.save(productMapper.toProductEntity(dto));
        return productMapper.toProductResponseDTO(savedProduct);
    }

    @Transactional
    public Page<PagedProductResponseDTO> getAllProducts(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size, sort);
        Page<Product> pagedProducts = productRepository.findAll(pageable);
        return pagedProducts.map(productMapper::toPagedProductResponseDTO);
    }

}
