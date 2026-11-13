# Sử dụng OpenJDK làm base image
FROM openjdk:17-jdk-alpine

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file jar vào container
COPY target/hrm-0.0.1-SNAPSHOT.jar app.jar

# Expose port (mặc định Spring Boot chạy trên 8080)
EXPOSE 2003

# Lệnh chạy ứng dụng Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]
