# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Resolve deps first (cached layer — only re-runs when pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Build the fat JAR
COPY src ./src
RUN mvn package -DskipTests -q

# Extract Spring Boot layers (faster subsequent image builds)
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Non-root user for security
RUN addgroup -S ucar && adduser -S ucar -G ucar

# Copy layered JAR contents (most-stable → least-stable for optimal cache)
COPY --from=builder /build/target/extracted/dependencies/          ./
COPY --from=builder /build/target/extracted/spring-boot-loader/    ./
COPY --from=builder /build/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /build/target/extracted/application/           ./

RUN chown -R ucar:ucar /app
USER ucar

EXPOSE 8080

# Container-aware JVM: respects cgroup CPU & memory limits
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "org.springframework.boot.loader.launch.JarLauncher"]
