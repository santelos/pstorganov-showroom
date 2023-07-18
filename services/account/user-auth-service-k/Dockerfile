FROM openjdk:17 as build
WORKDIR /build
RUN microdnf install findutils

COPY gradle /build/gradle
COPY settings.gradle.kts gradle.properties gradlew /build/
COPY app/build.gradle.kts /build/app/
COPY common/build.gradle.kts /build/common/
COPY config/build.gradle.kts /build/config/
COPY repo/build.gradle.kts /build/repo/
COPY service/build.gradle.kts /build/service/
COPY web/build.gradle.kts /build/web/
RUN ./gradlew dependencies

COPY . .
RUN ./gradlew buildFatJar

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /build/app/build/libs/app.jar /app/app.jar

EXPOSE 80
EXPOSE 5005

ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "/app/app.jar"]