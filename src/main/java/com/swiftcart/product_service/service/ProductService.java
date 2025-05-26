package com.swiftcart.product_service.service;

import com.swiftcart.product_service.dto.CreateProductDTO;
import com.swiftcart.product_service.dto.PagedProductResponseDTO;
import com.swiftcart.product_service.dto.ProductResponseDTO;
import com.swiftcart.product_service.entity.Product;
import com.swiftcart.product_service.mapper.ProductMapper;
import com.swiftcart.product_service.repository.ProductRepository;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
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

    @Transactional
    public PagedProductResponseDTO getProductById(Long id) {
        Product fetchedProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not available for this ID: " + id));
        return productMapper.toPagedProductResponseDTO(fetchedProduct);
    }

    @Transactional
    public void deleteProductById(Long id) {
        if (productRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Product not available for this ID: "+ id +" to delete.");
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public PagedProductResponseDTO updateById(Long id, CreateProductDTO dto) {
        Product fetchedProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not available to update for this ID: "+ id));
        Product result = productMapper.toUpdateEntity(dto, fetchedProduct.getProductId());
        Product updatedProduct = productRepository.save(result);
        return productMapper.toPagedProductResponseDTO(updatedProduct);
    }

}
