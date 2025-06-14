package odoonto.domain.specifications;

import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.Sexo;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.MedicalRecordId;

import java.time.LocalDate;
import java.time.Period;

import java.util.Map;

/**
 * Especificaciones para consultas complejas sobre pacientes.
 */
public class PatientSpecification {
    
    /**
     * Crea una especificación para filtrar pacientes por nombre o apellido
     * @param term Término de búsqueda
     * @return Especificación resultante
     */
    public static Specification<Patient> nameOrLastNameContains(String term) {
        return patient -> {
            if (term == null || term.trim().isEmpty()) {
                return true;
            }
            
            String lowerTerm = term.toLowerCase();
            return (patient.getNombre() != null && patient.getNombre().toLowerCase().contains(lowerTerm)) ||
                   (patient.getApellido() != null && patient.getApellido().toLowerCase().contains(lowerTerm));
        };
    }
    
    /**
     * Crea una especificación para filtrar pacientes por sexo
     * @param sexo Sexo a filtrar
     * @return Especificación resultante
     */
    public static Specification<Patient> bySexo(Sexo sexo) {
        return patient -> {
            if (sexo == null) {
                return true;
            }
            
            return patient.getSexo() == sexo;
        };
    }
    
    /**
     * Crea una especificación para filtrar pacientes por rango de edad
     * @param minAge Edad mínima (inclusive)
     * @param maxAge Edad máxima (inclusive)
     * @return Especificación resultante
     */
    public static Specification<Patient> byAgeRange(Integer minAge, Integer maxAge) {
        return patient -> {
            if (patient.getFechaNacimiento() == null) {
                return false;
            }
            
            LocalDate now = LocalDate.now();
            int age = Period.between(patient.getFechaNacimiento(), now).getYears();
            
            boolean minCheck = minAge == null || age >= minAge;
            boolean maxCheck = maxAge == null || age <= maxAge;
            
            return minCheck && maxCheck;
        };
    }
    
    /**
     * Crea una especificación para filtrar pacientes con citas pendientes
     * @return Especificación resultante
     */
    public static Specification<Patient> withPendingAppointments() {
        return patient -> !patient.getAppointmentIds().isEmpty();
    }
    
    /**
     * Crea una especificación para filtrar pacientes sin historial médico registrado
     * @return Especificación resultante
     */
    public static Specification<Patient> withoutMedicalRecord() {
        return patient -> {
            MedicalRecordId medicalRecordId = patient.deriveMedicalRecordId();
            return medicalRecordId == null || medicalRecordId.getValue().isEmpty();
        };
    }
    
    /**
     * Crea una especificación para filtrar pacientes activos (con actividad reciente)
     * @param monthsThreshold Número de meses para considerar activo
     * @return Especificación resultante
     */
    public static Specification<Patient> isActive(int monthsThreshold) {
        return patient -> {
            if (patient.getLastVisitDate() == null) {
                return false;
            }
            
            LocalDate thresholdDate = LocalDate.now().minusMonths(monthsThreshold);
            return !patient.getLastVisitDate().isBefore(thresholdDate);
        };
    }
    
    /**
     * Crea una especificación para filtrar pacientes por tener lesiones sin tratar
     * @return Especificación resultante
     */
    public static Specification<Patient> hasUntreatedLesions() {
        return patient -> {
            // Verificar si el paciente tiene un odontograma
            Odontogram odontogram = patient.getOdontogram();
            if (odontogram == null) {
                return false;
            }
            
            // Verificar si el odontograma tiene dientes con lesiones
            Map<String, Odontogram.ToothRecord> teeth = odontogram.getTeeth();
            return teeth != null && !teeth.isEmpty();
        };
    }
} 