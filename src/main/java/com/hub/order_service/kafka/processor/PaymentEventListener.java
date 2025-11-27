package com.hub.order_service.kafka.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hub.order_service.kafka.event.PaymentFailedEvent;
import com.hub.order_service.kafka.event.PaymentSucceededEvent;
import com.hub.order_service.kafka.event.OrderPaidEvent;
import com.hub.order_service.model.Order;
import com.hub.order_service.model.OrderItem;
import com.hub.order_service.model.enumeration.OrderStatus;
import com.hub.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final StreamBridge streamBridge;

    // Khởi tạo ObjectMapper với JavaTimeModule
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Bean
    public Consumer<String> paymentSucceededConsumer() {
        return payload -> {
            try {
                String json = new String(Base64.getDecoder().decode(payload));
                PaymentSucceededEvent event = mapper.readValue(json, PaymentSucceededEvent.class);

                log.info("✅ Payment SUCCESS for order {}", event.orderId());

                Order order = orderRepository.findById(event.orderId())
                        .orElseThrow(() -> new RuntimeException("Order not found: " + event.orderId()));
                order.setOrderStatus(OrderStatus.PAID);
                orderRepository.save(order);

                OrderPaidEvent orderPaidEvent = new OrderPaidEvent(
                        order.getId(),
                        order.getUserId(),
                        order.getTotalPrice(),
                        order.getOrderStatus(),
                        order.getCourseIds()
                );
                streamBridge.send("orderpaid-out-0", orderPaidEvent);
                log.info("📤 OrderPaidEvent sent for order {}", order.getId());

            } catch (Exception e) {
                log.error("❌ Failed to process PaymentSucceededEvent", e);
            }
        };
    }

    @Bean
    public Consumer<String> paymentFailedConsumer() {
        return payload -> {
            try {
                String json = new String(Base64.getDecoder().decode(payload));
                PaymentFailedEvent event = mapper.readValue(json, PaymentFailedEvent.class);

                log.warn("⚠️ Payment FAILED for order {}", event.orderId());

                Order order = orderRepository.findById(event.orderId())
                        .orElseThrow(() -> new RuntimeException("Order not found: " + event.orderId()));
                order.setOrderStatus(OrderStatus.REJECT);
                order.setRejectReason(event.failureReason());
                orderRepository.save(order);

            } catch (Exception ex) {
                log.error("❌ Failed to process PaymentFailedEvent", ex);
            }
        };
    }
}
