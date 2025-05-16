// src/services/api.js
import axios from 'axios';

// Usando la configuración global de axios que ya está en main.jsx
const api = axios.create();

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

// Servicios para Citas
export const getAppointments = async (date) => {
  try {
    const params = date ? { date } : {};
    const response = await api.get('/api/appointments', { params });
    return response.data;
  } catch (error) {
    console.error('Error al obtener citas:', error);
    throw error;
  }
};

export const getAppointmentsByDoctor = async (doctorId, date) => {
  try {
    const params = date ? { date } : {};
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

export const updateAppointmentStatus = async (id, status) => {
  try {
    const response = await api.patch(`/api/appointments/${id}/status`, { status });
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar estado de cita ${id}:`, error);
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

export const updateOdontogram = async (patientId, odontogramData) => {
  try {
    const response = await api.put(`/api/patients/${patientId}/odontogram`, odontogramData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar odontograma para paciente ${patientId}:`, error);
    throw error;
  }
};

export default api;