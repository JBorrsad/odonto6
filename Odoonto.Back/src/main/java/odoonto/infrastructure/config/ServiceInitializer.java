package odoonto.infrastructure.config;


import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

@Configuration
public class ServiceInitializer {
    private static final Logger LOG = Logger.getLogger(ServiceInitializer.class.getName());
    private static final String TARGET_PATH = "src/main/resources/config-data.json";

    // El JSON de configuración completo, con las secuencias de escape correctas
    private static final String CONFIG_JSON = "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"odoonto-e06a7\",\n" +
            "  \"private_key_id\": \"0fe8954dae26c2a1f493ec57136e3cc3d514eadf\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCbdzgRwdDoNKAj\\nvrWFfXJ8hhqrrcXKGGgdy2sI+ag38cm07qSAwSEtyKiUJCSWbOkgq4QDm7iiVd3d\\noBkZpk7YS2Kqk41ci5TEQqoxe6v3F9WJxuyuoCIQSlp4lJ0g7v/1dpMQyX1CxXiG\\noTmR0LFey+zq9qvNnLZtkzr1cO1M8RSrE2kLAzo8oyuSOkowOCE/jY1PLoW2PItA\\nh1u7fXbvjB7/cMqwQuIJn44w5tfd6SxyeQSmF/d+vopAVMUlblV9kq4ThfGJX+hJ\\nJMKUIPgxkDKOhy1xtiFwh0axQXHhgqEx8IZV6WmgZj2sJzeuZrwHw66CP3MAZlFr\\nqfXcsAuJAgMBAAECggEACR5hB4mau0P68J3K/91vzaYAAnna1BPCZ5z61jfuifmA\\nn0AwHe02CP1wBeBo1PnmYqgswhlsJvlqXgl572s1zLH/Q4oMDJWTUD+ow+ZBG+kl\\n+ULVtxhlZxc1X0U4QipCkR83WRC4bCYw/5SNT+QxwyGq780wHotGl/6+wo6xHIYg\\nBslu45BqBfkHyHN36QXF/qca1fnyUqqsJkFCWjXgtyk0zygV8CPS+HzSH8ie7sVk\\nttKiZpJp6tzm26K460O5CP0vz7keMI7txKZ0+aoG5eNYasUjKFjZznSHTTikc6MD\\nab+K6cP0WNrWMnfir3KzKzVM0Hyx2X+OI05ZkhkcUQKBgQDQ14UzYtAGiOGGlECB\\nPOvRg30qnw4ERY5wEcM7+LB9XQ94gphqTqX5cYqPQpFiPJ9ghJNwMhyyESJHjqP2\\nWi7WnhXNNsK6U2SbDYExFrudrNt2hnrteHK4pAb1Qrz1SJSiMNsqednzenCxmNV+\\nilurIeE/67KgfukUv4k8gMmdVwKBgQC+kjFlAgUCy0L+IkEnOC5GYfOELlflB2Pc\\nyS2S4FrD9XSpIJ1ufCJtxJU4snrW5w993OQqncAZp8MJ6tvtB6z2Kv6OfMzx1ooy\\nn0DlZPRd/LK3XKf1jWAJNhosjbBUWqFW3KhckliT7hJhXcT+LhLiJx5eOCXXJ59b\\n6m4vVu0yHwKBgGXd6mQBO9UkKVV6tOgVLjW4/HGwuaaNwweZMnDrQjM0FFrE4NFh\\n4YWFw39TihSv21G5+SzN1L9qqhzbT9DXdmdLHvlFT8CQ8s7KzIyEOSmU6kurFwUE\\nLnkv0REK556BM7bVO+JBI3Wm8JdAJEsf88zKrtN+BVhR7yEmuCsh6rXrAoGBAIP5\\npm93KJU4wtpQSqKoi5N+fjBKD+Cyb3wVbpRK/1C6CeKejahYG1e9TkIHiH6PYfEe\\nKn1A5ipePfn+RfQu3p9UQxnWX/ATTX2lQEIc2gbE29eUOVUaZSm76AB212hzo0wO\\nYCJLz0ATHXOEhsooijdINAW4CKyp6fizrz7lijqtAoGAHipgFrdJdwzYGHMWTG13\\nmdwzFeRxP6u1B0TiWU2lNEBjJB+uaasSAmvHbcOFVmNJqwctB+R6wMhnq/RSgLb0\\n8p9F9CMbQXLHIdWgXiHFWhv6RjDy3A0rfMy0pXPutTl4HaUzxjhNfLFZxVC3YxUm\\nFKqPBVD59aw+3ibyRH4NhMo=\\n-----END PRIVATE KEY-----\\n\",\n" +
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
        
        // Escribir archivo directamente sin manipulaciones que puedan dañar el formato JSON
        try (FileWriter writer = new FileWriter(targetFile)) {
            writer.write(CONFIG_JSON);
        }
        
        LOG.info("Archivo generado en: " + targetFile.getAbsolutePath());
    }
}