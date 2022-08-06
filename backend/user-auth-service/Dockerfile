FROM openjdk:17 as build
WORKDIR /build

COPY gradlew /build/graldew
COPY gradle /build/gradle
COPY *gradle* /build/
RUN ./gradlew clean --no-daemon

COPY src /build/src
RUN ./gradlew bootJar --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /build/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
