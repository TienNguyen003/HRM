FROM amazoncorretto:21-alpine

COPY target/hrm-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar" ]
