import React, { useState } from 'react';
import * as appointmentService from '../../services/appointmentService';
import Button from '../common/Button';

function AppointmentDetail({ appointment, onClose, onUpdate }) {
  const [status, setStatus] = useState(appointment?.status || 'PENDING');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('es-ES', {
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric',
      hour: '2-digit', 
      minute: '2-digit'
    }).format(date);
  };

  const getDuration = (slots) => {
    if (!slots) return 'N/A';
    
    if (slots === 1) return '30 minutos';
    if (slots === 2) return '1 hora';
    if (slots === 3) return '1 hora 30 minutos';
    if (slots === 4) return '2 horas';
    if (slots === 5) return '2 horas 30 minutos';
    if (slots === 6) return '3 horas';
    
    return `${slots * 30} minutos`;
  };

  const getStatusLabel = (statusCode) => {
    const statusMap = {
      'PENDING': 'Sin confirmar',
      'CONFIRMED': 'Confirmada',
      'WAITING_ROOM': 'En sala de espera',
      'IN_PROGRESS': 'En curso',
      'COMPLETED': 'Completada',
      'CANCELLED': 'Cancelada'
    };
    
    return statusMap[statusCode] || statusCode;
  };

  const getStatusClass = (statusCode) => {
    const statusClasses = {
      'PENDING': 'bg-yellow-100 text-yellow-800',
      'CONFIRMED': 'bg-green-100 text-green-800',
      'WAITING_ROOM': 'bg-blue-100 text-blue-800',
      'IN_PROGRESS': 'bg-purple-100 text-purple-800',
      'COMPLETED': 'bg-gray-100 text-gray-800',
      'CANCELLED': 'bg-red-100 text-red-800'
    };
    
    return statusClasses[statusCode] || 'bg-gray-100 text-gray-800';
  };

  const handleStatusChange = async (e) => {
    const newStatus = e.target.value;
    setStatus(newStatus);
    
    try {
      setLoading(true);
      setError(null);
      
      await appointmentService.update(appointment.id, {
        ...appointment,
        status: newStatus
      });
      
      // Actualizar la cita en el componente padre
      if (onUpdate) {
        onUpdate({
          ...appointment,
          status: newStatus
        });
      }
    } catch (err) {
      setError('Error al actualizar el estado de la cita');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (!appointment) {
    return <div>No hay información de la cita</div>;
  }

  return (
    <div className="p-4">
      {error && (
        <div className="mb-4 p-3 bg-red-50 border-l-4 border-red-500 text-red-700">
          {error}
        </div>
      )}
      
      <h2 className="text-lg font-semibold mb-4">Detalles de la Cita</h2>
      
      <div className="grid grid-cols-2 gap-4 mb-6">
        <div>
          <p className="text-sm text-gray-500">Fecha y hora:</p>
          <p className="font-medium">{formatDate(appointment.start)}</p>
        </div>
        
        <div>
          <p className="text-sm text-gray-500">Duración:</p>
          <p className="font-medium">{getDuration(appointment.durationSlots)}</p>
        </div>
        
        <div>
          <p className="text-sm text-gray-500">Paciente:</p>
          <p className="font-medium">{appointment.patientName || 'N/A'}</p>
        </div>
        
        <div>
          <p className="text-sm text-gray-500">Doctor:</p>
          <p className="font-medium">{appointment.doctorName || 'N/A'}</p>
        </div>
        
        <div>
          <p className="text-sm text-gray-500">Estado:</p>
          <div className={`inline-block px-2 py-1 rounded text-xs ${getStatusClass(appointment.status)}`}>
            {getStatusLabel(appointment.status)}
          </div>
        </div>
      </div>
      
      <div className="mb-6">
        <label className="block text-sm font-medium text-gray-700 mb-1">Cambiar estado:</label>
        <select
          value={status}
          onChange={handleStatusChange}
          disabled={loading}
          className="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        >
          <option value="PENDING">Sin confirmar</option>
          <option value="CONFIRMED">Confirmada</option>
          <option value="WAITING_ROOM">En sala de espera</option>
          <option value="IN_PROGRESS">En curso</option>
          <option value="COMPLETED">Completada</option>
          <option value="CANCELLED">Cancelada</option>
        </select>
      </div>
      
      {appointment.notes && (
        <div className="mb-6">
          <p className="text-sm font-medium text-gray-700 mb-1">Notas:</p>
          <div className="p-3 bg-gray-50 rounded border">{appointment.notes}</div>
        </div>
      )}
      
      <div className="flex justify-end">
        <Button onClick={onClose} variant="primary">
          Cerrar
        </Button>
      </div>
    </div>
  );
}

export default AppointmentDetail; 