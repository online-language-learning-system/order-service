package com.hub.order_service.kafka.event;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderPaidEvent(
        String studentId,
        List<Long> courseIds
) {
}
