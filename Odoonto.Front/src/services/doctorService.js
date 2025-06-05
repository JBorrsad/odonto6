import axios from 'axios';

const API_URL = '/api/doctors';

// Obtener todos los doctores
export const getAll = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error al obtener doctores:', error);
    throw error;
  }
};

// Obtener un doctor por ID
export const getById = async (id) => {
  try {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener doctor ${id}:`, error);
    throw error;
  }
};

// Crear un nuevo doctor
export const create = async (doctor) => {
  try {
    const response = await axios.post(API_URL, doctor);
    return response.data;
  } catch (error) {
    console.error('Error al crear doctor:', error);
    throw error;
  }
};

// Actualizar un doctor existente
export const update = async (id, doctor) => {
  try {
    const response = await axios.put(`${API_URL}/${id}`, doctor);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar doctor ${id}:`, error);
    throw error;
  }
};

// Eliminar un doctor
export const remove = async (id) => {
  try {
    await axios.delete(`${API_URL}/${id}`);
    return true;
  } catch (error) {
    console.error(`Error al eliminar doctor ${id}:`, error);
    throw error;
  }
};

// Buscar doctores por nombre
export const search = async (query) => {
  try {
    const response = await axios.get(`${API_URL}/search`, {
      params: { query }
    });
    return response.data;
  } catch (error) {
    console.error(`Error al buscar doctores con query "${query}":`, error);
    throw error;
  }
};

// Buscar doctores por especialidad
export const getByEspecialidad = async (especialidad) => {
  try {
    const response = await axios.get(`${API_URL}/especialidad/${especialidad}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener doctores por especialidad "${especialidad}":`, error);
    throw error;
  }
}; 