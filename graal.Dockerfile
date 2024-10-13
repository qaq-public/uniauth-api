FROM maven:3-ibm-semeru-21-jammy AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM ghcr.io/graalvm/native-image-community:21 AS native-build
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
RUN native-image -jar app.jar uniauth

FROM alpine:3.20
WORKDIR /app

COPY --from=native-build /app/uniauth /app/uniauth
RUN chmod +x /app/uniauth
EXPOSE 8888
CMD ["/app/uniauth", "-Duser.timezone=GMT+8"]
