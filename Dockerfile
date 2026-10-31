# ---------- 1. Build the React frontend ----------
FROM node:20-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ---------- 2. Build the Spring Boot backend (with the React build inside) ----------
FROM maven:3.9-eclipse-temurin-17 AS backend
WORKDIR /app/backend
COPY backend/pom.xml ./
RUN mvn -q dependency:go-offline
COPY backend/src ./src
COPY --from=frontend /app/frontend/dist ./src/main/resources/static
RUN mvn -q -DskipTests package

# ---------- 3. Run ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend /app/backend/target/learnhub-backend-1.0.0.jar app.jar
# Use MySQL in production. Provide DB_URL, DB_USERNAME, DB_PASSWORD and JWT_SECRET as environment variables.
ENV SPRING_PROFILES_ACTIVE=mysql
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
