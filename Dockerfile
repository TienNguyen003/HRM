FROM amazoncorretto:17-alpine3.12-jdk

COPY target/hrm-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar" ]
