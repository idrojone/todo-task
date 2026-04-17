FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8080
CMD ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]