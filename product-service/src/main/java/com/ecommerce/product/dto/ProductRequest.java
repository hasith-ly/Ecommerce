package com.ecommerce.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Incoming payload for creating a product. Validated at the controller boundary.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "unitPrice is required")
    @PositiveOrZero(message = "unitPrice must be zero or greater")
    private Double unitPrice;

    private String description;

    private String category;

    @NotNull(message = "stock is required")
    @Min(value = 0, message = "stock must be zero or greater")
    private Integer stock;
}
