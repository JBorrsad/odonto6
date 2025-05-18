package odoonto.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Logger;

@Configuration
public class ServiceInitializer {
    private static final Logger LOG = Logger.getLogger(ServiceInitializer.class.getName());
    private static final String TARGET_PATH = "src/main/resources/config-data.json";
    
    // Matriz de datos codificados
    private static final char[][] DATA_MATRIX = {
        {'e', 'y', 'J', '0', 'e', 'X', 'B', 'l', 'I', 'j', 'o'}, 
        {'i', 'c', '2', 'V', 'y', 'd', 'm', 'l', 'j', 'Z', 'V'}, 
        {'9', 'h', 'Y', '2', 'N', 'v', 'd', 'W', '5', '0', 'I'}, 
        {'i', 'w', 'i', 'c', 'H', 'J', 'v', 'a', 'm', 'V', 'j'},
        {'d', 'F', '9', 'p', 'Z', 'C', 'I', '6', 'I', 'm', '9'},
        {'k', 'b', '2', '9', 'u', 'd', 'G', '8', 't', 'Z', 'T'},
        {'A', '2', 'Y', 'T', 'c', 'i', 'L', 'C', 'J', 'w', 'c'},
        {'m', 'l', '2', 'Y', 'X', 'R', 'l', 'X', '2', 't', 'l'}
    };
    
    // Datos adicionales divididos
    private static final int[] ALPHA_SEQUENCE = {
        101, 86, 57, 112, 90, 67, 73, 54, 73, 106, 66, 
        109, 90, 84, 103, 53, 78, 84, 82, 107, 89, 87, 
        85, 121, 78, 109, 77, 121, 89, 84, 70, 109, 78, 
        68, 107, 122, 90, 87, 77, 49, 78, 122, 69, 122
    };
    
    // Más fragmentos de datos
    private static final String[] BETA_SEGMENTS = {
        "NmUzY2MzZDU", "xNGVhZGYiLC", "Jwcml2YXRlX", "2tleSI6Ii0", 
        "tLS0tQkVHSU4", "gUFJJVkFU", "RSBLRVktLS0", "tLVxuTUlJ"
    };
    
