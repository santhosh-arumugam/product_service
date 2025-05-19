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
The **Product Service** uses three PostgreSQL tables to manage products, ratings, reviews and product event logs:

### 1. products
Stores information about products.

| Column Name   | Data Type     | Constraints                 | Description                              |
|---------------|---------------|-----------------------------|------------------------------------------|
| productId     | BIGINT        | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for the product        |
| name          | VARCHAR(125)  | NOT NULL                    | Product name                             |
| description   | VARCHAR(1250) | NOT NULL                    | Product description                      |
| price         | NUMERIC(10,2) | NOT NULL                    | Product price (e.g., 99.99)              |
| category      | TEXT          | NOT NULL                    | Product category                         |
| variants      | JSONB         |                             | Product variants (e.g., size, color)     |
| imageUrl      | TEXT          | NOT NULL                    | URL to product image                     |
| createdAt     | TIMESTAMP     | NOT NULL                    | Creation timestamp                       |
| updatedAt     | TIMESTAMP     | NOT NULL                    | Last update timestamp                    |

### 2. ratings
Stores customer ratings for products.

| Column Name   | Data Type    | Constraints                      | Description                              |
|---------------|--------------|----------------------------------|------------------------------------------|
| ratingId      | BIGINT       | PRIMARY KEY, AUTO_INCREMENT      | Unique identifier for the rating         |
| productId     | BIGINT       | NOT NULL, FOREIGN KEY (products) | References the product being rated       |
| customerId    | BIGINT       | NOT NULL                         | Identifier of the customer               |
| starRating    | INTEGER      | NOT NULL                         | Rating value (e.g., 1-5 stars)           |
| createdAt     | TIMESTAMP    | NOT NULL                         | Creation timestamp                       |
| updatedAt     | TIMESTAMP    | NOT NULL                         | Last update timestamp                    |

### 3. reviews
Stores customer reviews for products.

| Column Name   | Data Type    | Constraints                      | Description                              |
|---------------|--------------|----------------------------------|------------------------------------------|
| reviewId      | BIGINT       | PRIMARY KEY, AUTO_INCREMENT      | Unique identifier for the review         |
| productId     | BIGINT       | NOT NULL, FOREIGN KEY (products) | References the product being reviewed    |
| customerId    | BIGINT       | NOT NULL                         | Identifier of the customer               |
| ReviewText    | VARCHAR(250) | NOT NULL                         | Review text                              |
| createdAt     | TIMESTAMP    | NOT NULL                         | Creation timestamp                       |
| updatedAt     | TIMESTAMP    | NOT NULL                         | Last update timestamp                    |

### 4. product_event_logs
Stores event logs related to products.

| Column Name   | Data Type           | Constraints                          | Description                              |
|---------------|---------------------|--------------------------------------|------------------------------------------|
| eventId       | BIGINT              | PRIMARY KEY, AUTO_INCREMENT          | Unique identifier for the event          |
| productId     | BIGINT              | NOT NULL, FOREIGN KEY (products)     | References the product                   |
| eventType     | INTEGER             | NOT NULL                             | Event type (enum ordinal)                |
| eventData     | JSONB               |                                      | Additional event data                    |
| timeStamp     | TIMESTAMP WITH TIME ZONE | NOT NULL                        | Event timestamp                          |
| sent          | BOOLEAN             | NOT NULL, DEFAULT FALSE              | Whether the event was sent               |

## Relationships

| Source Table         | Source Column | Target Table | Target Column | Relationship Type |
|----------------------|---------------|--------------|---------------|-------------------|
| ratings              | productId     | products     | productId     | Many-to-One       |
| reviews              | productId     | products     | productId     | Many-to-One       |
| product_event_logs   | productId     | products     | productId     | Many-to-One       |


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
