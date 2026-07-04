package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductResponse;

/**
 * Abstraction over the Product Service REST API. The service layer depends on
 * this interface, not on a concrete HTTP implementation (Dependency Inversion).
 */
public interface ProductClient {

    ProductResponse getProductById(Long productId);
}
