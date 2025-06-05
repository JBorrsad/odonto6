import React, { useState, useEffect } from 'react';
import { getPatients, getDoctors } from '../../services/api';
import Button from '../common/Button';

function AppointmentForm({ appointment, onSubmit, onCancel, patientId, loading = false }) {
  const [patients, setPatients] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [loadingData, setLoadingData] = useState(true);
  const [error, setError] = useState(null);
  
  const [form, setForm] = useState({
    patientId: patientId || '',
    doctorId: '',
    date: '',
    time: '08:00',
    durationSlots: 1,
    status: 'PENDING',
    notes: ''
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoadingData(true);
        const [patientsData, doctorsData] = await Promise.all([
          getPatients(),
          getDoctors()
        ]);
        
        setPatients(patientsData);
        setDoctors(doctorsData);
      } catch (err) {
        setError(err.message || 'Error al cargar datos');
      } finally {
        setLoadingData(false);
      }
    };
    
    fetchData();
  }, []);

  useEffect(() => {
    if (appointment) {
      // Convertir la fecha ISO a componentes locales
      const startDate = new Date(appointment.start);
      const dateStr = startDate.toISOString().split('T')[0];
      const timeStr = startDate.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
      
      setForm({
        patientId: appointment.patientId || '',
        doctorId: appointment.doctorId || '',
        date: dateStr,
        time: timeStr,
        durationSlots: appointment.durationSlots || 1,
        status: appointment.status || 'PENDING',
        notes: appointment.notes || ''
      });
    } else if (patientId) {
      setForm(prev => ({
        ...prev,
        patientId
      }));
    }
  }, [appointment, patientId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]: value,
    });
    
    // Limpiar error cuando el usuario comienza a corregir
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: null,
      });
    }
  };

  const validate = () => {
    const newErrors = {};
    
    if (!form.patientId) newErrors.patientId = 'El paciente es obligatorio';
    if (!form.doctorId) newErrors.doctorId = 'El doctor es obligatorio';
    if (!form.date) newErrors.date = 'La fecha es obligatoria';
    if (!form.time) newErrors.time = 'La hora es obligatoria';
    if (!form.durationSlots) newErrors.durationSlots = 'La duración es obligatoria';
    
    // Validar que la hora sea en punto o media hora
    const minutes = parseInt(form.time.split(':')[1], 10);
    if (minutes !== 0 && minutes !== 30) {
      newErrors.time = 'La hora debe ser en punto (:00) o media hora (:30)';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validate()) {
      // Crear objeto Date combinando fecha y hora
      const [hours, minutes] = form.time.split(':').map(n => parseInt(n, 10));
      const start = new Date(form.date);
      start.setHours(hours, minutes, 0, 0);
      
      // Calcular fecha de fin basada en la duración
      const end = new Date(start);
      end.setMinutes(end.getMinutes() + (parseInt(form.durationSlots, 10) * 30));
      
      const appointmentData = {
        id: appointment?.id, // Para actualizaciones
        patientId: form.patientId,
        doctorId: form.doctorId,
        start: start.toISOString(),
        end: end.toISOString(),
        durationSlots: parseInt(form.durationSlots, 10),
        status: form.status,
        notes: form.notes
      };
      
      onSubmit(appointmentData);
    }
  };

  if (loadingData) {
    return (
      <div className="text-center py-4">
        <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-gray-900"></div>
        <p className="mt-2">Cargando datos...</p>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {error && (
        <div className="bg-red-50 border-l-4 border-red-500 p-3 text-red-700 text-sm">
          {error}
        </div>
      )}
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Paciente</label>
        <select
          name="patientId"
          value={form.patientId}
          onChange={handleChange}
          disabled={!!patientId}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.patientId ? 'border-red-300' : ''
          }`}
        >
          <option value="">Seleccionar paciente</option>
          {patients.map(patient => (
            <option key={patient.id} value={patient.id}>
              {patient.nombre} {patient.apellido}
            </option>
          ))}
        </select>
        {errors.patientId && <p className="mt-1 text-sm text-red-600">{errors.patientId}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Doctor</label>
        <select
          name="doctorId"
          value={form.doctorId}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.doctorId ? 'border-red-300' : ''
          }`}
        >
          <option value="">Seleccionar doctor</option>
          {doctors.map(doctor => (
            <option key={doctor.id} value={doctor.id}>
              Dr. {doctor.nombreCompleto}
            </option>
          ))}
        </select>
        {errors.doctorId && <p className="mt-1 text-sm text-red-600">{errors.doctorId}</p>}
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Fecha</label>
          <input
            type="date"
            name="date"
            value={form.date}
            onChange={handleChange}
            min={new Date().toISOString().split('T')[0]}
            className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
              errors.date ? 'border-red-300' : ''
            }`}
          />
          {errors.date && <p className="mt-1 text-sm text-red-600">{errors.date}</p>}
        </div>
        
        <div>
          <label className="block text-sm font-medium text-gray-700">Hora</label>
          <select
            name="time"
            value={form.time}
            onChange={handleChange}
            className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
              errors.time ? 'border-red-300' : ''
            }`}
          >
            {[...Array(24)].map((_, hour) => [0, 30].map(minute => {
              const timeValue = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
              // Mostrar horas de 8:00 a 20:00
              if (hour >= 8 && hour < 20) {
                return (
                  <option key={timeValue} value={timeValue}>
                    {timeValue}
                  </option>
                );
              }
              return null;
            })).flat().filter(Boolean)}
          </select>
          {errors.time && <p className="mt-1 text-sm text-red-600">{errors.time}</p>}
        </div>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Duración (bloques de 30 min)</label>
        <select
          name="durationSlots"
          value={form.durationSlots}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.durationSlots ? 'border-red-300' : ''
          }`}
        >
          <option value="1">30 minutos</option>
          <option value="2">1 hora</option>
          <option value="3">1 hora 30 minutos</option>
          <option value="4">2 horas</option>
          <option value="5">2 horas 30 minutos</option>
          <option value="6">3 horas</option>
        </select>
        {errors.durationSlots && <p className="mt-1 text-sm text-red-600">{errors.durationSlots}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Estado</label>
        <select
          name="status"
          value={form.status}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        >
          <option value="PENDING">Sin confirmar</option>
          <option value="CONFIRMED">Confirmada</option>
          <option value="WAITING_ROOM">En sala de espera</option>
          <option value="IN_PROGRESS">En curso</option>
          <option value="COMPLETED">Completada</option>
          <option value="CANCELLED">Cancelada</option>
        </select>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Notas</label>
        <textarea
          name="notes"
          value={form.notes}
          onChange={handleChange}
          rows="3"
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        ></textarea>
      </div>
      
      <div className="flex justify-end space-x-2 pt-4">
        <Button 
          onClick={onCancel} 
          variant="secondary"
          disabled={loading}
        >
          Cancelar
        </Button>
        <Button 
          type="submit" 
          variant="primary"
          loading={loading}
        >
          {appointment ? 'Actualizar' : 'Crear'} Cita
        </Button>
      </div>
    </form>
  );
}

export default AppointmentForm; 