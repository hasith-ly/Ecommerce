package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.OrderCreatedEvent;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import org.springframework.stereotype.Component;

/**
 * Maps the Order entity to its response DTO and to the RabbitMQ event.
 */
@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .build();
    }

    public OrderCreatedEvent toEvent(Order order) {
        return OrderCreatedEvent.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .timestamp(order.getOrderDate())
                .message("Order " + order.getOrderId() + " created for customer "
                        + order.getCustomerId())
                .build();
    }
}
