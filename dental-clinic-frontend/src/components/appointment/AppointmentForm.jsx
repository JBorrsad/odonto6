import React, { useState, useEffect } from 'react';
import Button from '../common/Button';

function AppointmentForm({ appointment, patients, doctors, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    patientId: '',
    doctorId: '',
    start: '',
    time: '09:00',
    slots: 1, // Cantidad de bloques de 30 minutos
    status: 'PENDIENTE',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (appointment) {
      // Formatear fecha y hora para los inputs
      let startDate = '';
      let startTime = '';
      
      if (appointment.start) {
        const date = new Date(appointment.start);
        startDate = date.toISOString().split('T')[0];
        
        // Formatear hora como HH:MM
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        startTime = `${hours}:${minutes}`;
      }
      
      setForm({
        patientId: appointment.patientId || '',
        doctorId: appointment.doctorId || '',
        start: startDate,
        time: startTime,
        slots: appointment.durationSlots || 1,
        status: appointment.status || 'PENDIENTE',
      });
    }
  }, [appointment]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    // Convertir a número si es el campo 'slots'
    const processedValue = name === 'slots' ? parseInt(value, 10) : value;
    
    setForm({
      ...form,
      [name]: processedValue,
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
    
    if (!form.patientId) newErrors.patientId = 'Selecciona un paciente';
    if (!form.doctorId) newErrors.doctorId = 'Selecciona un doctor';
    if (!form.start) newErrors.start = 'Selecciona una fecha';
    if (!form.time) newErrors.time = 'Selecciona una hora';
    
    // Validar que la hora termine en :00 o :30
    if (form.time) {
      const minutes = form.time.split(':')[1];
      if (minutes !== '00' && minutes !== '30') {
        newErrors.time = 'La hora debe terminar en :00 o :30';
      }
    }
    
    // Validar el número de bloques
    if (form.slots < 1 || form.slots > 4) {
      newErrors.slots = 'La duración debe ser entre 1 y 4 bloques';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validate()) {
      // Combinar fecha y hora para crear DateTime
      const [year, month, day] = form.start.split('-');
      const [hours, minutes] = form.time.split(':');
      
      const startDateTime = new Date(
        parseInt(year, 10),
        parseInt(month, 10) - 1, // Month es zero-based
        parseInt(day, 10),
        parseInt(hours, 10),
        parseInt(minutes, 10)
      ).toISOString();
      
      const appointmentData = {
        patientId: form.patientId,
        doctorId: form.doctorId,
        start: startDateTime,
        durationSlots: form.slots,
        status: form.status,
      };
      
      onSubmit(appointmentData);
    }
  };

  // Lista de opciones para bloques de duración (30min a 2h)
  const slotOptions = [
    { value: 1, label: '30 minutos' },
    { value: 2, label: '1 hora' },
    { value: 3, label: '1 hora 30 minutos' },
    { value: 4, label: '2 horas' }
  ];

  // Lista de estados de cita
  const statusOptions = [
    { value: 'PENDIENTE', label: 'Pendiente' },
    { value: 'CONFIRMADA', label: 'Confirmada' },
    { value: 'EN_ESPERA', label: 'En sala de espera' },
    { value: 'EN_CURSO', label: 'En curso' },
    { value: 'COMPLETADA', label: 'Completada' },
    { value: 'CANCELADA', label: 'Cancelada' }
  ];

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700">Paciente</label>
        <select
          name="patientId"
          value={form.patientId}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.patientId ? 'border-red-300' : ''
          }`}
        >
          <option value="">Selecciona un paciente</option>
          {patients && patients.map(patient => (
            <option key={patient.id} value={patient.id}>
              {patient.nombre} {patient.apellidos}
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
          <option value="">Selecciona un doctor</option>
          {doctors && doctors.map(doctor => (
            <option key={doctor.id} value={doctor.id}>
              {doctor.nombreCompleto}
            </option>
          ))}
        </select>
        {errors.doctorId && <p className="mt-1 text-sm text-red-600">{errors.doctorId}</p>}
      </div>
      
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Fecha</label>
          <input
            type="date"
            name="start"
            value={form.start}
            onChange={handleChange}
            className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
              errors.start ? 'border-red-300' : ''
            }`}
          />
          {errors.start && <p className="mt-1 text-sm text-red-600">{errors.start}</p>}
        </div>
        
        <div>
          <label className="block text-sm font-medium text-gray-700">Hora</label>
          <input
            type="time"
            name="time"
            value={form.time}
            onChange={handleChange}
            step="1800" // Pasos de 30 minutos
            className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
              errors.time ? 'border-red-300' : ''
            }`}
          />
          {errors.time && <p className="mt-1 text-sm text-red-600">{errors.time}</p>}
        </div>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Duración</label>
        <select
          name="slots"
          value={form.slots}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.slots ? 'border-red-300' : ''
          }`}
        >
          {slotOptions.map(option => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
        {errors.slots && <p className="mt-1 text-sm text-red-600">{errors.slots}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Estado</label>
        <select
          name="status"
          value={form.status}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        >
          {statusOptions.map(option => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </div>
      
      <div className="flex justify-end space-x-2 pt-4">
        <Button onClick={onCancel} variant="secondary">
          Cancelar
        </Button>
        <Button type="submit" variant="primary">
          {appointment ? 'Actualizar' : 'Crear'} Cita
        </Button>
      </div>
    </form>
  );
}

export default AppointmentForm; 