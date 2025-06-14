package odoonto.domain.repository.staff;

import odoonto.domain.model.staff.aggregates.DoctorAggregate;
import odoonto.domain.model.staff.valueobjects.DoctorId;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    
    void save(DoctorAggregate doctor);
    
    Optional<DoctorAggregate> findById(DoctorId doctorId);
    
    List<DoctorAggregate> findAll();
    
    void delete(DoctorId doctorId);
} 