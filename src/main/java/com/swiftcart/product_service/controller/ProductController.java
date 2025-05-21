package com.swiftcart.product_service.controller;

import com.swiftcart.product_service.dto.CreateProductDTO;
import com.swiftcart.product_service.dto.ProductResponseDTO;
import com.swiftcart.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody CreateProductDTO dto) {
        ProductResponseDTO result = productService.createProduct(dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }






}
