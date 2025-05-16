package odoonto.api.domain.repositories;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import odoonto.api.domain.models.Doctor;

public interface DoctorRepository
        extends FirestoreReactiveRepository<Doctor> {
}