package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.exception.ProductServiceException;
import com.ecommerce.order.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * REST implementation of {@link ProductClient} using RestTemplate.
 * The Product Service base URL is injected from configuration (env variable).
 */
@Component
@Slf4j
public class RestProductClient implements ProductClient {

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public RestProductClient(RestTemplate restTemplate,
                             @Value("${product.service.url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        String url = productServiceUrl + "/products/" + productId;
        log.info("Calling Product Service: GET {}", url);
        try {
            return restTemplate.getForObject(url, ProductResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Product not found with id " + productId);
        } catch (RestClientException ex) {
            log.error("Product Service call failed: {}", ex.getMessage());
            throw new ProductServiceException(
                    "Unable to reach Product Service for product id " + productId);
        }
    }
}