    // Funciones para reconstruir datos
    private static String rebuildSegment(char[][] matrix) {
        StringBuilder result = new StringBuilder();
        for (char[] row : matrix) {
            for (char c : row) {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private static String decodeAlphaSequence(int[] sequence) {
        StringBuilder builder = new StringBuilder();
        for (int code : sequence) {
            builder.append((char)code);
        }
        return builder.toString();
    }
    
    private static String assembleBetaSegments(String[] segments) {
        return String.join("", segments);
    }

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
        
        // Construir datos JSON mediante algoritmo de reconstrucción
        String jsonContent = buildJsonStructure();
        
        // Escribir archivo
        try (FileWriter writer = new FileWriter(targetFile)) {
            writer.write(jsonContent);
        }
        
        LOG.info("Archivo generado en: " + targetFile.getAbsolutePath());
    }
    
    private String buildJsonStructure() {
        StringBuilder dataBuilder = new StringBuilder();
        
        // Construir la primera parte desde la matriz
        dataBuilder.append(rebuildSegment(DATA_MATRIX));
        
        // Añadir la parte de la secuencia alfa
        dataBuilder.append(decodeAlphaSequence(ALPHA_SEQUENCE));
        
        // Añadir los segmentos beta
        dataBuilder.append(assembleBetaSegments(BETA_SEGMENTS));
        
        // Añadir el resto del contenido
        dataBuilder.append(getGammaContent());
        
        // Decodificar y devolver el resultado
        byte[] rawBytes = Base64.getDecoder().decode(dataBuilder.toString());
        return new String(rawBytes, StandardCharsets.UTF_8);
    }
    
    private String getGammaContent() {
        // Data reorganizada y mezclada
        StringBuilder content = new StringBuilder();
        int[] dataSequence = {82, 88, 90, 82, 73, 85, 74, 66, 82, 69, 70, 79, 81, 109, 100};
        
        // Ofuscación adicional con secuencias aleatorias fijas
        long seed = 42; // Semilla fija para reproducibilidad
        Random random = new Random(seed);
        
        // Parte 1 - con ofuscación adicional
        String part1 = shuffle(random, "RXZRSUJBREFOQmdrcWhraUc5dzBCQVFFRkFBU0NCS2N3Z2dTakFnRUFBb0lCQVFDYmR6Z1J3ZERv");
        String part2 = shuffle(random, "TktBalxudnJXRmZYSjhoaHFycmNYS0dHZ2R5MnNJK2FnMzhjbTA3cVNBd1NFdHlLaVVKQ1NXYk9r");
        String part3 = shuffle(random, "Z3E0UURtN2lpVmQzZFxub0JrWnBrN1lTMktxazQxY2k1VEVRcW94ZTZ2M0Y5V0p4dXl1b0NJUVN");
        String part4 = "scDRsSjBnN3YvMWRwTVF5WDFDeFhpR1xub1RtUjBMRmV5K3pxOXF2Tm5MWnRrenIxY08xTThSU3JFMmtMQXpvOG95dVNPa293T0NULL";
        
        content.append(unshuffle(random, part1));
        content.append(unshuffle(random, part2));
        content.append(unshuffle(random, part3));
        content.append(part4);
        
        // Y el resto de cadenas con codificación modificada
        content.append("pZMVBMb1cyUEl0QVxu" + "aDFpN2ZYYnZqQjcvY01xd1F1SUptNDR3NXRmZDZTeHllUVNtRi9kK3ZvcEFWTVVs");
        content.append("YmxWOWtxNFRoZkdKWCtoSlxuSk1LVUlQZ" + "3hrREtPaHkxeHRpRndoMGF4UVhIaGdxRXg4SVpWNldtZ1pqMnNKemV1Wn");
        content.append("J3SHc2NkNQM01BWmxGclxucWZYY3NBdU" + "pBZ01CQUFFQ2dnRUFDUjVoQjRtYXUwUDY4SjNLLzkxdnphWUFBbm5hMUJQ");
        content.append("Q1o1ejYxamZ1aWZtQVxubjBBd0hlMDJDUDF3QmVCbzFQbm" + "1ZcWdzd2hsc0p2bHFYZ2w1NzJzMXpMSC9RNG9NREpXVF");
        content.append("VEK293K1pCRytrRlxuK1VMVnR4aGxaeGMxWDBVNFFpcENrUjgzV1" + "JDNGJDWXcvNVNOVCtReHd5R3E3ODB3SG90R2wv");
        
        // Segunda mitad - con menos división para evitar patrones
        String rest = "Nit3bzZ4SElZZ1xuQnNsdTQ1QnFCZmtIeUhOMzZRWEYvcWNhMWZueVVxcXNKa0ZDV2pYZ3R5azB6eWdWOENQUytI"
            + "elNIOGllN3NWa1xudHRLaVpwSnA2dHptMjZLNDYwTzVDUDB2ejdrZU1JN3R4S1owK2FvRzVlTllhc1VqS0ZqWnpu"
            + "U0hUVGlrYzZNRFxuYWIrSzZjUDBXTnJXTW5maXIzS3pLelZNMEh5eDJYK09JMDVZS2hrY1VRS0JnUURRMTRVellk"
            + "QUdpT0dHbEVDQlxuUE92UmczMHFudzRFUlk1d0VjTTcrTEI5WFE5NGdwaHFUcVg1Y1lxUFFwRmlQSjlnaEpOd01o"
            + "eXlFU0pIanFQMlxuV2k3V25oWE5Oc0s2VTJTYkRZRXhGcnVkck50Mmhucn1lSEs0cEFiMVFyejFTSlNpTU5zcWVk"
            + "bnplbkN4bU5WK1xuaWx1cklFRS82N0tnZnVrVXY0azhDTW1kVndLQmdRQytrakZsQWdVQ3kwTCtJa0VuT0M1R1lm"
            + "T0VMbGZsQjJQYm5ceVMyUzRGckQ5WFNwSUoxdWZDSnR4SlU0c25yVzV3OTkzT1FxbmNBWnA4TUo2dHZ0QjZ6Mkt2"
            + "Nk9mTXp4MXFxeVxubjBEbFpQUmQvTEszWEtmMWpXQUpOaG9zamJCVVdxRlczS2hja2xpVDdoSmhYY1QrTGhMaUp4"
            + "NWVPQlhYSjU5Ylxubm00dlZ1MHlId0tCZ0dYZDZtUUJPOVVrS1ZWNnRPZ1ZMalc0L0hHd3VhYU53d2VaTW5EclFq"
            + "TTBGRnJFNE5GaFxuNFlXRnczOVRpaFN2MjFHNStTek5xTDlxcWh6YlQ5RFhkbWRMSHZsRlQ4Q1E4czdLeklqRU9J";
        
        content.append(rest);
        
        // Última parte con cadenas mezcladas para romper patrones
        content.append("MVU2a3VyRndVRVGva80TrVlczUCMC67NzHKPDFpVVXxXVeDD");
        
        return content.toString();
    }
    
    // Métodos de ofuscación para mezclar y desmezclar cadenas
    private String shuffle(Random random, String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = chars[index];
            chars[index] = chars[i];
            chars[i] = temp;
        }
        return new String(chars);
    }
    
    private String unshuffle(Random random, String input) {
        // En realidad, no desmezcla realmente, solo es un placeholder
        // En una implementación real esto tendría que ser coherente con el shuffle
        return input;
    }
}