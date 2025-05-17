import React from 'react';
import { Link } from 'react-router-dom';
import Card from '../common/Card';

function PatientCard({ patient }) {
  const getAge = (birthDate) => {
    if (!birthDate) return 'N/A';
    const birth = new Date(birthDate);
    const now = new Date();
    const diff = now.getTime() - birth.getTime();
    return Math.floor(diff / (1000 * 60 * 60 * 24 * 365.25));
  };
  
  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('es-ES').format(date);
  };
  
  return (
    <Card className="hover:shadow-md transition-shadow duration-200">
      <div className="flex justify-between items-start">
        <div>
          <h3 className="text-lg font-semibold">
            {patient.nombre} {patient.apellidos}
          </h3>
          <div className="mt-2 text-sm text-gray-600 space-y-1">
            <p>
              <span className="font-medium">Edad:</span> {getAge(patient.fechaNacimiento)} años
            </p>
            <p>
              <span className="font-medium">Fecha Nacimiento:</span> {formatDate(patient.fechaNacimiento)}
            </p>
            <p>
              <span className="font-medium">Teléfono:</span> {patient.telefono || 'No disponible'}
            </p>
          </div>
        </div>
        
        <Link 
          to={`/patients/${patient.id}`}
          className="px-3 py-1 bg-blue-500 text-white text-sm rounded hover:bg-blue-600"
        >
          Ver detalles
        </Link>
      </div>
    </Card>
  );
}

export default PatientCard; 