import React, { useState, useEffect } from 'react';
import * as odontogramService from '../../services/odontogramService';

function Odontogram({ patientId, data, isChild = false, isEditable = false, onUpdate }) {
  const [selectedLesion, setSelectedLesion] = useState('CARIES');
  const [loading, setLoading] = useState(false);
  const [toothData, setToothData] = useState({});
  const [odontogramId, setOdontogramId] = useState(null);
  
  useEffect(() => {
    if (data) {
      // Si ya tenemos datos del odontograma, usarlos
      if (data.teeth) {
        // Backend format: { teeth: { "11": { faces: { "VESTIBULAR": "CARIES" } } } }
        const formattedData = {};
        Object.keys(data.teeth).forEach(toothId => {
          if (data.teeth[toothId].faces) {
            formattedData[toothId] = data.teeth[toothId].faces;
          }
        });
        setToothData(formattedData);
      } else if (data.dientes) {
        // Frontend format: { dientes: { "11": { "VESTIBULAR": "CARIES" } } }
        setToothData(data.dientes);
      }
      setOdontogramId(data.id || data.odontogramId);
    } else {
      // Si no hay data, inicializamos con un objeto vacío
      setToothData({});
      // Generar ID de odontograma basado en el ID del paciente según la arquitectura del backend
      if (patientId) {
        setOdontogramId(`odontogram_${patientId}`);
      }
    }
  }, [data, patientId]);
  
  if (!patientId) {
    return <div className="p-4 border rounded bg-red-50 text-red-600">Error: ID de paciente requerido.</div>;
  }
  
  if (!data && !isEditable) {
    return <div className="p-4 border rounded bg-gray-50">No hay datos de odontograma disponibles.</div>;
  }
  
  // Configuración de dientes basada en si es temporal (niño) o permanente (adulto)
  const teethIds = isChild 
    ? ['55','54','53','52','51', '61','62','63','64','65', '85','84','83','82','81', '71','72','73','74','75'] // Dientes temporales
    : ['18','17','16','15','14','13','12','11', '21','22','23','24','25','26','27','28', '48','47','46','45','44','43','42','41', '31','32','33','34','35','36','37','38']; // Dientes permanentes
  
  // Caras de los dientes
  const faces = ['VESTIBULAR', 'PALATINO', 'MESIAL', 'DISTAL', 'OCLUSAL'];
  
  // Tipos de lesiones para seleccionar
  const lesionTypes = [
    { value: 'CARIES', label: 'Caries', color: 'bg-red-500' },
    { value: 'OBTURACION', label: 'Obturación', color: 'bg-green-500' },
    { value: 'CORONA', label: 'Corona', color: 'bg-yellow-500' },
    { value: 'AUSENTE', label: 'Ausente', color: 'bg-gray-500' },
    { value: 'ENDODONCIA', label: 'Endodoncia', color: 'bg-purple-500' }
  ];

  // Manejar clic en una cara del diente
  const handleFaceClick = async (toothId, face) => {
    if (!isEditable || loading || !odontogramId) return;
    
    const tooth = toothData[toothId] || {};
    const currentLesion = tooth[face];
    
    setLoading(true);
    
    try {
      if (currentLesion) {
        // Remover lesión usando el nuevo servicio
        const updatedOdontogram = await odontogramService.removeLesion(odontogramId, parseInt(toothId), face);
        
        // Actualizar estado local desde la respuesta del backend
        if (updatedOdontogram && updatedOdontogram.teeth) {
          const formattedData = {};
          Object.keys(updatedOdontogram.teeth).forEach(tId => {
            if (updatedOdontogram.teeth[tId].faces) {
              formattedData[tId] = updatedOdontogram.teeth[tId].faces;
            }
          });
          setToothData(formattedData);
        } else {
          // Fallback: actualizar estado local manualmente
          const updatedToothData = { ...toothData };
          if (updatedToothData[toothId]) {
            delete updatedToothData[toothId][face];
            if (Object.keys(updatedToothData[toothId]).length === 0) {
              delete updatedToothData[toothId];
            }
          }
          setToothData(updatedToothData);
        }
      } else {
        // Añadir lesión usando el nuevo servicio
        const updatedOdontogram = await odontogramService.addLesion(odontogramId, parseInt(toothId), face, selectedLesion);
        
        // Actualizar estado local desde la respuesta del backend
        if (updatedOdontogram && updatedOdontogram.teeth) {
          const formattedData = {};
          Object.keys(updatedOdontogram.teeth).forEach(tId => {
            if (updatedOdontogram.teeth[tId].faces) {
              formattedData[tId] = updatedOdontogram.teeth[tId].faces;
            }
          });
          setToothData(formattedData);
        } else {
          // Fallback: actualizar estado local manualmente
          const updatedToothData = { ...toothData };
          if (!updatedToothData[toothId]) {
            updatedToothData[toothId] = {};
          }
          updatedToothData[toothId][face] = selectedLesion;
          setToothData(updatedToothData);
        }
      }
      
      // Notificar al componente padre si es necesario
      if (onUpdate) {
        onUpdate({
          id: odontogramId,
          teeth: toothData
        });
      }
      
    } catch (error) {
      console.error('Error al actualizar odontograma:', error);
      
      // Mostrar mensaje de error específico al usuario
      let errorMessage = 'Error al actualizar el odontograma.';
      
      if (error.message) {
        errorMessage = error.message;
      } else if (error.response?.status === 404) {
        errorMessage = 'El odontograma no existe. Por favor, recarga la página.';
      } else if (error.response?.status === 500) {
        errorMessage = 'Error del servidor. Por favor, inténtalo de nuevo.';
      }
      
      // Mostrar el error en una notificación más amigable
      if (window.confirm(`${errorMessage}\n\n¿Deseas recargar la página para intentar solucionarlo?`)) {
        window.location.reload();
      }
    } finally {
      setLoading(false);
    }
  };

  // Obtener color para una cara basado en el tipo de lesión
  const getFaceColor = (toothId, face) => {
    const tooth = toothData[toothId] || {};
    const lesion = tooth[face];
    
    if (!lesion) return 'bg-white';
    
    const lesionObj = lesionTypes.find(l => l.value === lesion);
    return lesionObj ? lesionObj.color : 'bg-gray-200';
  };
  
  return (
    <div className="odontogram-container">
      {isEditable && (
        <div className="mb-4 p-3 border rounded bg-gray-50">
          <h4 className="text-sm font-medium mb-2">Selecciona el tipo de lesión:</h4>
          <div className="grid grid-cols-5 gap-2">
            {lesionTypes.map(type => (
              <button
                key={type.value}
                onClick={() => setSelectedLesion(type.value)}
                className={`p-2 rounded text-xs ${
                  selectedLesion === type.value 
                    ? `${type.color} text-white ring-2 ring-offset-2 ring-blue-500` 
                    : 'bg-white border'
                }`}
                disabled={loading}
              >
                {type.label}
              </button>
            ))}
          </div>
        </div>
      )}
      
      {loading && (
        <div className="absolute inset-0 bg-white bg-opacity-70 flex items-center justify-center z-10">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>
      )}
      
      <div className="odontogram border rounded p-3 overflow-x-auto relative">
        {/* Cuadrantes superiores */}
        <div className="flex justify-center mb-6">
          {teethIds.slice(0, teethIds.length / 2).map(toothId => (
            <div key={toothId} className="tooth-container mx-1 text-center">
              <div className="tooth-number text-xs font-semibold mb-1">{toothId}</div>
              <div className="tooth-graphic w-10 h-14 relative">
                {/* Cara oclusal (central) */}
                <div 
                  className={`absolute inset-x-2 inset-y-4 ${getFaceColor(toothId, 'OCLUSAL')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'OCLUSAL')}
                ></div>
                
                {/* Cara vestibular (superior) */}
                <div 
                  className={`absolute inset-x-2 top-0 h-4 ${getFaceColor(toothId, 'VESTIBULAR')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'VESTIBULAR')}
                ></div>
                
                {/* Cara palatina (inferior) */}
                <div 
                  className={`absolute inset-x-2 bottom-0 h-4 ${getFaceColor(toothId, 'PALATINO')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'PALATINO')}
                ></div>
                
                {/* Cara mesial (izquierda) */}
                <div 
                  className={`absolute left-0 inset-y-0 w-2 ${getFaceColor(toothId, 'MESIAL')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'MESIAL')}
                ></div>
                
                {/* Cara distal (derecha) */}
                <div 
                  className={`absolute right-0 inset-y-0 w-2 ${getFaceColor(toothId, 'DISTAL')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'DISTAL')}
                ></div>
              </div>
            </div>
          ))}
        </div>
        
        {/* Línea divisoria */}
        <div className="border-t border-gray-300 my-4"></div>
        
        {/* Cuadrantes inferiores */}
        <div className="flex justify-center">
          {teethIds.slice(teethIds.length / 2).map(toothId => (
            <div key={toothId} className="tooth-container mx-1 text-center">
              <div className="tooth-graphic w-10 h-14 relative">
                {/* Cara oclusal (central) */}
                <div 
                  className={`absolute inset-x-2 inset-y-4 ${getFaceColor(toothId, 'OCLUSAL')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'OCLUSAL')}
                ></div>
                
                {/* Cara vestibular (inferior) */}
                <div 
                  className={`absolute inset-x-2 bottom-0 h-4 ${getFaceColor(toothId, 'VESTIBULAR')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'VESTIBULAR')}
                ></div>
                
                {/* Cara palatina (superior) */}
                <div 
                  className={`absolute inset-x-2 top-0 h-4 ${getFaceColor(toothId, 'PALATINO')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'PALATINO')}
                ></div>
                
                {/* Cara mesial (izquierda) */}
                <div 
                  className={`absolute left-0 inset-y-0 w-2 ${getFaceColor(toothId, 'MESIAL')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'MESIAL')}
                ></div>
                
                {/* Cara distal (derecha) */}
                <div 
                  className={`absolute right-0 inset-y-0 w-2 ${getFaceColor(toothId, 'DISTAL')} border ${isEditable ? 'cursor-pointer' : ''}`}
                  onClick={() => handleFaceClick(toothId, 'DISTAL')}
                ></div>
              </div>
              <div className="tooth-number text-xs font-semibold mt-1">{toothId}</div>
            </div>
          ))}
        </div>
      </div>
      
      {/* Leyenda */}
      <div className="mt-4 p-2 bg-gray-50 rounded border">
        <h4 className="text-sm font-medium mb-2">Leyenda:</h4>
        <div className="grid grid-cols-3 gap-2 text-xs">
          {lesionTypes.map(type => (
            <div key={type.value} className="flex items-center">
              <div className={`w-3 h-3 ${type.color} mr-1 rounded-sm`}></div>
              <span>{type.label}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default Odontogram; 