# Használjunk hivatalos Java 21 image-t
FROM eclipse-temurin:21-jdk-alpine

# Munkakönyvtár beállítása
WORKDIR /app

# Másoljuk be a lefordított jar fájlt (a target mappából)
COPY target/sulieszkoz_kolcsonzo-0.0.1-SNAPSHOT.jar app.jar

# Port kiajánlása (alapértelmezett Spring Boot port)
EXPOSE 8080

# Alkalmazás indítása
ENTRYPOINT ["java", "-jar", "app.jar"]