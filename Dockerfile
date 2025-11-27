#
# Dependency build stage
#
FROM maven:3.9.9-eclipse-temurin-21 AS common_builder
WORKDIR /app/common-library
COPY library/pom.xml .
COPY library/src ./src
RUN mvn clean install -DskipTests

#
# Build course-service
#
FROM maven:3.9.9-eclipse-temurin-21 AS course_builder
WORKDIR /app/course-service
COPY course-service/pom.xml .
COPY course-service/src ./src
COPY --from=common_builder /root/.m2 /root/.m2
RUN mvn clean install -DskipTests

#
# Build order-service
#
FROM maven:3.9.9-eclipse-temurin-21 AS order_builder
WORKDIR /app/order-service
COPY order-service/pom.xml .
COPY order-service/src ./src
COPY --from=common_builder /root/.m2 /root/.m2
COPY --from=course_builder /root/.m2 /root/.m2
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=order_builder /app/order-service/target/*.jar app.jar
EXPOSE 9004
ENTRYPOINT ["java", "-jar", "app.jar"]
