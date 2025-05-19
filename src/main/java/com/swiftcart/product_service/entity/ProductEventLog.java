package com.swiftcart.product_service.entity;

import com.swiftcart.product_service.enums.ProductEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "productEventLogs")
@Entity
public class ProductEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne()
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Enumerated(EnumType.ORDINAL)
    private ProductEventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> eventData;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime timeStamp;

    @Column(nullable = false)
    private Boolean sent = false;

}
