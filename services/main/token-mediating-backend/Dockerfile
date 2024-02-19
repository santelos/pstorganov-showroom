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

EXPOSE 80

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
