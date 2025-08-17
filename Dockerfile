# syntax=docker/dockerfile:1
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline
COPY . .
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/CardMaster \
    SPRING_DATASOURCE_USERNAME=ayzek \
    SPRING_DATASOURCE_PASSWORD=123 \
    SPRING_JPA_HIBERNATE_DDL_AUTO=validate \
    SPRING_SECURITY_USER_NAME=AyzeksBank \
    SPRING_SECURITY_USER_PASSWORD=IuseHaskellBTW \
    SPRING_SECURITY_USER_ROLES=ADMIN \
    APP_JWT_SECRET=8nswcQpoE3eTzWmknTdcaxiaSlbM0VpqDt7hD0QXGGA= \
    APP_JWT_TTL_MS=86400000 \
    APP_CRYPTO_KEY=pHQQqThqHaXP64qN49swqzvK7QFd61HoL7ZJOdScBfQ=

COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
