// src/main/java/odoonto/api/application/ManageDoctorsService.java
package odoonto.api.application;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import odoonto.api.domain.models.Doctor;
import odoonto.api.domain.repositories.DoctorRepository;

@Service
public class ManageDoctorsService {

    private final DoctorRepository repo;

    public ManageDoctorsService(DoctorRepository repo) {
        this.repo = repo;
    }

    public Flux<Doctor> findAll() {
        return repo.findAll();
    }

    public Mono<Doctor> findById(String id) {
        return repo.findById(id);
    }

    public Mono<Doctor> create(Doctor doctor) {
        return repo.save(doctor);
    }

    public Mono<Doctor> update(String id, Doctor doctor) {
        doctor.setId(id);
        return repo.save(doctor);
    }

    public Mono<Void> delete(String id) {
        return repo.deleteById(id);
    }
}
