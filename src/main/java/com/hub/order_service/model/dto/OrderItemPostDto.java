package com.hub.order_service.model.dto;

import java.math.BigDecimal;

public record OrderItemPostDto(
        Long courseId,
        String courseTitle,
        BigDecimal coursePrice,
        BigDecimal discountAmount
) {
}
