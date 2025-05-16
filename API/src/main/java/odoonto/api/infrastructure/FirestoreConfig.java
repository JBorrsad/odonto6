package odoonto.api.infrastructure;

import com.google.cloud.spring.data.firestore.repository.config.EnableReactiveFirestoreRepositories;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableReactiveFirestoreRepositories(basePackages = "odoonto.api.domain.repositories")
public class FirestoreConfig {
    // La anotación @EnableReactiveFirestoreRepositories es suficiente para
    // que Spring encuentre y registre los repositorios de Firestore
} 