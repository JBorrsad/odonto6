package odoonto.presentation.rest.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import odoonto.domain.exceptions.DomainException;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.exceptions.DoctorNotFoundException;
import odoonto.application.exceptions.OdontogramNotFoundException;
import odoonto.application.exceptions.MedicalRecordNotFoundException;
import odoonto.application.exceptions.AppointmentConflictException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Estructura básica de respuesta de error
     * @param exception Excepción capturada
     * @param status Estado HTTP a devolver
     * @param request Petición web
     * @return Respuesta con información de error estructurada
     */
    private ResponseEntity<Object> createErrorResponse(Exception exception, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", exception.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, status);
    }
    
    /**
     * Maneja excepciones de dominio
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Object> handleDomainException(DomainException ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }
    
    /**
     * Maneja excepciones de paciente no encontrado
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Object> handlePatientNotFoundException(PatientNotFoundException ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }
    
    /**
     * Maneja excepciones de doctor no encontrado
     */
    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<Object> handleDoctorNotFoundException(DoctorNotFoundException ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }
    
    /**
     * Maneja excepciones de odontograma no encontrado
     */
    @ExceptionHandler(OdontogramNotFoundException.class)
    public ResponseEntity<Object> handleOdontogramNotFoundException(OdontogramNotFoundException ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }
    
    /**
     * Maneja excepciones de historial médico no encontrado
     */
    @ExceptionHandler(MedicalRecordNotFoundException.class)
    public ResponseEntity<Object> handleMedicalRecordNotFoundException(MedicalRecordNotFoundException ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }
    
    /**
     * Maneja excepciones de conflicto de citas
     */
    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<Object> handleAppointmentConflictException(AppointmentConflictException ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.CONFLICT, request);
    }
    
    /**
     * Maneja todas las demás excepciones no capturadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
} 