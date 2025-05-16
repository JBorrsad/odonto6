package odoonto.api.domain.repositories;


import odoonto.api.domain.models.Patient;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import reactor.core.publisher.Mono;

public interface PatientRepository
        extends FirestoreReactiveRepository<Patient> {
    Mono<Boolean> existsByEmailAndTelefono(String email, String telefono);
}