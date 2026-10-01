# --- build: compila el jar ejecutable con el Gradle wrapper del proyecto ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Primero solo lo necesario para resolver dependencias: esta capa queda cacheada
# mientras no cambie build.gradle, y los rebuilds solo recompilan el código.
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

COPY src ./src
# Los tests de integración necesitan una base MySQL, por eso no se corren en el build de la imagen.
RUN ./gradlew bootJar --no-daemon -x test

# --- runtime: solo el JRE y el jar, corriendo con un usuario sin privilegios ---
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=build --chown=spring:spring /app/build/libs/*.jar app.jar

USER spring

# Perfil de producción (exige las variables de entorno obligatorias, ver DEPLOY.md).
# Zona horaria de Argentina: LocalDate.now() / LocalDateTime.now() (turnos, vencimientos,
# fechas de subida) se calculan con la hora local, no UTC.
# La JVM usa como máximo el 75% de la memoria asignada al contenedor.
ENV SPRING_PROFILES_ACTIVE=prod \
    TZ=America/Argentina/Buenos_Aires \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Duser.timezone=America/Argentina/Buenos_Aires"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
