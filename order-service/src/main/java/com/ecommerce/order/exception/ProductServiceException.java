package com.ecommerce.order.exception;

/**
 * Thrown when the Product Service is unreachable or returns an unexpected error.
 * Mapped to HTTP 502 (Bad Gateway).
 */
public class ProductServiceException extends RuntimeException {

    public ProductServiceException(String message) {
        super(message);
    }
}
