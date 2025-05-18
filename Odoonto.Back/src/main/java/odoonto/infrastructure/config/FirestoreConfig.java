package odoonto.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.cloud.spring.data.firestore.mapping.FirestoreMappingContext;
import com.google.cloud.spring.data.firestore.repository.config.EnableReactiveFirestoreRepositories;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Configuración para conectar con Firestore
 */
@Configuration
@EnableReactiveFirestoreRepositories(basePackages = "odoonto.infrastructure.persistence.reactive")
public class FirestoreConfig {
    private static final Logger LOGGER = Logger.getLogger(FirestoreConfig.class.getName());
    private static final String CONFIG_PATH = "src/main/resources/config-data.json";
    
    @Autowired
    private ServiceInitializer serviceInit;

    @Bean
    @Profile("prod")
    @DependsOn("serviceInitializer")
    public Firestore firestoreProd() throws IOException {
        return getFirestore();
    }
    
    @Bean
    @Profile("!prod")
    @DependsOn("serviceInitializer")
    public Firestore firestore() throws IOException {
        return getFirestore();
    }
    
    private Firestore getFirestore() throws IOException {
        File configFile = new File(CONFIG_PATH);
        
        if (!configFile.exists()) {
            LOGGER.severe("No se encontró el archivo en: " + configFile.getAbsolutePath());
            throw new IOException("El archivo necesario no existe. Asegúrate de que ServiceInitializer se ejecute primero.");
        }
        
        Firestore dbInstance;
        
        try (FileInputStream serviceData = new FileInputStream(configFile)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceData))
                    .build();
            
            // Inicializa si no está ya inicializado
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                LOGGER.info("Conexión inicializada correctamente usando archivo de configuración.");
            }
            
            // Obtener instancia
            dbInstance = FirestoreClient.getFirestore();
            
            // No eliminamos el archivo porque lo necesitan otros componentes
            LOGGER.info("Conexiones inicializadas correctamente.");
            
            return dbInstance;
        }
    }
    
    @PreDestroy
    public void cleanUp() {
        // Eliminar el archivo al cerrar la aplicación
        File tempFile = new File(CONFIG_PATH);
        if (tempFile.exists() && tempFile.delete()) {
            LOGGER.info("Archivo temporal eliminado al cerrar la aplicación.");
        }
    }
    
    @Bean
    public FirestoreMappingContext firestoreMappingContext() {
        return new FirestoreMappingContext();
    }
    
    // La configuración de FirestoreTemplate se realizará automáticamente
    // a través de la anotación EnableReactiveFirestoreRepositories
} 