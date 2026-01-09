FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /build


COPY pom.xml .

COPY stats-service ./stats-service
COPY main-service ./main-service

RUN mvn clean package -DskipTests -pl main-service/main-service-server -am


FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /build/main-service/main-service-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]