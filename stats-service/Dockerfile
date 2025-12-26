FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /build

COPY pom.xml .

COPY stats-service ./stats-service
COPY main-service ./main-service

RUN mvn clean package -DskipTests -pl stats-service/stats-service-server -am


FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /build/stats-service/stats-service-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]