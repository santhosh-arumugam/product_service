package com.swiftcart.product_service.controller;

import com.swiftcart.product_service.dto.CreateRatingDTO;
import com.swiftcart.product_service.dto.RatingResponseDTO;
import com.swiftcart.product_service.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/ratings")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingResponseDTO> createRating(@RequestBody CreateRatingDTO dto, @PathVariable Long productId) {
        RatingResponseDTO response = ratingService.createRating(dto, productId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
