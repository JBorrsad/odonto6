import React, { useState, useEffect } from 'react';
import { addLesions, removeLesions } from '../../services/api';

function Odontogram({ patientId, data, isChild = false, isEditable = false, onUpdate }) {
  const [selectedLesion, setSelectedLesion] = useState('CARIES');
  const [loading, setLoading] = useState(false);
  const [toothData, setToothData] = useState({});
  
  useEffect(() => {
    if (data) {
      setToothData(data);
    }
  }, [data]);
  
  if (!toothData) {
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
    { value: 'TREATMENT', label: 'Tratamiento', color: 'bg-blue-500' },
    { value: 'ABSENT', label: 'Ausente', color: 'bg-gray-500' },
    { value: 'CROWN', label: 'Corona', color: 'bg-yellow-500' },
    { value: 'ROOT_CANAL', label: 'Endodoncia', color: 'bg-purple-500' }
  ];

  // Manejar clic en una cara del diente
  const handleFaceClick = async (toothId, face) => {
    if (!isEditable || loading) return;
    
    const tooth = toothData[toothId] || {};
    const faceData = tooth.faces || {};
    const currentLesion = faceData[face];
    
    setLoading(true);
    
    try {
      if (currentLesion) {
        // Remover lesión
        await removeLesions(patientId, [{ toothId, face }]);
        
        // Actualizar estado local
        const updatedToothData = { ...toothData };
        if (updatedToothData[toothId]?.faces) {
          delete updatedToothData[toothId].faces[face];
        }
        setToothData(updatedToothData);
      } else {
        // Añadir lesión
        await addLesions(patientId, [{ toothId, face, lesion: selectedLesion }]);
        
        // Actualizar estado local
        const updatedToothData = { ...toothData };
        if (!updatedToothData[toothId]) {
          updatedToothData[toothId] = { faces: {} };
        }
        if (!updatedToothData[toothId].faces) {
          updatedToothData[toothId].faces = {};
        }
        updatedToothData[toothId].faces[face] = selectedLesion;
        setToothData(updatedToothData);
      }
      
      // Notificar al componente padre si es necesario
      if (onUpdate) onUpdate(toothData);
      
    } catch (error) {
      console.error('Error al actualizar odontograma:', error);
    } finally {
      setLoading(false);
    }
  };

  // Obtener color para una cara basado en el tipo de lesión
  const getFaceColor = (toothId, face) => {
    const tooth = toothData[toothId] || {};
    const faceData = tooth.faces || {};
    const lesion = faceData[face];
    
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