# Product Service for SwiftCart

The **Product Service** is a core component of the **SwiftCart** e-commerce platform, responsible for managing the product catalog in a microservices architecture. It handles product metadata, customer-facing product searches, ratings, and reviews, and integrates with other services (e.g., Inventory, Order) via Kafka events. Built with **Spring Boot**, **Java 21**, **PostgreSQL**, **Redis** and **Kafka**, the service ensures real-time catalog management, scalability, and observability.

## Features
- **Product Catalog Management**: Create, update, delete, and retrieve product details (e.g., name, description, price, category, variants, image URL).
- **Customer Search**: Enable customers to search products by name, category, or other filters, with pagination and sorting.
- **Ratings and Reviews**: Allow customers to add, update, or delete star ratings and reviews for products.
- **Event-Driven Integration**: Publish events (e.g., `PRODUCT_CREATED`, `PRODUCT_UPDATED`) to Kafka and consume events (e.g., `STOCK_UPDATED`) for synchronization with the **Inventory Service**.
- **Fault Tolerance**: Implement retries, idempotency (via Redis), and transactional outbox for reliable operations.
- **Observability**:
  - Centralized logging with trace/span IDs (Micrometer Tracing + OpenTelemetry).
  - Health endpoints (`/actuator/health`) and Prometheus metrics (`/actuator/prometheus`).
  - Grafana dashboards for visualizing metrics and traces.
- **Security**: Restrict admin APIs (create, update, delete) via JWT authentication through the **API Gateway**.
- **API Documentation**: Available via Swagger UI (`/swagger-ui.html`).

## Technologies
- **Java**: 21
- **Spring Boot**: 3.4.5
- **Database**: PostgreSQL (with `JSONB` for event data)
- **Messaging**: Apache Kafka (event-driven communication)
- **Caching**: Redis (for idempotency and search caching)
- **Observability**: Micrometer Tracing, OpenTelemetry, Prometheus, Grafana, Jaeger
- **Dependencies**: Spring Web, Spring Data JPA, Spring Kafka, Spring Retry, Lombok, Springdoc OpenAPI

## Project Structure
```
product-service/
├── src/
│   ├── main/
│   │   ├── java/com/swiftcart/productservice/
│   │   │   ├── controller/    # REST API controllers
│   │   │   ├── service/       # Business logic
│   │   │   ├── repository/    # JPA repositories
│   │   │   ├── entity/       # Product, Rating, Review entities
│   │   │   ├── dto/          # Data transfer objects
│   │   │   ├── config/       # Configuration (Kafka, OpenTelemetry, etc.)
│   │   ├── resources/
│   │       ├── application.yml  # Configuration properties
│   ├── test/                  # Unit and integration tests
├── pom.xml                    # Maven dependencies
├── README.md                  # This file
```

## Entities
The **Product Service** uses three PostgreSQL tables to manage products, ratings, and reviews:

### Product (`products`)
| Column Name         | Data Type         | Constraints                     | Description                                                                 |
|---------------------|-------------------|---------------------------------|-----------------------------------------------------------------------------|
| `product_id`        | VARCHAR(50)       | PRIMARY KEY                     | Unique identifier for the product (e.g., "P1").                             |
| `name`              | VARCHAR(255)      | NOT NULL                        | Product name (e.g., "iPhone 14").                                          |
| `description`       | TEXT              |                                 | Product description.                                                       |
| `price`             | DECIMAL(10,2)     | NOT NULL                        | Product price (e.g., 799.99).                                              |
| `category`          | VARCHAR(50)       | NOT NULL                        | Product category (e.g., "Electronics").                                    |
| `variants`          | JSONB             |                                 | Product variants (e.g., `[{"color": "Black", "size": "128GB"}]`).          |
| `image_url`         | VARCHAR(255)      |                                 | URL of the product image.                                                  |
| `created_at`        | TIMESTAMP         | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Timestamp when the product was created.                                |
| `updated_at`        | TIMESTAMP         | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Timestamp when the product was last updated.                           |

### Rating (`ratings`)
| Column Name         | Data Type         | Constraints                     | Description                                                                 |
|---------------------|-------------------|---------------------------------|-----------------------------------------------------------------------------|
| `rating_id`         | BIGINT            | PRIMARY KEY, AUTO_INCREMENT     | Unique identifier for the rating.                                           |
| `product_id`        | VARCHAR(50)       | FOREIGN KEY, NOT NULL           | References the **Product**.                                                |
| `customer_id`       | VARCHAR(50)       | NOT NULL                        | Identifier of the customer (from User Service).                             |
| `star_rating`       | INTEGER           | NOT NULL, CHECK (1 <= star_rating <= 5) | Star rating (1 to 5).                                              |
| `created_at`        | TIMESTAMP         | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Timestamp when the rating was created.                                 |
| `updated_at`        | TIMESTAMP         | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Timestamp when the rating was last updated.                            |

