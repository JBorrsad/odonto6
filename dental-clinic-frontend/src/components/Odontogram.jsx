import React, { useState } from 'react';

// Componente que representa un diente individual
const Tooth = ({ number, data, onClick, isChild }) => {
  // Determinar estado de cada cara del diente
  const faceStatus = {
    top: data?.caras?.oclusal || data?.caras?.superior || 'normal',
    bottom: data?.caras?.inferior || 'normal',
    left: data?.caras?.mesial || 'normal',
    right: data?.caras?.distal || 'normal',
    center: data?.caras?.vestibular || data?.caras?.lingual || data?.caras?.palatino || 'normal',
  };

  // Colores basados en el estado
  const getColor = (status) => {
    switch (status) {
      case 'caries':
        return '#e74c3c'; // Rojo para caries
      case 'tratamiento':
        return '#3498db'; // Azul para tratamientos pendientes
      case 'obturado':
        return '#2ecc71'; // Verde para tratamientos realizados
      case 'ausente':
        return '#7f8c8d'; // Gris para dientes ausentes
      case 'corona':
        return '#f1c40f'; // Amarillo para coronas
      default:
        return '#ecf0f1'; // Blanco por defecto
    }
  };

  const toothStatus = data?.estado || 'normal';
  const isAbsent = toothStatus === 'ausente';

  return (
    <div className="relative" style={{ width: '60px', height: '80px', margin: '0 2px' }}>
      <div className="text-center text-xs mb-1">{number}</div>
      
      <div 
        className="relative" 
        style={{ 
          width: '100%',
          height: '60px',
          border: '1px solid #ccc',
          borderRadius: isChild ? '20%' : '0',
          backgroundColor: isAbsent ? getColor('ausente') : '#fff',
          opacity: isAbsent ? 0.5 : 1,
          cursor: 'pointer'
        }}
        onClick={() => onClick(number)}
      >
        {/* Cara superior/oclusal */}
        <div 
          style={{ 
            position: 'absolute', 
            top: '0', 
            left: '30%', 
            width: '40%', 
            height: '20%',
            backgroundColor: getColor(faceStatus.top),
            cursor: 'pointer'
          }}
          onClick={(e) => { e.stopPropagation(); onClick(number, 'oclusal'); }}
        />

        {/* Cara izquierda/mesial */}
        <div 
          style={{ 
            position: 'absolute', 
            top: '20%', 
            left: '0', 
            width: '30%', 
            height: '60%',
            backgroundColor: getColor(faceStatus.left),
            cursor: 'pointer'
          }}
          onClick={(e) => { e.stopPropagation(); onClick(number, 'mesial'); }}
        />

        {/* Cara central/vestibular */}
        <div 
          style={{ 
            position: 'absolute', 
            top: '20%', 
            left: '30%', 
            width: '40%', 
            height: '60%',
            backgroundColor: getColor(faceStatus.center),
            cursor: 'pointer'
          }}
          onClick={(e) => { e.stopPropagation(); onClick(number, 'vestibular'); }}
        />

        {/* Cara derecha/distal */}
        <div 
          style={{ 
            position: 'absolute', 
            top: '20%', 
            right: '0', 
            width: '30%', 
            height: '60%',
            backgroundColor: getColor(faceStatus.right),
            cursor: 'pointer'
          }}
          onClick={(e) => { e.stopPropagation(); onClick(number, 'distal'); }}
        />

        {/* Cara inferior */}
        <div 
          style={{ 
            position: 'absolute', 
            bottom: '0', 
            left: '30%', 
            width: '40%', 
            height: '20%',
            backgroundColor: getColor(faceStatus.bottom),
            cursor: 'pointer'
          }}
          onClick={(e) => { e.stopPropagation(); onClick(number, 'inferior'); }}
        />
      </div>
    </div>
  );
};

function Odontogram({ data = {}, isChild = false, isEditable = false, onUpdate = () => {} }) {
  const [selectedTooth, setSelectedTooth] = useState(null);
  const [selectedFace, setSelectedFace] = useState(null);
  
  // Definir los grupos de dientes según sean adultos o niños
  const adultTeeth = {
    upperRight: [18, 17, 16, 15, 14, 13, 12, 11],
    upperLeft: [21, 22, 23, 24, 25, 26, 27, 28],
    lowerLeft: [31, 32, 33, 34, 35, 36, 37, 38],
    lowerRight: [48, 47, 46, 45, 44, 43, 42, 41]
  };
  
  const childTeeth = {
    upperRight: [55, 54, 53, 52, 51],
    upperLeft: [61, 62, 63, 64, 65],
    lowerLeft: [71, 72, 73, 74, 75],
    lowerRight: [85, 84, 83, 82, 81]
  };
  
  const teeth = isChild ? childTeeth : adultTeeth;
  
  const handleToothClick = (number, face = null) => {
    if (!isEditable) return;
    
    setSelectedTooth(number);
    setSelectedFace(face);
    
    // Aquí se podría abrir un modal para editar el estado del diente/cara
    console.log(`Tooth ${number}, Face: ${face || 'all'}`);
  };
  
  const getLegendItem = (status, label, color) => (
    <div className="flex items-center mr-4 mb-2">
      <div className="w-4 h-4 mr-1" style={{ backgroundColor: color }}></div>
      <span className="text-xs">{label}</span>
    </div>
  );

  return (
    <div className="odontogram-container">
      {/* Leyenda */}
      <div className="flex flex-wrap mb-4 p-2 border-b">
        {getLegendItem('caries', 'Caries', '#e74c3c')}
        {getLegendItem('tratamiento', 'Tratamiento pendiente', '#3498db')}
        {getLegendItem('obturado', 'Obturado', '#2ecc71')}
        {getLegendItem('ausente', 'Ausente', '#7f8c8d')}
        {getLegendItem('corona', 'Corona', '#f1c40f')}
      </div>
      
      <div className="mb-6">
        <div className="flex justify-center mb-4">
          {teeth.upperRight.map(number => (
            <Tooth 
              key={number} 
              number={number} 
              data={data[number]} 
              onClick={handleToothClick}
              isChild={isChild}
            />
          ))}
          {teeth.upperLeft.map(number => (
            <Tooth 
              key={number} 
              number={number} 
              data={data[number]} 
              onClick={handleToothClick}
              isChild={isChild}
            />
          ))}
        </div>
        
        <div className="flex justify-center">
          {teeth.lowerRight.map(number => (
            <Tooth 
              key={number} 
              number={number} 
              data={data[number]} 
              onClick={handleToothClick}
              isChild={isChild}
            />
          )).reverse()}
          {teeth.lowerLeft.map(number => (
            <Tooth 
              key={number} 
              number={number} 
              data={data[number]} 
              onClick={handleToothClick}
              isChild={isChild}
            />
          )).reverse()}
        </div>
      </div>
      
      {isEditable && selectedTooth && (
        <div className="p-3 border-t">
          <div className="font-medium">Diente {selectedTooth}{selectedFace ? `, Cara: ${selectedFace}` : ''}</div>
          <div className="text-sm text-gray-500">Seleccione una acción para aplicar</div>
          {/* Aquí irían los controles para cambiar el estado del diente o cara seleccionada */}
        </div>
      )}
    </div>
  );
}

export default Odontogram;