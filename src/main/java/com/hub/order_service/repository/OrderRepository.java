package com.hub.order_service.repository;

import com.hub.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Long> {
}
