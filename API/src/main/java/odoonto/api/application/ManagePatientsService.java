package odoonto.api.application;

import odoonto.api.presentation.dto.LesionCommandDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import odoonto.api.domain.models.Patient;
import odoonto.api.domain.models.Odontogram;
import odoonto.api.domain.core.valueobjects.ToothFace;
import odoonto.api.domain.core.valueobjects.LesionType;
import odoonto.api.domain.repositories.PatientRepository;

import java.util.List;

@Service
public class ManagePatientsService {

    private final PatientRepository repo;

    public ManagePatientsService(PatientRepository repo) {
        this.repo = repo;
    }

    // CRUD Paciente
    public Flux<Patient> findAll() {
        return repo.findAll();
    }

    public Mono<Patient> findById(String id) {
        return repo.findById(id);
    }

    public Mono<Patient> create(Patient p) {
        return repo.save(p);
    }

    public Mono<Patient> update(String id, Patient p) {
        p.setId(id);
        return repo.save(p);
    }

    public Mono<Void> delete(String id) {
        return repo.deleteById(id);
    }

    // Odontograma
    public Mono<Odontogram> getOdontogram(String patientId) {
        return repo.findById(patientId)
                .map(Patient::getOdontogram);
    }

    public Mono<Odontogram> addLesions(String patientId, List<LesionCommandDto> commands) {
        return repo.findById(patientId)
                .flatMap(p -> {
                    Odontogram od = p.getOdontogram();
                    commands.forEach(cmd ->
                            od.addLesion(cmd.getToothId(), ToothFace.valueOf(cmd.getFace()), LesionType.valueOf(cmd.getLesion()))
                    );
                    p.setOdontogram(od);
                    return repo.save(p);
                })
                .map(Patient::getOdontogram);
    }

    public Mono<Odontogram> removeLesions(String patientId, List<LesionCommandDto> commands) {
        return repo.findById(patientId)
                .flatMap(p -> {
                    Odontogram od = p.getOdontogram();
                    commands.forEach(cmd ->
                            od.removeLesion(cmd.getToothId(), ToothFace.valueOf(cmd.getFace()))
                    );
                    p.setOdontogram(od);
                    return repo.save(p);
                })
                .map(Patient::getOdontogram);
    }
}
