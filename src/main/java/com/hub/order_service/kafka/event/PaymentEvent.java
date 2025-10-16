package com.hub.order_service.kafka.event;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public abstract class PaymentEvent {
    protected Long orderId;
    protected String studentId;
    protected String transactionId;
    protected String paymentStatus;
    protected String rejectReason;
    protected OffsetDateTime createAt;
}
