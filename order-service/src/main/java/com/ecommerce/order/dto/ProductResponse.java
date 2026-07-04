package com.ecommerce.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * View of a product as returned by the Product Service REST API.
 * Only the fields the Order Service needs are declared.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {

    private Long productId;
    private String name;
    private Double unitPrice;
    private Integer stock;
}
