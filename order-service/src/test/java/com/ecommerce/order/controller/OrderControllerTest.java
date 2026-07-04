package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for the OrderController using MockMvc.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_returns201() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .customerId(7L).productId(1L).quantity(3).build();
        OrderResponse response = OrderResponse.builder()
                .orderId(50L).customerId(7L).productId(1L).productName("Wireless Mouse")
                .quantity(3).totalPrice(75.0).orderDate(LocalDateTime.now()).status("CREATED")
                .build();
        when(orderService.createOrder(any())).thenReturn(response);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(50))
                .andExpect(jsonPath("$.totalPrice").value(75.0));
    }

    @Test
    void createOrder_invalidQuantity_returns400() throws Exception {
        OrderRequest invalid = OrderRequest.builder()
                .customerId(7L).productId(1L).quantity(0).build();

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_missing_returns404() throws Exception {
        when(orderService.getOrderById(1L))
                .thenThrow(new ResourceNotFoundException("Order not found with id 1"));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isNotFound());
    }
}
