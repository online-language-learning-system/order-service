package com.hub.order_service.model;

import com.hub.common_library.model.AbstractAuditEntity;
import com.hub.order_service.model.enumeration.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", orphanRemoval = true)
    List<OrderItem> orderItems = new ArrayList<>();

}
