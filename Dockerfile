# --- 1. LÉPÉS: FORDÍTÁS A DOCKEREN BELÜL (Linux környezetben) ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Bemásoljuk a projekt leíróját és a forráskódot (NEM a lefordított taget-et!)
COPY pom.xml .
COPY src ./src

# A Docker maga végzi el a fordítást egy tiszta, hiba nélküli környezetben
RUN mvn clean package -DskipTests

# --- 2. LÉPÉS: A KÉSZ ALKALMAZÁS FUTTATÁSA ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Átvesszük a tiszta fordítás eredményét az 1. lépésből
COPY --from=build /app/target/*.jar app.jar

# Port kiajánlása
EXPOSE 8080

# Alkalmazás indítása
ENTRYPOINT ["java", "-jar", "app.jar"]