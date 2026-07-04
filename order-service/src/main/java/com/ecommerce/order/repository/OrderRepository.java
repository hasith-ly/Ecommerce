package com.ecommerce.order.repository;

import com.ecommerce.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository layer for orders.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
