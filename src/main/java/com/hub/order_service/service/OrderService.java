package com.hub.order_service.service;

import com.hub.common_library.exception.NotFoundException;
//import com.hub.course_service.grpc.CourseDetail;
//import com.hub.course_service.grpc.CourseListResponse;
//import com.hub.order_service.grpc.CourseGrpcClient;
import com.hub.order_service.kafka.event.OrderPlacedEvent;
import com.hub.order_service.kafka.event.OrderPlacedEvent;
import com.hub.order_service.kafka.producer.OrderProducer;
import com.hub.order_service.model.Order;
import com.hub.order_service.model.OrderItem;
import com.hub.order_service.model.dto.OrderDetailGetDto;
import com.hub.order_service.model.dto.OrderItemPostDto;
import com.hub.order_service.model.dto.OrderPostDto;
import com.hub.order_service.model.enumeration.OrderStatus;
import com.hub.order_service.repository.OrderItemRepository;
import com.hub.order_service.repository.OrderRepository;
import com.hub.order_service.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderProducer orderProducer;
//    private final CourseGrpcClient courseGrpcClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderDetailGetDto createOrder(OrderPostDto orderPostDto) {

        // Get Course from course service
        List<Long> courseIds =
            orderPostDto.orderItemPostDtos().stream()
                .map(OrderItemPostDto::courseId)
                .toList();

        if (courseIds.isEmpty())
            throw new RuntimeException("No courses exist");

        // List<CourseDetail>
//        CourseListResponse courseListResponse = courseGrpcClient.getCourseDetail(courseIds);
//        Map<Long, CourseDetail> courseMap = courseListResponse.getCoursesList().stream()
//                .collect(Collectors.toMap(CourseDetail::getCourseId, Function.identity()));

        // Course Request Validation
//        for (OrderItemPostDto orderItemPostDto : orderPostDto.orderItemPostDtos()) {
//            CourseDetail courseDetail = courseMap.get(orderItemPostDto.courseId());
//            if (courseDetail == null)
//                throw new NotFoundException(Constants.ErrorCode.COURSE_NOT_FOUND, orderItemPostDto.courseId());
//            if (!isMatchingCourseDetail(orderItemPostDto, courseDetail))
//                throw new RuntimeException(Constants.ErrorCode.INVALID_COURSE);
//        }

        // Create Order
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);
        order.setUserId(userId);
        order.setTotalPrice(BigDecimal.ZERO);
        Order savedOrder = orderRepository.save(order);     // Save to get order id for OrderItem

        // Create Order Item
        Set<OrderItem> orderItems = orderPostDto.orderItemPostDtos().stream()
                .map(
                    orderItemPostDto -> {
                        OrderItem orderItem = OrderItem.builder()
                                .order(savedOrder)
                                .courseId(orderItemPostDto.courseId())
                                .courseTitle(orderItemPostDto.courseTitle())
                                .coursePrice(orderItemPostDto.coursePrice())
                                .build();

                        // Set OrderItem to Order respectively
                        savedOrder.getOrderItems().add(orderItem);
                        savedOrder.setTotalPrice(savedOrder.getTotalPrice().add(orderItem.getCoursePrice()));

                        return orderItem;
                    })
                .collect(Collectors.toSet());

        orderItemRepository.saveAll(orderItems);
        orderRepository.save(savedOrder);

        // Emits event
        OrderPlacedEvent orderPlacedEvent = OrderPlacedEvent.builder()
                .orderId(savedOrder.getId())
                .studentId(savedOrder.getUserId())
                .courseIds(courseIds)
                .totalPrice(savedOrder.getTotalPrice())
                .orderStatus(savedOrder.getOrderStatus().toString())
                .createdOn(savedOrder.getCreatedOn())
                .paymentMethod("VNPay")
                .build();
        orderProducer.sendOrderPlaced(orderPlacedEvent);
        log.info("The order with id {} created", savedOrder.getId());

        return OrderDetailGetDto.fromModel(savedOrder);
    }

//    private boolean isMatchingCourseDetail(OrderItemPostDto orderItemPostDto, CourseDetail courseDetail) {
//        return orderItemPostDto.courseId().equals(courseDetail.getCourseId())
//            && orderItemPostDto.courseTitle().equals(courseDetail.getCourseName())
//            && orderItemPostDto.coursePrice().compareTo(BigDecimal.valueOf(courseDetail.getPrice())) == 0;
//    }

    public void acceptOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.ORDER_NOT_FOUND, orderId));
        order.setOrderStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);
    }

    public void rejectOrder(Long orderId, String rejectReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.ORDER_NOT_FOUND, orderId));
        order.setOrderStatus(OrderStatus.REJECT);
        order.setRejectReason(rejectReason);
        orderRepository.save(order);
    }

}
