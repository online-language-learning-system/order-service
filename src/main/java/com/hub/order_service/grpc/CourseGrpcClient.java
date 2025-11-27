//package com.hub.order_service.grpc;
//
//import com.hub.course_service.grpc.CourseListRequest;
//import com.hub.course_service.grpc.CourseListResponse;
//import com.hub.course_service.grpc.CourseServiceGrpc;
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.util.List;
//
//@Component
//public class CourseGrpcClient {
//
//    private ManagedChannel channel;
//    private CourseServiceGrpc.CourseServiceBlockingStub stub;
//
//    @PostConstruct
//    public void init() {
//        channel = ManagedChannelBuilder
//                .forAddress("localhost", 50051)
//                .usePlaintext()
//                .build();
//
//        // Blocking Stub: waiting for response
//        stub = CourseServiceGrpc.newBlockingStub(channel);
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        if (channel != null) channel.shutdown();
//    }
//
//    public CourseListResponse getCourseDetail(List<Long> courseIds) {
//        CourseListRequest request = CourseListRequest.newBuilder()
//                .addAllCourseId(courseIds)
//                .build();
//        return stub.getCourseDetails(request);
//    }
//
//}
