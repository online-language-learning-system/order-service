package com.hub.order_service.kafka.producer;

import com.hub.order_service.kafka.event.OrderPaidEvent;
import com.hub.order_service.kafka.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final StreamBridge streamBridge;

    public void sendOrderPlaced(OrderPlacedEvent orderPlacedEvent) {
        // Cả OrderPlacedEvent và OrderPaidEvent gửi lên chung topic "order.events"
        streamBridge.send("orders-out-0", orderPlacedEvent);
    }

    public void sendOrderPaid(OrderPaidEvent orderPaidEvent) {
        streamBridge.send("orders-out-0", orderPaidEvent);
    }
}

