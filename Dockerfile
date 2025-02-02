FROM eclipse-temurin:21

RUN groupadd spring && useradd -m -g spring spring
USER spring:spring

ARG JAR_OTEL=telemetry/opentelemetry-javaagent.jar
ARG JAR_FILE=target/*.jar

COPY ${JAR_OTEL} agent.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-javaagent:agent.jar", "-jar", "app.jar"]