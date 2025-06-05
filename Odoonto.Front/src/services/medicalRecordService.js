import axios from 'axios';

const API_URL = '/api/medical-records';

// Obtener todos los historiales médicos
export const getAll = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error al obtener historiales médicos:', error);
    throw error;
  }
};

// Obtener un historial médico por ID
export const getById = async (id) => {
  try {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener historial médico ${id}:`, error);
    throw error;
  }
};

// Obtener el historial médico de un paciente
export const getByPatientId = async (patientId) => {
  try {
    const response = await axios.get(`/api/patients/${patientId}/medical-record`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener historial médico del paciente ${patientId}:`, error);
    throw error;
  }
};

// Añadir una entrada al historial médico
export const addEntry = async (medicalRecordId, entryDescription) => {
  try {
    await axios.post(`${API_URL}/${medicalRecordId}/entries`, null, {
      params: { entryDescription }
    });
    return true;
  } catch (error) {
    console.error(`Error al añadir entrada al historial médico ${medicalRecordId}:`, error);
    throw error;
  }
}; 