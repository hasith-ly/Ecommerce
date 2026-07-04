package com.ecommerce.order.service.impl;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.messaging.OrderEventPublisher;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order business logic. When an order is created it:
 *   1. Calls the Product Service (REST) to fetch product details.
 *   2. Calculates the total price.
 *   3. Stores the order in the database.
 *   4. Publishes an order event to RabbitMQ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Fetch product details from the Product Service.
        ProductResponse product = productClient.getProductById(request.getProductId());

        // 2. Calculate total price.
        double totalPrice = product.getUnitPrice() * request.getQuantity();

        // 3. Store the order.
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .productId(product.getProductId())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .status("CREATED")
                .build();
        Order saved = orderRepository.save(order);
        log.info("Stored order id={} totalPrice={}", saved.getOrderId(), saved.getTotalPrice());

        // 4. Publish an event to RabbitMQ for the Notification Service.
        orderEventPublisher.publishOrderCreated(orderMapper.toEvent(saved));

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id " + orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}
