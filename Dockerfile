FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/pakbzer-1.0.0.jar app.jar

RUN mkdir -p /app/data

ENV PORT=10000

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]
