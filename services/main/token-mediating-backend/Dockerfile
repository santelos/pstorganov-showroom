FROM curlimages/curl as resources
WORKDIR /resources

RUN curl -Lso /resources/opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar

FROM openjdk:17 as build
WORKDIR /build

RUN microdnf install findutils

COPY gradlew /build/graldew
COPY gradle /build/gradle
COPY *gradle* /build/
RUN ./gradlew dependencies --no-daemon

COPY src /build/src
RUN ./gradlew buildFatJar --no-daemon

FROM --platform=amd64 openjdk:17-slim
WORKDIR /app
COPY --from=build /build/build/libs/app.jar /app/app.jar
COPY --from=resources /resources/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "-javaagent:/app/opentelemetry-javaagent.jar", "/app/app.jar"]
