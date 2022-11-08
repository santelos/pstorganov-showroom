FROM openjdk:11 as build
WORKDIR /build

COPY gradlew /build/graldew
COPY gradle /build/gradle
COPY *gradle* /build/
RUN ./gradlew clean --no-daemon

COPY src /build/src
RUN ./gradlew buildFatJar --no-daemon

FROM openjdk:11-slim
WORKDIR /app
COPY --from=build /build/build/libs/app.jar /app/app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "/app/app.jar"]
