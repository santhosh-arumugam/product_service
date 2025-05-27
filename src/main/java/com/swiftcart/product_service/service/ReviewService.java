package com.swiftcart.product_service.service;

import com.swiftcart.product_service.dto.CreateReviewDTO;
import com.swiftcart.product_service.dto.ReviewResponseDTO;
import com.swiftcart.product_service.entity.Product;
import com.swiftcart.product_service.entity.Review;
import com.swiftcart.product_service.mapper.ReviewMapper;
import com.swiftcart.product_service.repository.ProductRepository;
import com.swiftcart.product_service.repository.ReviewRepository;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.reviewMapper = reviewMapper;
    }

    @Transactional
    public ReviewResponseDTO createReview(CreateReviewDTO dto, Long productId) {
        if (reviewRepository.findByCustomerIdAndProductId(dto.getCustomerId(), productId).isPresent()) {
            throw new DuplicateResourceException("Review already exists for this Product ID :"+productId+" by this Customer ID: "+dto.getCustomerId());
        }
        Review result = reviewMapper.toEntity(dto, productId);

        Product fetchedProduct = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not available to add review"));

        result.setProduct(fetchedProduct);

        Review savedReview = reviewRepository.save(result);
        return reviewMapper.toResponseDTO(savedReview);
    }
}
