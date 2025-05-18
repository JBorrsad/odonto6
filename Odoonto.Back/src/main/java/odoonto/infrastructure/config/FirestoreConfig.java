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
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/firebase-service-account.json";
    
    @Autowired
    private FirebaseCredentialsInitializer firebaseCredentialsInitializer;

    @Bean
    @Profile("prod")
    @DependsOn("firebaseCredentialsInitializer")
    public Firestore firestoreProd() throws IOException {
        return getFirestore();
    }
    
    @Bean
    @Profile("!prod")
    @DependsOn("firebaseCredentialsInitializer")
    public Firestore firestore() throws IOException {
        return getFirestore();
    }
    
    private Firestore getFirestore() throws IOException {
        File credentialsFile = new File(CREDENTIALS_FILE_PATH);
        
        if (!credentialsFile.exists()) {
            LOGGER.severe("No se encontró el archivo de credenciales en: " + credentialsFile.getAbsolutePath());
            throw new IOException("El archivo de credenciales no existe. Asegúrate de que FirebaseCredentialsInitializer se ejecute primero.");
        }
        
        Firestore firestoreInstance;
        
        try (FileInputStream serviceAccount = new FileInputStream(credentialsFile)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            // Inicializa Firebase si no está ya inicializado
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                LOGGER.info("Firebase inicializado correctamente usando el archivo de credenciales.");
            }
            
            // Obtener instancia de Firestore
            firestoreInstance = FirestoreClient.getFirestore();
            
            // No eliminamos el archivo de credenciales porque lo necesitan otros componentes
            LOGGER.info("Firebase y Firestore inicializados correctamente.");
            
            return firestoreInstance;
        }
    }
    
    @PreDestroy
    public void cleanUp() {
        // Eliminar el archivo de credenciales al cerrar la aplicación
        File credentialsFile = new File(CREDENTIALS_FILE_PATH);
        if (credentialsFile.exists() && credentialsFile.delete()) {
            LOGGER.info("Archivo de credenciales eliminado al cerrar la aplicación.");
        }
    }
    
    @Bean
    public FirestoreMappingContext firestoreMappingContext() {
        return new FirestoreMappingContext();
    }
    
    // La configuración de FirestoreTemplate se realizará automáticamente
    // a través de la anotación EnableReactiveFirestoreRepositories
} 