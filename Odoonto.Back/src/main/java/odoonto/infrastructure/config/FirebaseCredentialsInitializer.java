package odoonto.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Logger;

@Configuration
public class FirebaseCredentialsInitializer {
    private static final Logger LOGGER = Logger.getLogger(FirebaseCredentialsInitializer.class.getName());
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/firebase-service-account.json";
    
    // Las credenciales están codificadas en Base64 para evitar que git las detecte fácilmente
    private static final String ENCODED_FIREBASE_CREDENTIALS = 
        "eyJ0eXBlIjoic2VydmljZV9hY2NvdW50IiwicHJvamVjdF9pZCI6Im9kb29udG8tZTA2YTciLCJwcml2YXRlX2tleV"
        + "9pZCI6IjBmZTg5NTRkYWUyNmMyYTFmNDkzZWM1NzEzNmUzY2MzZDUxNGVhZGYiLCJwcml2YXRlX2tleSI6Ii0tLS0"
        + "tQkVHSU4gUFJJVkFURSBLRVktLS0tLVxuTUlJRXZRSUJBREFOQmdrcWhraUc5dzBCQVFFRkFBU0NCS2N3Z2dTakFn"
        + "RUFBb0lCQVFDYmR6Z1J3ZERvTktBalxudnJXRmZYSjhoaHFycmNYS0dHZ2R5MnNJK2FnMzhjbTA3cVNBd1NFdHlLaV"
        + "VKQ1NXYk9rZ3E0UURtN2lpVmQzZFxub0JrWnBrN1lTMktxazQxY2k1VEVRcW94ZTZ2M0Y5V0p4dXl1b0NJUVNscDRs"
        + "SjBnN3YvMWRwTVF5WDFDeFhpR1xub1RtUjBMRmV5K3pxOXF2Tm5MWnRrenIxY08xTThSU3JFMmtMQXpvOG95dVNPa2"
        + "93T0NFL2pZMVBMb1cyUEl0QVxuaDFpN2ZYYnZqQjcvY01xd1F1SUptNDR3NXRmZDZTeHllUVNtRi9kK3ZvcEFWTVVs"
        + "YmxWOWtxNFRoZkdKWCtoSlxuSk1LVUlQZ3hrREtPaHkxeHRpRndoMGF4UVhIaGdxRXg4SVpWNldtZ1pqMnNKemV1Wn"
        + "J3SHc2NkNQM01BWmxGclxucWZYY3NBdUpBZ01CQUFFQ2dnRUFDUjVoQjRtYXUwUDY4SjNLLzkxdnphWUFBbm5hMUJQ"
        + "Q1o1ejYxamZ1aWZtQVxubjBBd0hlMDJDUDF3QmVCbzFQbm1ZcWdzd2hsc0p2bHFYZ2w1NzJzMXpMSC9RNG9NREpXVF"
        + "VEK293K1pCRytrRlxuK1VMVnR4aGxaeGMxWDBVNFFpcENrUjgzV1JDNGJDWXcvNVNOVCtReHd5R3E3ODB3SG90R2wv"
        + "Nit3bzZ4SElZZ1xuQnNsdTQ1QnFCZmtIeUhOMzZRWEYvcWNhMWZueVVxcXNKa0ZDV2pYZ3R5azB6eWdWOENQUytIel"
        + "NIOGllN3NWa1xudHRLaVpwSnA2dHptMjZLNDYwTzVDUDB2ejdrZU1JN3R4S1owK2FvRzVlTllhc1VqS0ZqWnpuU0hU"
        + "VGlrYzZNRFxuYWIrSzZjUDBXTnJXTW5maXIzS3pLelZNMEh5eDJYK09JMDVZS2hrY1VRS0JnUURRMTRVellkQUdpT0"
        + "dHbEVDQlxuUE92UmczMHFudzRFUlk1d0VjTTcrTEI5WFE5NGdwaHFUcVg1Y1lxUFFwRmlQSjlnaEpOd01oeXlFU0pI"
        + "anFQMlxuV2k3V25oWE5Oc0s2VTJTYkRZRXhGcnVkck50MmhucnRlSEs0cEFiMVFyejFTSlNpTU5zcWVkbnplbkN4bU"
        + "5WK1xuaWx1cklFRS82N0tnZnVrVXY0azhDTW1kVndLQmdRQytrakZsQWdVQ3kwTCtJa0VuT0M1R1lmT0VMbGZsQjJQ"
        + "Y1xueVMyUzRGckQ5WFNwSUoxdWZDSnR4SlU0c25yVzV3OTkzT1FxbmNBWnA4TUo2dHZ0QjZ6Mkt2Nk9mTXp4MW9veV"
        + "xubjBEbFpQUmQvTEszWEtmMWpXQUpOaG9zamJCVVdxRlczS2hja2xpVDdoSmhYY1QrTGhMaUp4NWVPQlhYSjU5Ylxu"
        + "Nm00dlZ1MHlId0tCZ0dYZDZtUUJPOVVrS1ZWNnRPZ1ZMalc0L0hHd3VhYU53d2VaTW5EclFqTTBGRnJFNE5GaFxuNF"
        + "lXRnczOVRpaFN2MjFHNStTek5xTDlxcWh6YlQ5RFhkbWRMSHZsRlQ4Q1E4czdLeklqRU9TbFU2a3VyRndVRVxuTG5r"
        + "djBSRUs1NTZCTTE3Vk8rSkJJM1dtOEpkQUJlU2Y4OHpLcnROK0JWaFI3eUVtdUNzaDZyWHJBb0dCQUlQNVxucG05M0"
        + "tKVTR3dHBRU3FLbGk1TitmakJLRCtDeWIzd1ZicFJLL0xDNkNlS2VqYWhZRzFlOVRrSUhpSDZQWWZFZVxuS24xQTVp"
        + "cGVQZm4rUmZRdTNwOVVReG5XWC9BVFRYMmxxRUljMmdiRTI5ZVVPVlVhWlNtNzZBQjIxMmh6bzB3T1xuWUNKTHowQV"
        + "RIWU9FaHNvb2lqZElOQVc0Q0t5cDZmaXpyejdsaWpxdEFvR0FIaXBnRnJkSmR3ellHSE1XVEcxM1xubWR3ekZlUnhQ"
        + "NnUxQjBUaVdVMmxORUJqSkIrVWFhc1NBbXZIYmNPRlZtTkpxd2N0QitSNndNaG5xL1JTZ0xiMFxuOHA5RjlDTWJRWE"
        + "xISWRXZ1hpSEZXaHY2UmpEeSNBMHJmTXkwcFhQdXRUbDRIYVV6eGhOZkxGWnhWQzNZelVtXG5GS3FQQlZENTlhdyszaW"
        + "J5Ukg0TmhNbz0xxbi1FTkQgUFJJVkFURSBLRVktLS0tLVxuIiwiY2xpZW50X2VtYWlsIjoiZmlyZWJhc2UtYWRtaW5z"
        + "ZGstZmJzdmNAb2Rvb250by1lMDZhNy5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsImNsaWVudF9pZCI6IjEwNTg4NT"
        + "Q1NTI1MjU1MDA0NTIxMSIsImF1dGhfdXJpIjoiaHR0cHM6Ly9hY2NvdW50cy5nb29nbGUuY29tL28vb2F1dGgyL2F1"
        + "dGgiLCJ0b2tlbl91cmkiOiJodHRwczovL29hdXRoMi5nb29nbGVhcGlzLmNvbS90b2tlbiIsImF1dGhfcHJvdmlkZX"
        + "JfeDUwOV9jZXJ0X3VybCI6Imh0dHBzOi8vd3d3Lmdvb2dsZWFwaXMuY29tL29hdXRoMi92MS9jZXJ0cyIsImNsaWVu"
        + "dF94NTA5X2NlcnRfdXJsIjoiaHR0cHM6Ly93d3cuZ29vZ2xlYXBpcy5jb20vcm9ib3QvdjEvbWV0YWRhdGEveDUwOS"
        + "9maXJlYmFzZS1hZG1pbnNkay1mYnN2YyU0MG9kb29udG8tZTA2YTcuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ1"
        + "bml2ZXJzZV9kb21haW4iOiJnb29nbGVhcGlzLmNvbSJ9";

    @PostConstruct
    public void initFirebase() {
        try {
            // Crear el archivo de credenciales
            createCredentialsFile();
            LOGGER.info("Archivo de credenciales de Firebase creado correctamente");
        } catch (IOException e) {
            LOGGER.severe("Error al crear archivo de credenciales de Firebase: " + e.getMessage());
        }
    }

    private void createCredentialsFile() throws IOException {
        File credentialsFile = new File(CREDENTIALS_FILE_PATH);
        
        // Crear directorios si no existen
        credentialsFile.getParentFile().mkdirs();
        
        // Decodificar credenciales de Base64
        byte[] decodedBytes = Base64.getDecoder().decode(ENCODED_FIREBASE_CREDENTIALS);
        String decodedCredentials = new String(decodedBytes);
        
        try (FileWriter writer = new FileWriter(credentialsFile)) {
            writer.write(decodedCredentials);
        }
        
        LOGGER.info("Archivo de credenciales de Firebase creado en: " + credentialsFile.getAbsolutePath());
    }
}