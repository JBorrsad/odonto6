package odoonto.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.cloud.spring.data.firestore.mapping.FirestoreMappingContext;
import com.google.cloud.spring.data.firestore.repository.config.EnableReactiveFirestoreRepositories;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Configuración para conectar con Firestore
 */
@Configuration
@EnableReactiveFirestoreRepositories(basePackages = "odoonto.infrastructure.persistence.reactive")
public class FirestoreConfig {

    @Bean
    public Firestore firestore() throws IOException {
        // Intenta cargar credenciales desde el classpath
        InputStream serviceAccount = new ClassPathResource("firebase-adminsdk.json").getInputStream();
        
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        
        // Inicializa Firebase si no está ya inicializado
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
        
        return FirestoreClient.getFirestore();
    }
    
    @Bean
    public FirestoreMappingContext firestoreMappingContext() {
        return new FirestoreMappingContext();
    }
    
    // La configuración de FirestoreTemplate se realizará automáticamente
    // a través de la anotación EnableReactiveFirestoreRepositories
} 