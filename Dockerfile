FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:25-jdk-alpine
# create non-root group/user and install curl (used by potential healthchecks)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup \
	&& apk add --no-cache bash curl

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# set ownership and switch to non-root user
RUN chown appuser:appgroup /app/app.jar
USER appuser

ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]