package com.hub.order_service.kafka.event;

import com.hub.order_service.model.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record OrderPlacedEvent (
    Long orderId,
    String studentId,
    List<Long> courseIds,
    BigDecimal totalPrice,
    String orderStatus,
    OffsetDateTime createdOn,
    String paymentMethod
) {

}
