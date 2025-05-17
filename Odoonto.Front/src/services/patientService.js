import axios from 'axios';

const API_URL = '/api/patients';

// Obtener todos los pacientes
export const getAll = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error al obtener pacientes:', error);
    throw error;
  }
};

// Obtener un paciente por ID
export const getById = async (id) => {
  try {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener paciente ${id}:`, error);
    throw error;
  }
};

// Crear un nuevo paciente
export const create = async (patient) => {
  try {
    const response = await axios.post(API_URL, patient);
    return response.data;
  } catch (error) {
    console.error('Error al crear paciente:', error);
    throw error;
  }
};

// Actualizar un paciente existente
export const update = async (id, patient) => {
  try {
    const response = await axios.put(`${API_URL}/${id}`, patient);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar paciente ${id}:`, error);
    throw error;
  }
};

// Eliminar un paciente
export const remove = async (id) => {
  try {
    await axios.delete(`${API_URL}/${id}`);
    return true;
  } catch (error) {
    console.error(`Error al eliminar paciente ${id}:`, error);
    throw error;
  }
};

// ============ Odontograma ============

// Obtener el odontograma de un paciente
export const getOdontogram = async (patientId) => {
  try {
    const response = await axios.get(`${API_URL}/${patientId}/odontogram`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener odontograma del paciente ${patientId}:`, error);
    throw error;
  }
};

// Añadir lesiones al odontograma
export const addLesions = async (patientId, lesions) => {
  try {
    const response = await axios.post(`${API_URL}/${patientId}/odontogram/lesions`, lesions);
    return response.data;
  } catch (error) {
    console.error(`Error al añadir lesiones al odontograma ${patientId}:`, error);
    throw error;
  }
};

// Eliminar lesiones del odontograma
export const removeLesions = async (patientId, lesions) => {
  try {
    // Usar axios con config para incluir body en DELETE
    const response = await axios.delete(`${API_URL}/${patientId}/odontogram/lesions`, {
      data: lesions
    });
    return response.data;
  } catch (error) {
    console.error(`Error al eliminar lesiones del odontograma ${patientId}:`, error);
    throw error;
  }
}; 