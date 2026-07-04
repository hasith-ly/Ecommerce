package com.ecommerce.order.messaging;

import com.ecommerce.order.dto.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publishes {@link OrderCreatedEvent} messages to RabbitMQ.
 */
@Component
@Slf4j
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${rabbitmq.exchange:order-exchange}") String exchange,
                               @Value("${rabbitmq.routing-key:order.created}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Published OrderCreatedEvent for orderId={} to exchange={} routingKey={}",
                event.getOrderId(), exchange, routingKey);
    }
}
