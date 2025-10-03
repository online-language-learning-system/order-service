package com.hub.order_service.service;

import com.hub.common_library.exception.NotFoundException;
import com.hub.order_service.grpc.Course;
import com.hub.order_service.grpc.CourseDetail;
import com.hub.order_service.grpc.CourseGrpcClient;
import com.hub.order_service.grpc.CourseListResponse;
import com.hub.order_service.model.dto.OrderItemPostDto;
import com.hub.order_service.model.dto.OrderPostDto;
import com.hub.order_service.repository.OrderItemRepository;
import com.hub.order_service.repository.OrderRepository;
import com.hub.order_service.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CourseGrpcClient courseGrpcClient;

    public void createOrder(OrderPostDto orderPostDto) {

        List<Long> courseIds =
            orderPostDto.orderItemPostDtos().stream()
                .map(OrderItemPostDto::courseId)
                .toList();

        // List<CourseDetail>
        CourseListResponse courseListResponse = courseGrpcClient.getCourseDetail(courseIds);
        Map<Long, CourseDetail> courseMap = courseListResponse.getCoursesList().stream()
                .collect(Collectors.toMap(CourseDetail::getCourseId, Function.identity()));

        // Course Request Validation
        for (OrderItemPostDto orderItemPostDto : orderPostDto.orderItemPostDtos()) {
            CourseDetail courseDetail = courseMap.get(orderItemPostDto.courseId());
            if (courseDetail == null)
                throw new NotFoundException(Constants.ErrorCode.COURSE_NOT_FOUND, orderItemPostDto.courseId());
            if (!validateCourse(orderItemPostDto, courseDetail))
                throw new RuntimeException(Constants.ErrorCode.INVALID_COURSE);
        }


    }

    private boolean validateCourse(OrderItemPostDto orderItemPostDto, CourseDetail courseDetail) {
        return orderItemPostDto.courseId().equals(courseDetail.getCourseId())
            && orderItemPostDto.courseTitle().equals(courseDetail.getCourseName())
            && orderItemPostDto.coursePrice().compareTo(BigDecimal.valueOf(courseDetail.getPrice())) == 0;
    }


}
