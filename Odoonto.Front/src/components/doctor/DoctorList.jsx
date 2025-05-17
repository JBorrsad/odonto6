import React from 'react';
import Card from '../common/Card';
import Button from '../common/Button';

function DoctorList({ doctors, onEdit, onDelete, onAdd }) {
  if (!doctors || doctors.length === 0) {
    return (
      <div className="text-center py-10">
        <p className="text-gray-500 mb-4">No hay doctores registrados</p>
        <Button onClick={onAdd} variant="primary">
          Añadir Doctor
        </Button>
      </div>
    );
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold">Listado de Doctores</h2>
        <Button onClick={onAdd} variant="primary">
          Añadir Doctor
        </Button>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {doctors.map(doctor => (
          <Card 
            key={doctor.id} 
            className="hover:shadow-md transition-shadow"
          >
            <div className="flex flex-col h-full">
              <div className="flex-grow">
                <h3 className="font-semibold text-lg">{doctor.nombreCompleto}</h3>
                <p className="text-gray-600 mt-1">{doctor.especialidad}</p>
              </div>
              
              <div className="flex justify-end space-x-2 mt-4">
                <Button 
                  onClick={() => onEdit(doctor)} 
                  variant="outline"
                  className="text-sm px-3 py-1"
                >
                  Editar
                </Button>
                <Button 
                  onClick={() => onDelete(doctor.id)} 
                  variant="danger"
                  className="text-sm px-3 py-1"
                >
                  Eliminar
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}

export default DoctorList; 