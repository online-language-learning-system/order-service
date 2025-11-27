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
@Table(name = "\"order\"", schema = "dbo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "total_discount_amount")
    private Float discount;

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "reject_reason")
    private String rejectReason;

    // Liên kết tới các OrderItem
    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Phương thức tiện ích để lấy danh sách courseIds
    @Transient
    public List<Long> getCourseIds() {
        if (orderItems == null) return List.of();
        return orderItems.stream()
                .map(OrderItem::getCourseId)
                .toList();
    }
}
