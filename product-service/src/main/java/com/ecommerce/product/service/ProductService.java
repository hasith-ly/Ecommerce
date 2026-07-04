package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;

import java.util.List;

/**
 * Product business-logic contract. Depending on this abstraction rather than a
 * concrete class keeps the controller decoupled (Dependency Inversion Principle).
 */
public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long productId);

    List<ProductResponse> getAllProducts();

    void deleteProduct(Long productId);
}
