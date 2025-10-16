package com.hub.order_service.kafka.processor;

import com.hub.order_service.kafka.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final StreamBridge streamBridge;

    public void sendOrderEvent(OrderPlacedEvent orderPlacedEvent) {
        // "orders-out-0" is binding/channel name, message payload is orderMessage
        streamBridge.send("orders-out-0", orderPlacedEvent);
    }

}
