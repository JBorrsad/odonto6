package odoonto.application.service;

import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.aggregates.Patient;
import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.dto.request.LesionCreateDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.OdontogramMapper;
import odoonto.domain.repository.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de aplicación para la gestión de odontogramas.
 * Nota: El odontograma es un objeto de valor que forma parte del agregado Patient.
 */
@Service
public class OdontogramService {

    private final PatientRepository patientRepository;
    private final OdontogramMapper odontogramMapper;

    @Autowired
    public OdontogramService(PatientRepository patientRepository, OdontogramMapper odontogramMapper) {
        this.patientRepository = patientRepository;
        this.odontogramMapper = odontogramMapper;
    }

    /**
     * Obtiene el odontograma actual de un paciente
     * @param patientId ID del paciente
     * @return DTO del odontograma
     * @throws PatientNotFoundException si no se encuentra el paciente
     */
    public OdontogramDTO getOdontogram(String patientId) {
        return patientRepository.findById(patientId)
                .map(Patient::getOdontogram)
                .map(this::toDTO)
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

    /**
     * Añade una lesión al odontograma de un paciente
     * @param patientId ID del paciente
     * @param toothId ID del diente
     * @param face Cara del diente
     * @param lesionType Tipo de lesión
     * @return DTO del odontograma actualizado
     * @throws PatientNotFoundException si no se encuentra el paciente
     */
    public OdontogramDTO addLesion(String patientId, String toothId, String face, String lesionType) {
        Patient patient = patientRepository.findById(patientId)
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(patientId));
        
        Odontogram odontogram = patient.getOdontogram();
        odontogram.addLesion(toothId, ToothFace.fromCodigo(face), LesionType.valueOf(lesionType));
        
        patient.setOdontogram(odontogram);
        patientRepository.save(patient).block();
        
        return toDTO(odontogram);
    }

    /**
     * Elimina una lesión del odontograma de un paciente
     * @param patientId ID del paciente
     * @param toothId ID del diente
     * @param face Cara del diente
     * @return DTO del odontograma actualizado
     * @throws PatientNotFoundException si no se encuentra el paciente
     */
    public OdontogramDTO removeLesion(String patientId, String toothId, String face) {
        Patient patient = patientRepository.findById(patientId)
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(patientId));
        
        Odontogram odontogram = patient.getOdontogram();
        odontogram.removeLesion(toothId, ToothFace.fromCodigo(face));
        
        patient.setOdontogram(odontogram);
        patientRepository.save(patient).block();
        
        return toDTO(odontogram);
    }

    /**
     * Convierte un odontograma a su DTO
     * @param odontogram Odontograma
     * @return DTO del odontograma
     */
    private OdontogramDTO toDTO(Odontogram odontogram) {
        OdontogramDTO dto = new OdontogramDTO();
        // Implementar la conversión según la estructura del DTO
        return dto;
    }
} 