### Review (`reviews`)
| Column Name         | Data Type         | Constraints                     | Description                                                                 |
|---------------------|-------------------|---------------------------------|-----------------------------------------------------------------------------|
| `review_id`         | BIGINT            | PRIMARY KEY, AUTO_INCREMENT     | Unique identifier for the review.                                           |
| `product_id`        | VARCHAR(50)       | FOREIGN KEY, NOT NULL           | References the **Product**.                                                |
| `customer_id`       | VARCHAR(50)       | NOT NULL                        | Identifier of the customer (from User Service).                             |
| `review_text`       | TEXT              | NOT NULL                        | Review content.                                                            |
| `created_at`        | TIMESTAMP         | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Timestamp when the review was created.                                 |
| `updated_at`        | TIMESTAMP         | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Timestamp when the review was last updated.                            |

**Relationships**:
- **Product to Rating**: One-to-Many (via `product_id` foreign key).
- **Product to Review**: One-to-Many (via `product_id` foreign key).
- **Rating/Review to Product**: Many-to-One (ensures ratings/reviews are tied to a product).

## Functionalities
1. **Create Product**: Add a new product to the catalog (admin-only).
2. **Update Product**: Modify product details (e.g., price, variants) (admin-only).
3. **Delete Product**: Remove a product from the catalog (admin-only).
4. **Get All Products**: Retrieve products with filtering (e.g., name, category), pagination, and sorting.
5. **Get Product by ID**: Retrieve details of a specific product.
6. **Add/Update Ratings**: Allow customers to submit or update star ratings (1-5) for products.
7. **Add/Update/Delete Reviews**: Allow customers to submit, update, or delete product reviews.
8. **Event Production**: Publish events (e.g., `PRODUCT_CREATED`, `PRODUCT_UPDATED`, `PRODUCT_DELETED`) to Kafka.
9. **Event Consumption**: Consume events (e.g., `STOCK_UPDATED` from **Inventory Service**) to update cached stock data.
10. **Observability**: Log requests, trace operations, and expose metrics for monitoring.

## API Endpoints
The **Product Service** exposes public REST APIs (versioned at `/api/v1/`), accessible via the **API Gateway** with JWT authentication. Customer-facing APIs are read-only or restricted to ratings/reviews, while admin APIs require elevated permissions.

| Method | Endpoint                              | Description                              | Access          |
|--------|---------------------------------------|------------------------------------------|-----------------|
| GET    | `/api/v1/products`                    | Search products by name, category, etc. (paginated, sorted) | Public (customers) |
| GET    | `/api/v1/products/{productId}`        | Get details of a specific product        | Public (customers) |
| POST   | `/api/v1/products`                    | Create a new product                     | Admin           |
| PATCH  | `/api/v1/products/{productId}`        | Update product details                   | Admin           |
| DELETE | `/api/v1/products/{productId}`        | Delete a product                         | Admin           |
| POST   | `/api/v1/products/{productId}/ratings`| Add or update a star rating              | Authenticated (customers) |
| POST   | `/api/v1/products/{productId}/reviews`| Add a review                             | Authenticated (customers) |
| PATCH  | `/api/v1/products/{productId}/reviews/{reviewId}` | Update a review                | Authenticated (customers) |
| DELETE | `/api/v1/products/{productId}/reviews/{reviewId}` | Delete a review                | Authenticated (customers) |


**API Documentation**: Available at `/swagger-ui.html` (via Springdoc OpenAPI).

## Observability
The **Product Service** is equipped with observability features for monitoring and debugging, consistent with the **Order Service**:
- **Centralized Logging**:
  - Uses **Micrometer Tracing** and **OpenTelemetry** to add trace/span IDs to logs.
  - Logs exported to **Jaeger** for distributed tracing.
  - Log format: `2025-05-06 10:00:00 [traceId/spanId] INFO ...`.
- **Health Endpoints**:
  - Exposed via Spring Boot Actuator: `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`.
  - Monitors connectivity to **PostgreSQL**, **Kafka**, and **Redis**.
- **Prometheus Metrics**:
  - Metrics (e.g., search latency, rating updates) available at `/actuator/prometheus`.
  - Scraped by **Prometheus** for monitoring.
