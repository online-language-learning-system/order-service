package com.hub.order_service.controller;

import com.hub.order_service.model.dto.OrderDetailGetDto;
import com.hub.order_service.model.dto.OrderPostDto;
import com.hub.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(path = "/storefront/order")
    public ResponseEntity<OrderDetailGetDto> createOrder(@RequestBody OrderPostDto orderPostDto) {
        return ResponseEntity.ok(orderService.createOrder(orderPostDto));
    }
}
