package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import org.springframework.stereotype.Component;

/**
 * Converts between the Product entity and its DTOs, keeping mapping logic
 * out of the service (Single Responsibility Principle).
 */
@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .unitPrice(request.getUnitPrice())
                .description(request.getDescription())
                .category(request.getCategory())
                .stock(request.getStock())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .unitPrice(product.getUnitPrice())
                .description(product.getDescription())
                .category(product.getCategory())
                .stock(product.getStock())
                .build();
    }
}