- **Grafana Dashboard**:
  - Visualizes Prometheus metrics and Jaeger traces.
  - Displays API performance, product searches, and service health.

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd product-service
   ```

2. **Configure Dependencies**:
   - Ensure `pom.xml` includes required dependencies (see below).
   - Run `mvn clean install` to download dependencies.

3. **Set Up PostgreSQL**:
   - Create a database:
     ```sql
     CREATE DATABASE swiftcart;
     ```
   - Apply the schema:
     ```sql
     CREATE TABLE products (
         product_id VARCHAR(50) PRIMARY KEY,
         name VARCHAR(255) NOT NULL,
         description TEXT,
         price DECIMAL(10,2) NOT NULL,
         category VARCHAR(50) NOT NULL,
         variants JSONB,
         image_url VARCHAR(255),
         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
     );

     CREATE TABLE ratings (
         rating_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
         product_id VARCHAR(50) NOT NULL,
         customer_id VARCHAR(50) NOT NULL,
         star_rating INTEGER NOT NULL CHECK (star_rating >= 1 AND star_rating <= 5),
         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
     );

     CREATE TABLE reviews (
         review_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
         product_id VARCHAR(50) NOT NULL,
         customer_id VARCHAR(50) NOT NULL,
         review_text TEXT NOT NULL,
         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
     );
     ```

4. **Configure Application**:
   - Edit `src/main/resources/application.yml`:
     ```yaml
     spring:
       application:
         name: product-service
       datasource:
         url: jdbc:postgresql://localhost:5432/swiftcart
         username: postgres
         password: password
         driver-class-name: org.postgresql.Driver
       jpa:
         hibernate:
           ddl-auto: update
         properties:
           hibernate:
             dialect: org.hibernate.dialect.PostgreSQLDialect
       kafka:
         bootstrap-servers: localhost:9092
         producer:
           key-serializer: org.apache.kafka.common.serialization.StringSerializer
           value-serializer: org.apache.kafka.common.serialization.StringSerializer
         consumer:
           group-id: product-service
           auto-offset-reset: earliest
           key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
           value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
       data:
         redis:
           host: localhost
           port: 6379
       otel:
         exporter:
           otlp:
             endpoint: http://localhost:4317
         resource:
           attributes:
             service.name: product-service
         traces:
           sampler:
             probability: 1.0
     management:
       endpoints:
         web:
           exposure:
             include: health,metrics,prometheus
       metrics:
         export:
           prometheus:
             enabled: true
         tags:
           application: ${spring.application.name}
     server:
       port: 8081
     springdoc:
       api-docs:
         path: /api-docs
       swagger-ui:
         path: /swagger-ui.html
     logging:
       pattern:
         console: "%d{yyyy-MM-dd HH:mm:ss} [%X{traceId}/%X{spanId}] %-5level %logger{36} - %msg%n"
       level:
         root: INFO
         com.swiftcart: DEBUG
     ```

5. **Set Up Kafka**:
   - Run Kafka and ZooKeeper (e.g., via Docker):
     ```bash
     docker-compose -f kafka-docker-compose.yml up -d
     ```
   - Create a topic: `product-events`.

6. **Set Up Redis**:
   - Run Redis:
     ```bash
     docker run -d --name redis -p 6379:6379 redis
     ```

7. **Set Up Observability**:
   - **Jaeger**:
     ```bash
     docker run -d --name jaeger -e COLLECTOR_OTLP_ENABLED=true -p 16686:16686 -p 4317:4317 jaegertracing/all-in-one
     ```
   - **Prometheus**:
     - Create `prometheus.yml`:
       ```yaml
       global:
         scrape_interval: 15s
       scrape_configs:
         - job_name: 'product-service'
           metrics_path: '/actuator/prometheus'
           static_configs:
             - targets: ['localhost:8081']
       ```
     - Run:
       ```bash
       docker run -d --name prometheus -p 9090:9090 -v /path/to/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus
       ```
   - **Grafana**:
     ```bash
     docker run -d --name grafana -p 3000:3000 grafana/grafana
     ```

8. **Run the Service**:
   ```bash
   mvn spring-boot:run
   ```
   - Access APIs at `http://localhost:8081/api/v1/products`.
   - View Swagger UI at `http://localhost:8081/swagger-ui.html`.

## Dependencies
The `pom.xml` includes:
- Spring Boot Starter Web
- Spring Data JPA
- PostgreSQL Driver
- Spring Kafka
- Spring Validation
- Lombok
- Spring Boot Actuator
- Micrometer Tracing Bridge OTEL
- OpenTelemetry Exporter OTLP
- Micrometer Registry Prometheus
- Jackson Databind
- Spring Retry
- Spring Boot Configuration Processor
- Spring Data Redis
- Springdoc OpenAPI

See the **Order Service** `pom.xml` for a similar structure, adjusted for `product-service`.

## Event Integration
- **Produced Events**:
  - `PRODUCT_CREATED`: New product added.
  - `PRODUCT_UPDATED`: Product details updated.
  - `PRODUCT_DELETED`: Product removed.
- **Consumed Events**:
  - `STOCK_UPDATED`: Update cached stock availability from **Inventory Service**.
- **Mechanism**: Uses the transactional outbox pattern with a `product_event_log` table (similar to **Order Service**).
