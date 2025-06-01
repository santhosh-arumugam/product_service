package com.swiftcart.product_service.service;

import com.swiftcart.product_service.dto.CreateRatingDTO;
import com.swiftcart.product_service.dto.DeleteRatingDTO;
import com.swiftcart.product_service.dto.RatingResponseDTO;
import com.swiftcart.product_service.entity.Product;
import com.swiftcart.product_service.entity.Rating;
import com.swiftcart.product_service.mapper.RatingMapper;
import com.swiftcart.product_service.repository.ProductRepository;
import com.swiftcart.product_service.repository.RatingRepository;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final ProductRepository productRepository;

    @Autowired
    RatingService(RatingRepository ratingRepository, RatingMapper ratingMapper, ProductRepository productRepository) {
        this.ratingRepository = ratingRepository;
        this.ratingMapper = ratingMapper;
        this.productRepository = productRepository;
    }

    @Transactional
    public RatingResponseDTO createRating(CreateRatingDTO dto, Long productId) {
        Optional<Rating> existingRating = ratingRepository.findByCustomerIdAndProductId(dto.getCustomerId(), productId);
        if (existingRating.isPresent()) {
            throw new DuplicateResourceException("Existing Rating available for this product ID: "+productId+" by this customer ID: "+ dto.getCustomerId()); }

        Rating result = ratingMapper.toCreateEntity(dto);

        Product product = productRepository.findById(productId)
                        .orElseThrow(()-> new ResourceNotFoundException("Product not found to add rating"));
        result.setProduct(product);

        Rating savedRating = ratingRepository.save(result);
        return ratingMapper.toRatingResponseDTO(savedRating);
    }

    @Transactional
    public RatingResponseDTO updateRating(CreateRatingDTO dto, Long productId, Long ratingId) {
        Optional<Rating> existingRating = ratingRepository.findByCustomerIdAndProductIdAndRatingId(dto.getCustomerId(), productId, ratingId);
        if (existingRating.isEmpty()) {
            throw new ResourceNotFoundException("Existing Rating not available to update for this Product ID: "+productId+" Customer ID: "+ dto.getCustomerId() + " Rating ID: "+ratingId); }

        Rating ratingEntity = ratingMapper.toCreateEntity(dto);
        ratingEntity.setRatingId(ratingId);
        ratingEntity.setProduct(existingRating.get().getProduct());
        Rating response = ratingRepository.save(ratingEntity);
        return ratingMapper.toRatingResponseDTO(response);
    }

    @Transactional
    public void deleteRating(DeleteRatingDTO dto, Long productId, Long ratingId) {
        Optional<Rating> existingRating = ratingRepository.findByCustomerIdAndProductIdAndRatingId(dto.getCustomerId(), productId, ratingId);
        if (existingRating.isEmpty()) {
            throw new ResourceNotFoundException("Existing Rating not available to delete for this Product ID: "+productId+" Customer ID: "+ dto.getCustomerId()+ " Rating ID: "+ratingId); }
        ratingRepository.deleteById(ratingId);
    }
}
