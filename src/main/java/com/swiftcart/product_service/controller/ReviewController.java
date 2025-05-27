package com.swiftcart.product_service.controller;

import com.swiftcart.product_service.dto.CreateReviewDTO;
import com.swiftcart.product_service.dto.ReviewResponseDTO;
import com.swiftcart.product_service.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody CreateReviewDTO dto, @PathVariable Long productId) {
        ReviewResponseDTO response = reviewService.createReview(dto, productId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
