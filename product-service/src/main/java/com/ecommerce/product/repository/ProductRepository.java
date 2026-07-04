package com.ecommerce.product.repository;

import com.ecommerce.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository layer. Spring Data JPA supplies the CRUD implementation at runtime.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
