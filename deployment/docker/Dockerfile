FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY applications/app-service/build/libs/franchise-service.jar franchise-service.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=dev
ENV AWS_REGION=us-east-1
# Create non-root user
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar franchise-service.jar" ]
