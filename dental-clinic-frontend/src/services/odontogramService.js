import * as patientService from './patientService';

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

// Añadir una lesión
export const addLesion = async (patientId, toothId, face, lesionType) => {
  try {
    const cmd = [createLesionCommand(toothId, face, lesionType)];
    return await patientService.addLesions(patientId, cmd);
  } catch (error) {
    console.error(`Error al añadir lesión al paciente ${patientId}:`, error);
    throw error;
  }
};

// Añadir múltiples lesiones
export const addLesions = async (patientId, lesions) => {
  try {
    return await patientService.addLesions(patientId, lesions);
  } catch (error) {
    console.error(`Error al añadir lesiones al paciente ${patientId}:`, error);
    throw error;
  }
};

// Eliminar una lesión
export const removeLesion = async (patientId, toothId, face) => {
  try {
    const cmd = [createLesionCommand(toothId, face, null)];
    return await patientService.removeLesions(patientId, cmd);
  } catch (error) {
    console.error(`Error al eliminar lesión del paciente ${patientId}:`, error);
    throw error;
  }
};

// Eliminar múltiples lesiones
export const removeLesions = async (patientId, lesions) => {
  try {
    return await patientService.removeLesions(patientId, lesions);
  } catch (error) {
    console.error(`Error al eliminar lesiones del paciente ${patientId}:`, error);
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
  const teethIds = Object.keys(odontogram.teeth);
  result.esTemporal = teethIds.some(id => 
    id.startsWith('5') || id.startsWith('6') || 
    id.startsWith('7') || id.startsWith('8')
  );
  
  // Transformar cada diente al formato esperado
  for (const [toothId, toothRecord] of Object.entries(odontogram.teeth)) {
    result.dientes[toothId] = {};
    
    // Transformar cada cara
    for (const [face, lesionType] of Object.entries(toothRecord.faces || {})) {
      result.dientes[toothId][face] = lesionType;
    }
  }
  
  return result;
}; 