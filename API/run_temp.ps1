Write-Host "Ejecutando aplicaciÃ³n con parÃ¡metros JVM para Firestore..."
& .\mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=--add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.time.chrono=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED" "-Dserver.port=8080" 
