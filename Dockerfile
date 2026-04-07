FROM node:20-bookworm-slim AS frontend-build

WORKDIR /app/frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN npm install --include=optional

COPY frontend/ ./
COPY api/ ../api/

RUN VITE_API_URL="" npm run build

FROM eclipse-temurin:21-jdk-jammy AS backend-build

WORKDIR /app/backend

COPY backend/gradlew backend/build.gradle backend/settings.gradle ./
COPY backend/gradle ./gradle
RUN chmod +x ./gradlew

COPY backend/src ./src
COPY api/ ../api/
COPY --from=frontend-build /app/frontend/dist ../frontend/dist

RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

COPY --from=backend-build /app/backend/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
