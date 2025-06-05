package odoonto.infrastructure.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Configuration
public class ServiceInitializer {
    private static final Logger LOG = Logger.getLogger(ServiceInitializer.class.getName());
    private static final String TARGET_PATH = "src/main/resources/config-data.json";

    // El JSON de configuración con las nuevas credenciales
    private static final String CONFIG_JSON = "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"odoonto-e06a7\",\n" +
            "  \"private_key_id\": \"3e41a266eef2a71d59825b621087d9c230f89b92\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC9E+pRvD6F8RyC\\n/17aldEGA0Z8xGLmhFG3bMj6SpKEnvY2pSU6R/4vcUvl4FHXBDvcGkMY1O0EhEIH\\n3lgozbtQVeQ2S73r3rahq3ElcK/rP1w7EvO6JeVRjOq5S6lTclVMZTxtbZIr0uVD\\nxkaraqRTZHiJYoc7mvanzcgxH10bkAgtPygxLFMrsXhnHYKLNlM4mFqt+QLPb06A\\nXXx2efZz//lMk1sL8VsS12fJBq49O+qi7XKzWdK/gFiGv+NXXvIN+fJv8LcooCL9\\nlBuuELMSngFnPf5q547T8TU8fhzczTkY3H4GVTuMES0RKDvY3YaKo+gv5t2a4xex\\nB8fb2tkHAgMBAAECggEAGF67weMwb9Ue5qeBk0ziDxNW/3fUg3+khK7JzLDV57AR\\nbgOlI7jpwzWoIa3i/oK2MG1WHpo7HmzpkdgPptq1fOpFKtZiWUdwZLATOk8m7XxS\\nJ+8OaPy/bN5r1oww++dtpRYbTsNjprBdCnpA25E4MuNtQc/oPD+B8Sjt158CQi36\\nHyFps6UHb1nk9XxYD/p10tzvex8N88hXBJweGMC+G4GqIVhvWshOnxrBou/5wRfU\\nPIw9IoSyGSR1kf1lzW+SQlXoEZ0odMtdc1Cv29xsZpn3OBIrtjhREfvPM7n2Jkpu\\n2vRa07hCQV5ObS2I2xvdFOyyFDTnlCBFBCWCvXuPaQKBgQD6allDmCMJYPxLbsqf\\nVyUvCIE3ukx8z3j/svE5K5O0VytOVSeEG82yBA71P7R3K+M8az5HhZi8sD0z878K\\nAzFI55Qb7EdRLA+fn0M4oLjxtUXJw3IMWKuS1nphwTIwimyvXt1Csiw3fD6TApaC\\nJOOo6UUicNk/513cA9iQN4DmDQKBgQDBS2IA7sgoK5aGugjU8xLf9TZAQbJ92jD1\\nCScz9hs9CzrAZJlPAZstbLEWvzfTz77GBu/SgkjXVG91uMQub1JfDZoax4LoJ6p6\\nf47jlL6EYw+6tv/Oij9mcrlfsbEmp4Vi7B4msAwapssPyI/n7nItX5yRSdulnD/j\\nI2UD2SLqYwKBgFlz8g0uZJ2uEpITsrf21maVwCsbFsWjC5Mz5e3JPks2tFaYHrSX\\nhgQoZPuA1UOY3uSvXqEH/UayIjrTwJOgDlP/va232sYJWk9oAghRiZ92ZMatValJ\\nZ4jdqvwOmpIycJ644Ys4wIjVjjjPvNoiuVyWb8bczw5/5Mx6ZzslSJtlAoGBAKA5\\n0Yo+ZPJPd15ElY+X0lyNEiRfkQqbtBtaNAHxjpCiX1gdy6tfBHe58m2NokRUn6nd\\nFCzmTGUaG650s0JeiuQ6DTUHjq7MOpeCZzqlEOwChYZbrV5S4M0Wai01yUfmcz4V\\nxcSCB/aLriVuNkOmN0T/TXRtycHU7Gxm6ZQwCK5LAoGAFNRGeyNLZZJ+QVYr7oRW\\nG7jxdg4t/pILOSh8k5xTJX2wkzjr3nJDULEFYF4Q8E9ahfyUasXrlxU925kwmw4I\\nyntqUC3xDFeBy2Zv8VHi2/j/uzws7vTnTLyBcEZ4uGos1rnb2g04FEdfRxUmfRwN\\nTaBZ6hjFw/a+bb9icHS+KgQ=\\n-----END PRIVATE KEY-----\\n\",\n" +
            "  \"client_email\": \"firebase-adminsdk-fbsvc@odoonto-e06a7.iam.gserviceaccount.com\",\n" +
            "  \"client_id\": \"105885455252550045211\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40odoonto-e06a7.iam.gserviceaccount.com\",\n" +
            "  \"universe_domain\": \"googleapis.com\"\n" +
            "}";

    @PostConstruct
    public void initSystem() {
        try {
            prepareConfigDataFile();
            LOG.info("Sistema iniciado correctamente");
        } catch (IOException e) {
            LOG.severe("Error al iniciar sistema: " + e.getMessage());
        }
    }

    private void prepareConfigDataFile() throws IOException {
        File targetFile = new File(TARGET_PATH);
        
        // Asegurar que el directorio existe
        targetFile.getParentFile().mkdirs();
        
        // Escribir archivo usando OutputStreamWriter con codificación UTF-8
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(targetFile), StandardCharsets.UTF_8)) {
            writer.write(CONFIG_JSON);
        }
        
        LOG.info("Archivo generado en: " + targetFile.getAbsolutePath());
    }
}