package com.swiftcart.product_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@Table(name = "ratings")
@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @ManyToOne()
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Integer starRating;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

}
