package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the product business logic (service layer) using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    // A real mapper keeps the test focused on service behaviour, not mapping stubs.
    private final ProductMapper productMapper = new ProductMapper();

    private ProductServiceImpl productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, productMapper);
        sampleProduct = Product.builder()
                .productId(1L)
                .name("Wireless Mouse")
                .unitPrice(25.0)
                .description("Ergonomic 2.4GHz mouse")
                .category("Accessories")
                .stock(100)
                .build();
    }

    @Test
    @DisplayName("createProduct persists and returns the saved product")
    void createProduct_returnsSavedProduct() {
        ProductRequest request = ProductRequest.builder()
                .name("Wireless Mouse").unitPrice(25.0).description("Ergonomic 2.4GHz mouse")
                .category("Accessories").stock(100).build();
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        ProductResponse response = productService.createProduct(request);

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Wireless Mouse");
        assertThat(response.getUnitPrice()).isEqualTo(25.0);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("getProductById returns the product when it exists")
    void getProductById_found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        ProductResponse response = productService.getProductById(1L);

        assertThat(response.getName()).isEqualTo("Wireless Mouse");
    }

    @Test
    @DisplayName("getProductById throws ResourceNotFoundException when missing")
    void getProductById_notFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("getAllProducts maps every entity to a response")
    void getAllProducts_returnsList() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("deleteProduct removes an existing product")
    void deleteProduct_existing() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProduct throws when the product does not exist")
    void deleteProduct_missing() {
        when(productRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(42L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(productRepository, times(0)).deleteById(42L);
    }
}
