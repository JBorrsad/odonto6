import axios from 'axios';

const API_URL = '/api/appointments';

// Obtener todas las citas
export const getAll = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error al obtener citas:', error);
    throw error;
  }
};

// Obtener una cita por ID
export const getById = async (id) => {
  try {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener cita ${id}:`, error);
    throw error;
  }
};

// Crear una nueva cita (simple CRUD, sin validación de solapamientos)
export const create = async (appointment) => {
  try {
    const response = await axios.post(API_URL, appointment);
    return response.data;
  } catch (error) {
    console.error('Error al crear cita:', error);
    throw error;
  }
};

// Reservar cita (con validación de solapamientos)
export const book = async (appointment) => {
  try {
    const response = await axios.post(`${API_URL}/book`, appointment);
    return response.data;
  } catch (error) {
    console.error('Error al reservar cita:', error);
    throw error;
  }
};

// Actualizar una cita existente
export const update = async (id, appointment) => {
  try {
    const response = await axios.put(`${API_URL}/${id}`, appointment);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar cita ${id}:`, error);
    throw error;
  }
};

// Eliminar una cita
export const remove = async (id) => {
  try {
    await axios.delete(`${API_URL}/${id}`);
    return true;
  } catch (error) {
    console.error(`Error al eliminar cita ${id}:`, error);
    throw error;
  }
};

// Funciones auxiliares para filtrar citas

// Obtener citas por fecha
export const getByDate = async (date) => {
  try {
    const allAppointments = await getAll();
    
    // Formatear fecha para comparación
    const targetDate = new Date(date);
    const targetDateStr = targetDate.toISOString().split('T')[0];
    
    return allAppointments.filter(appointment => {
      const appointmentDate = new Date(appointment.start);
      return appointmentDate.toISOString().split('T')[0] === targetDateStr;
    });
  } catch (error) {
    console.error(`Error al obtener citas para la fecha ${date}:`, error);
    throw error;
  }
};

// Obtener citas por doctor
export const getByDoctor = async (doctorId) => {
  try {
    const allAppointments = await getAll();
    return allAppointments.filter(appointment => appointment.doctorId === doctorId);
  } catch (error) {
    console.error(`Error al obtener citas para el doctor ${doctorId}:`, error);
    throw error;
  }
};

// Obtener citas por paciente
export const getByPatient = async (patientId) => {
  try {
    const allAppointments = await getAll();
    return allAppointments.filter(appointment => appointment.patientId === patientId);
  } catch (error) {
    console.error(`Error al obtener citas para el paciente ${patientId}:`, error);
    throw error;
  }
}; 