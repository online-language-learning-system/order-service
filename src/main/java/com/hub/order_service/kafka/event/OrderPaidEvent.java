package com.hub.order_service.kafka.event;

import com.hub.order_service.model.enumeration.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderPaidEvent(
        Long orderId,
        String studentId,
        BigDecimal totalPrice,
        OrderStatus status,
        List<Long>courseIds
) {}
