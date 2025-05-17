package odoonto.interface.rest.advice;

import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.exceptions.DoctorNotFoundException;
import odoonto.application.exceptions.AppointmentConflictException;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.exceptions.InvalidToothFaceException;
import odoonto.domain.exceptions.InvalidAppointmentTimeException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Manejador global de excepciones para la API REST
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    /**
     * Clase para representar errores en la respuesta
     */
    static class ErrorResponse {
        private final String code;
        private final String message;
        
        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Maneja excepciones de tipo PatientNotFoundException
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFound(PatientNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("PATIENT_NOT_FOUND", ex.getMessage()));
    }
    
    /**
     * Maneja excepciones de tipo DoctorNotFoundException
     */
    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDoctorNotFound(DoctorNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("DOCTOR_NOT_FOUND", ex.getMessage()));
    }
    
    /**
     * Maneja excepciones de tipo AppointmentConflictException
     */
    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<ErrorResponse> handleAppointmentConflict(AppointmentConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("APPOINTMENT_CONFLICT", ex.getMessage()));
    }
    
    /**
     * Maneja excepciones específicas del dominio
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("DOMAIN_ERROR", ex.getMessage()));
    }
    
    /**
     * Maneja excepciones de cara de diente inválida
     */
    @ExceptionHandler(InvalidToothFaceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToothFace(InvalidToothFaceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_TOOTH_FACE", ex.getMessage()));
    }
    
    /**
     * Maneja excepciones de tiempo de cita inválido
     */
    @ExceptionHandler(InvalidAppointmentTimeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAppointmentTime(InvalidAppointmentTimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_APPOINTMENT_TIME", ex.getMessage()));
    }
    
    /**
     * Maneja cualquier otra excepción no capturada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
    }
} 