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

// Buscar pacientes por nombre o apellido
export const search = async (query) => {
  try {
    const response = await axios.get(`${API_URL}/search`, {
      params: { query }
    });
    return response.data;
  } catch (error) {
    console.error(`Error al buscar pacientes con query "${query}":`, error);
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
    // Solo interceptar errores si NO estamos en el contexto del test de la API
    const isApiTest = window.location.href.includes('api-test') || window.location.pathname.includes('api-test');
    
    if (!isApiTest && (error.response?.status === 404 || error.response?.status === 500)) {
      console.log(`El paciente ${patientId} no tiene odontograma creado aún`);
      return null; // Devolver null en lugar de lanzar error solo para la UI normal
    }
    
    console.error(`Error al obtener odontograma del paciente ${patientId}:`, error);
    throw error; // Lanzar el error real para el test de la API
  }
};

// Obtener el odontograma de un paciente SIN manejo de errores (para test de API)
export const getOdontogramRaw = async (patientId) => {
  try {
    const response = await axios.get(`${API_URL}/${patientId}/odontogram`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener odontograma del paciente ${patientId}:`, error);
    throw error; // Lanzar el error real para que se vea en el test de la API
  }
};

// ============ Nota: Las funciones de lesiones ahora se manejan directamente en odontogramService.js ============
// debido a los cambios en la estructura de endpoints del backend 