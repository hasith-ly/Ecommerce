package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderCreatedEvent;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.messaging.OrderEventPublisher;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the order business logic. The Product Service call, the
 * repository and the RabbitMQ publisher are all mocked.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private OrderEventPublisher orderEventPublisher;

    private final OrderMapper orderMapper = new OrderMapper();

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, productClient,
                orderEventPublisher, orderMapper);
    }

    @Test
    @DisplayName("createOrder calculates total price, saves and publishes an event")
    void createOrder_success() {
        OrderRequest request = OrderRequest.builder()
                .customerId(7L).productId(1L).quantity(3).build();
        ProductResponse product = ProductResponse.builder()
                .productId(1L).name("Wireless Mouse").unitPrice(25.0).stock(100).build();
        when(productClient.getProductById(1L)).thenReturn(product);
        // Repository returns the saved order with a generated id.
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setOrderId(50L);
            return o;
        });

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.getOrderId()).isEqualTo(50L);
        assertThat(response.getProductName()).isEqualTo("Wireless Mouse");
        // 25.0 * 3 = 75.0
        assertThat(response.getTotalPrice()).isEqualTo(75.0);
        assertThat(response.getStatus()).isEqualTo("CREATED");

        // Verify the published event carries the correct data.
        ArgumentCaptor<OrderCreatedEvent> captor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(orderEventPublisher, times(1)).publishOrderCreated(captor.capture());
        assertThat(captor.getValue().getTotalPrice()).isEqualTo(75.0);
        assertThat(captor.getValue().getOrderId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("createOrder propagates not-found and never saves when product is missing")
    void createOrder_productNotFound() {
        OrderRequest request = OrderRequest.builder()
                .customerId(7L).productId(999L).quantity(1).build();
        when(productClient.getProductById(999L))
                .thenThrow(new ResourceNotFoundException("Product not found with id 999"));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, never()).save(any());
        verify(orderEventPublisher, never()).publishOrderCreated(any());
    }

    @Test
    @DisplayName("getOrderById returns the order when it exists")
    void getOrderById_found() {
        Order order = Order.builder()
                .orderId(50L).customerId(7L).productId(1L).productName("Wireless Mouse")
                .quantity(3).totalPrice(75.0).orderDate(LocalDateTime.now()).status("CREATED")
                .build();
        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(50L);

        assertThat(response.getCustomerId()).isEqualTo(7L);
    }

    @Test
    @DisplayName("getOrderById throws when the order does not exist")
    void getOrderById_notFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
