import axios from 'axios';
import * as patientService from './patientService';

const ODONTOGRAM_API_URL = '/api/odontograms';

// Función auxiliar para crear un objeto lesión
export const createLesionCommand = (toothId, face, lesionType) => {
  return {
    toothId,
    face,
    lesionType
  };
};

// Obtener el odontograma de un paciente
export const getOdontogram = async (patientId) => {
  try {
    return await patientService.getOdontogram(patientId);
  } catch (error) {
    console.error(`Error al obtener odontograma para paciente ${patientId}:`, error);
    throw error;
  }
};

// Obtener un odontograma por ID
export const getOdontogramById = async (odontogramId) => {
  try {
    const response = await axios.get(`${ODONTOGRAM_API_URL}/${odontogramId}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener odontograma ${odontogramId}:`, error);
    throw error;
  }
};

// Añadir una lesión usando los nuevos endpoints del backend
export const addLesion = async (odontogramId, toothNumber, face, lesionType) => {
  try {
    const response = await axios.post(
      `${ODONTOGRAM_API_URL}/${odontogramId}/teeth/${toothNumber}/faces/${face}/lesions`,
      null,
      { params: { lesionType } }
    );
    return response.data;
  } catch (error) {
    // Manejar errores específicos del backend
    if (error.response?.status === 404) {
      console.warn(`Odontograma ${odontogramId} no encontrado. Intentando crear uno nuevo...`);
      // Podrías implementar aquí lógica para crear el odontograma automáticamente
      throw new Error('El odontograma no existe. Por favor, recarga la página.');
    } else if (error.response?.status === 500) {
      console.error('Error interno del servidor al añadir lesión');
      throw new Error('Error del servidor al añadir la lesión. Inténtalo de nuevo.');
    }
    
    console.error(`Error al añadir lesión al odontograma ${odontogramId}:`, error);
    throw error;
  }
};

// Eliminar una lesión usando los nuevos endpoints del backend
export const removeLesion = async (odontogramId, toothNumber, face) => {
  try {
    const response = await axios.delete(
      `${ODONTOGRAM_API_URL}/${odontogramId}/teeth/${toothNumber}/faces/${face}/lesions`
    );
    return response.data;
  } catch (error) {
    // Manejar errores específicos del backend
    if (error.response?.status === 404) {
      console.warn(`Odontograma ${odontogramId} no encontrado o lesión no existe`);
      throw new Error('La lesión o el odontograma no existen.');
    } else if (error.response?.status === 500) {
      console.error('Error interno del servidor al eliminar lesión');
      throw new Error('Error del servidor al eliminar la lesión. Inténtalo de nuevo.');
    }
    
    console.error(`Error al eliminar lesión del odontograma ${odontogramId}:`, error);
    throw error;
  }
};

// Añadir un tratamiento
export const addTreatment = async (odontogramId, toothNumber, treatmentType) => {
  try {
    const response = await axios.post(
      `${ODONTOGRAM_API_URL}/${odontogramId}/teeth/${toothNumber}/treatments`,
      null,
      { params: { treatmentType } }
    );
    return response.data;
  } catch (error) {
    console.error(`Error al añadir tratamiento al odontograma ${odontogramId}:`, error);
    throw error;
  }
};

// Eliminar un tratamiento
export const removeTreatment = async (odontogramId, toothNumber) => {
  try {
    const response = await axios.delete(
      `${ODONTOGRAM_API_URL}/${odontogramId}/teeth/${toothNumber}/treatments`
    );
    return response.data;
  } catch (error) {
    console.error(`Error al eliminar tratamiento del odontograma ${odontogramId}:`, error);
    throw error;
  }
};

// Mapear el odontograma al formato esperado por el componente front-end
export const mapOdontogramToViewFormat = (odontogram) => {
  if (!odontogram) return null;
  
  const result = {
    dientes: {},
    esTemporal: false // Por defecto, asumimos adulto
  };
  
  // Detectar si es dentadura temporal basándonos en los IDs de los dientes
  const teethIds = Object.keys(odontogram.teeth || {});
  result.esTemporal = teethIds.some(id => 
    id.startsWith('5') || id.startsWith('6') || 
    id.startsWith('7') || id.startsWith('8')
  );
  
  // Transformar cada diente al formato esperado
  for (const [toothId, toothRecord] of Object.entries(odontogram.teeth || {})) {
    result.dientes[toothId] = {};
    
    // Transformar cada cara
    for (const [face, lesionType] of Object.entries(toothRecord.faces || {})) {
      result.dientes[toothId][face] = lesionType;
    }
  }
  
  return result;
};

// ================ FUNCIONES DE DIAGNÓSTICO ================

// Función para probar el endpoint directo de odontogramas
export const testOdontogramEndpoint = async (patientId) => {
  console.log(`🔍 Probando endpoints de odontograma para paciente: ${patientId}`);
  
  const results = {
    patientEndpoint: null,
    directEndpoint: null,
    errors: []
  };
  
  // Probar endpoint /api/patients/{id}/odontogram
  try {
    console.log(`📞 Llamando a /api/patients/${patientId}/odontogram`);
    const response = await axios.get(`/api/patients/${patientId}/odontogram`);
    results.patientEndpoint = {
      status: response.status,
      data: response.data,
      dataType: typeof response.data,
      isArray: Array.isArray(response.data),
      hasTeeth: response.data && response.data.teeth
    };
    console.log(`✅ Endpoint paciente exitoso:`, results.patientEndpoint);
  } catch (error) {
    results.errors.push({
      endpoint: 'patient',
      status: error.response?.status,
      message: error.message,
      data: error.response?.data
    });
    console.log(`❌ Error en endpoint paciente:`, error.response?.status, error.message);
  }
  
  // Probar endpoint /api/odontograms/{id} usando el ID derivado
  try {
    const odontogramId = `odontogram_${patientId}`;
    console.log(`📞 Llamando a /api/odontograms/${odontogramId}`);
    const response = await axios.get(`/api/odontograms/${odontogramId}`);
    results.directEndpoint = {
      status: response.status,
      data: response.data,
      dataType: typeof response.data,
      isArray: Array.isArray(response.data),
      hasTeeth: response.data && response.data.teeth
    };
    console.log(`✅ Endpoint directo exitoso:`, results.directEndpoint);
  } catch (error) {
    results.errors.push({
      endpoint: 'direct',
      status: error.response?.status,
      message: error.message,
      data: error.response?.data
    });
    console.log(`❌ Error en endpoint directo:`, error.response?.status, error.message);
  }
  
  return results;
};

// Función para probar con múltiples pacientes
export const testMultiplePatients = async (patientIds = ['1', '2', '3']) => {
  console.log(`🔍 Probando odontogramas para múltiples pacientes:`, patientIds);
  
  const results = [];
  
  for (const patientId of patientIds) {
    const result = await testOdontogramEndpoint(patientId);
    results.push({
      patientId,
      ...result
    });
  }
  
  // Resumen
  const summary = {
    totalTested: results.length,
    successful: results.filter(r => r.patientEndpoint || r.directEndpoint).length,
    withErrors: results.filter(r => r.errors.length > 0).length,
    details: results
  };
  
  console.log(`📊 Resumen de pruebas:`, summary);
  return summary;
}; 