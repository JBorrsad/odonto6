// src/services/api.js
import axios from 'axios';

// Usando la configuración global de axios que ya está en main.jsx
const api = axios.create({
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Interceptor para detectar respuestas HTML y manejar errores
api.interceptors.response.use(
  response => {
    // Si la respuesta es un string que parece HTML, lanzar un error
    if (typeof response.data === 'string' && response.data.includes('<!doctype html')) {
      console.error('Respuesta HTML recibida:', response.data);
      throw new Error('La API respondió con HTML en lugar de JSON. Verifica que el backend esté corriendo y configurado correctamente.');
    }
    return response;
  },
  error => {
    // Agregar más información de depuración y manejo específico de errores
    if (error.response) {
      // La solicitud fue realizada y el servidor respondió con un código de estado
      const status = error.response.status;
      const url = error.config?.url || 'URL desconocida';
      
      // Mensajes específicos según el tipo de error
      if (status === 404) {
        console.log(`Recurso no encontrado (404): ${url}`);
        // Para odontogramas, solo interceptar si NO es una llamada desde el test de API
        if (url.includes('/odontogram') && !window.location.href.includes('api-test')) {
          console.log('El odontograma aún no existe para este paciente');
        }
      } else if (status === 500) {
        console.error(`Error interno del servidor (500): ${url}`, error.response.data);
        // No agregar userMessage para odontogramas si es desde el test de API
        if (!url.includes('/odontogram') || window.location.href.includes('api-test')) {
          error.userMessage = 'Error interno del servidor. Por favor, inténtalo de nuevo más tarde.';
        }
      } else if (status === 400) {
        console.error(`Solicitud incorrecta (400): ${url}`, error.response.data);
        error.userMessage = 'Datos incorrectos enviados al servidor.';
      } else if (status === 401) {
        console.error(`No autorizado (401): ${url}`);
        error.userMessage = 'No tienes autorización para realizar esta acción.';
      } else if (status === 403) {
        console.error(`Prohibido (403): ${url}`);
        error.userMessage = 'Acceso prohibido a este recurso.';
      } else {
        console.error(`Error de respuesta (${status}): ${url}`, error.response.data);
      }
    } else if (error.request) {
      // La solicitud fue realizada pero no se recibió respuesta
      console.error('Error de solicitud (sin respuesta):', error.request);
      error.userMessage = 'No se pudo conectar con el servidor. Verifica tu conexión a internet.';
    } else {
      // Algo sucedió al configurar la solicitud
      console.error('Error general:', error.message);
      error.userMessage = error.message;
    }
    return Promise.reject(error);
  }
);

// Re-exportar servicios modulares para mantener compatibilidad
import * as patientService from './patientService.js';
import * as doctorService from './doctorService.js';
import * as appointmentService from './appointmentService.js';
import * as odontogramService from './odontogramService.js';
import * as medicalRecordService from './medicalRecordService.js';

// Servicios para Pacientes (usando servicios modulares)
export const getPatients = patientService.getAll;
export const getPatientById = patientService.getById;
export const createPatient = patientService.create;
export const updatePatient = patientService.update;
export const deletePatient = patientService.remove;

// Servicios para Doctores (usando servicios modulares)
export const getDoctors = doctorService.getAll;
export const getDoctorById = doctorService.getById;
export const createDoctor = doctorService.create;
export const updateDoctor = doctorService.update;
export const deleteDoctor = doctorService.remove;

// Servicios para Citas (usando servicios modulares)
export const getAppointments = appointmentService.getAll;
export const createAppointment = appointmentService.create;
export const updateAppointment = appointmentService.update;
export const deleteAppointment = appointmentService.remove;

// Funciones específicas de citas que usan los endpoints correctos del backend
export const getAppointmentsByDoctor = appointmentService.getByDoctor;
export const getAppointmentsByPatient = appointmentService.getByPatient;

// Servicios para Odontograma (usando servicios modulares)
export const getOdontogramByPatientId = odontogramService.getOdontogram;

export default api;