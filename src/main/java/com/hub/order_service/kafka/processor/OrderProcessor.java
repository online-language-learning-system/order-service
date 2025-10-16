package com.hub.order_service.kafka.processor;

import com.hub.order_service.kafka.event.OrderPaidEvent;
import com.hub.order_service.kafka.event.PaymentEvent;
import com.hub.order_service.kafka.event.PaymentFailedEvent;
import com.hub.order_service.kafka.event.PaymentSucceededEvent;
import com.hub.order_service.model.Order;
import com.hub.order_service.model.OrderItem;
import com.hub.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class OrderProcessor {

    private final OrderService orderService;

    @Bean
    public Function<PaymentEvent, OrderPaidEvent> updateOrderStatus() {
        return paymentEvent -> {

            if (paymentEvent instanceof PaymentSucceededEvent success) {
                Order order = orderService.acceptOrder(success.getOrderId());
                return OrderPaidEvent.builder()
                        .studentId(success.getStudentId())
                        .courseIds(order.getOrderItems().stream().map(OrderItem::getCourseId).toList())
                        .build();
            }

            if (paymentEvent instanceof PaymentFailedEvent failed) {
                Order order = orderService.rejectOrder(failed.getOrderId(), failed.getRejectReason());
                return null;
            }

            return null;
        };
    }

}
