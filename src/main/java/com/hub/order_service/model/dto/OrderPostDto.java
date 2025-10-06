package com.hub.order_service.model.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OrderPostDto (
        Float discount,
        @NotNull BigDecimal totalPrice,
        @NotNull List<OrderItemPostDto> orderItemPostDtos
){
}
