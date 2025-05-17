// src/services/api.js
import axios from 'axios';

// Usando la configuración global de axios que ya está en main.jsx
const api = axios.create({
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Interceptor para detectar respuestas HTML
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
    // Agregar más información de depuración
    if (error.response) {
      // La solicitud fue realizada y el servidor respondió con un código de estado
      console.error('Error de respuesta:', error.response.status, error.response.data);
    } else if (error.request) {
      // La solicitud fue realizada pero no se recibió respuesta
      console.error('Error de solicitud (sin respuesta):', error.request);
    } else {
      // Algo sucedió al configurar la solicitud
      console.error('Error general:', error.message);
    }
    return Promise.reject(error);
  }
);

// Servicios para Pacientes
export const getPatients = async () => {
  try {
    const response = await api.get('/api/patients');
    return response.data;
  } catch (error) {
    console.error('Error al obtener pacientes:', error);
    throw error;
  }
};

export const getPatientById = async (id) => {
  try {
    const response = await api.get(`/api/patients/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener paciente con ID ${id}:`, error);
    throw error;
  }
};

export const createPatient = async (patientData) => {
  try {
    const response = await api.post('/api/patients', patientData);
    return response.data;
  } catch (error) {
    console.error('Error al crear paciente:', error);
    throw error;
  }
};

export const updatePatient = async (id, patientData) => {
  try {
    const response = await api.put(`/api/patients/${id}`, patientData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar paciente con ID ${id}:`, error);
    throw error;
  }
};

export const deletePatient = async (id) => {
  try {
    await api.delete(`/api/patients/${id}`);
    return true;
  } catch (error) {
    console.error(`Error al eliminar paciente con ID ${id}:`, error);
    throw error;
  }
};

// Servicios para Doctores
export const getDoctors = async () => {
  try {
    const response = await api.get('/api/doctors');
    return response.data;
  } catch (error) {
    console.error('Error al obtener doctores:', error);
    throw error;
  }
};

export const getDoctorById = async (id) => {
  try {
    const response = await api.get(`/api/doctors/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener doctor con ID ${id}:`, error);
    throw error;
  }
};

export const createDoctor = async (doctorData) => {
  try {
    const response = await api.post('/api/doctors', doctorData);
    return response.data;
  } catch (error) {
    console.error('Error al crear doctor:', error);
    throw error;
  }
};

export const updateDoctor = async (id, doctorData) => {
  try {
    const response = await api.put(`/api/doctors/${id}`, doctorData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar doctor con ID ${id}:`, error);
    throw error;
  }
};

export const deleteDoctor = async (id) => {
  try {
    await api.delete(`/api/doctors/${id}`);
    return true;
  } catch (error) {
    console.error(`Error al eliminar doctor con ID ${id}:`, error);
    throw error;
  }
};

// Servicios para Citas
export const getAppointments = async (date) => {
  try {
    const params = date ? { date: date.toISOString() } : {};
    const response = await api.get('/api/appointments', { params });
    return response.data;
  } catch (error) {
    console.error('Error al obtener citas:', error);
    throw error;
  }
};

export const getAppointmentsByDoctor = async (doctorId, date) => {
  try {
    const params = date ? { date: date.toISOString() } : {};
    const response = await api.get(`/api/doctors/${doctorId}/appointments`, { params });
    return response.data;
  } catch (error) {
    console.error(`Error al obtener citas para el doctor ${doctorId}:`, error);
    throw error;
  }
};

export const getAppointmentsByPatient = async (patientId) => {
  try {
    const response = await api.get(`/api/patients/${patientId}/appointments`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener citas para el paciente ${patientId}:`, error);
    throw error;
  }
};

export const createAppointment = async (appointmentData) => {
  try {
    const response = await api.post('/api/appointments', appointmentData);
    return response.data;
  } catch (error) {
    console.error('Error al crear cita:', error);
    throw error;
  }
};

export const updateAppointment = async (id, appointmentData) => {
  try {
    const response = await api.put(`/api/appointments/${id}`, appointmentData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar cita ${id}:`, error);
    throw error;
  }
};

export const updateAppointmentStatus = async (id, status) => {
  try {
    const response = await api.patch(`/api/appointments/${id}/status`, { status });
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar estado de cita ${id}:`, error);
    throw error;
  }
};

export const deleteAppointment = async (id) => {
  try {
    await api.delete(`/api/appointments/${id}`);
    return true;
  } catch (error) {
    console.error(`Error al eliminar cita ${id}:`, error);
    throw error;
  }
};

// Servicios para Odontograma
export const getOdontogramByPatientId = async (patientId) => {
  try {
    const response = await api.get(`/api/patients/${patientId}/odontogram`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener odontograma para paciente ${patientId}:`, error);
    throw error;
  }
};

export const addLesions = async (patientId, lesions) => {
  try {
    const response = await api.post(`/api/patients/${patientId}/odontogram/lesions`, lesions);
    return response.data;
  } catch (error) {
    console.error(`Error al añadir lesiones al odontograma del paciente ${patientId}:`, error);
    throw error;
  }
};

export const removeLesions = async (patientId, lesions) => {
  try {
    const response = await api.delete(`/api/patients/${patientId}/odontogram/lesions`, {
      data: lesions
    });
    return response.data;
  } catch (error) {
    console.error(`Error al eliminar lesiones del odontograma del paciente ${patientId}:`, error);
    throw error;
  }
};

export default api;