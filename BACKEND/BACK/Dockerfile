# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Cache dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S ucar && adduser -S ucar -G ucar

COPY --from=builder /build/target/*.jar app.jar

RUN chown ucar:ucar app.jar
USER ucar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
