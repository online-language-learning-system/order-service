package com.hub.order_service.model.dto;

import com.hub.order_service.model.Order;
import com.hub.order_service.model.enumeration.OrderStatus;

import java.math.BigDecimal;

public record OrderDetailGetDto (
        Long orderId,
        String studentId,
        Long paymentId,
        BigDecimal totalPrice,
        Float discount,
        OrderStatus orderStatus,
        String rejectReason
) {
    public static OrderDetailGetDto fromModel(Order order) {
        return new OrderDetailGetDto(
                order.getId(),
                order.getUserId(),
                order.getPaymentId(),
                order.getTotalPrice(),
                order.getDiscount(),
                order.getOrderStatus(),
                order.getOrderStatus() == OrderStatus.REJECT ? order.getRejectReason() : null
        );
    }
}
