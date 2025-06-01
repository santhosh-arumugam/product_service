package com.swiftcart.product_service.controller;

import com.swiftcart.product_service.dto.CreateRatingDTO;
import com.swiftcart.product_service.dto.DeleteRatingDTO;
import com.swiftcart.product_service.dto.RatingResponseDTO;
import com.swiftcart.product_service.service.RatingService;
import jakarta.validation.Valid;
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
    public ResponseEntity<RatingResponseDTO> createRating(@Valid @RequestBody CreateRatingDTO dto, @PathVariable Long productId) {
        RatingResponseDTO response = ratingService.createRating(dto, productId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{ratingId}")
    public ResponseEntity<RatingResponseDTO> updateRating(@Valid @RequestBody CreateRatingDTO dto, @PathVariable Long productId, @PathVariable Long ratingId) {
        RatingResponseDTO response = ratingService.updateRating(dto, productId, ratingId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{ratingId}")
    public ResponseEntity<Void> deleteRating(@Valid @RequestBody DeleteRatingDTO dto, @PathVariable Long productId, @PathVariable Long ratingId) {
        System.out.println("Request received to delete a StarRating for Product ID: "+productId+" Customer ID: "+dto.getCustomerId()+" Rating ID: "+ratingId);
        ratingService.deleteRating(dto, productId, ratingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
