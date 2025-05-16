import React, { useState, useEffect } from 'react';
import Button from '../common/Button';

function PatientForm({ patient, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    nombre: '',
    apellidos: '',
    fechaNacimiento: '',
    sexo: 'MASCULINO',
    telefono: '',
    email: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (patient) {
      // Formato de fecha para el input date
      const fechaNacimiento = patient.fechaNacimiento 
        ? new Date(patient.fechaNacimiento).toISOString().split('T')[0]
        : '';
        
      setForm({
        nombre: patient.nombre || '',
        apellidos: patient.apellidos || '',
        fechaNacimiento,
        sexo: patient.sexo || 'MASCULINO',
        telefono: patient.telefono || '',
        email: patient.email || '',
      });
    }
  }, [patient]);

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
    
    if (!form.nombre) newErrors.nombre = 'El nombre es obligatorio';
    if (!form.apellidos) newErrors.apellidos = 'Los apellidos son obligatorios';
    if (!form.fechaNacimiento) newErrors.fechaNacimiento = 'La fecha de nacimiento es obligatoria';
    
    if (form.telefono && !/^\d{9}$/.test(form.telefono)) {
      newErrors.telefono = 'El teléfono debe tener 9 dígitos';
    }
    
    if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      newErrors.email = 'El email no es válido';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validate()) {
      // Convertir la fecha a objeto Date para backend
      const patientData = {
        ...form,
        fechaNacimiento: form.fechaNacimiento ? new Date(form.fechaNacimiento).toISOString() : null,
      };
      
      onSubmit(patientData);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700">Nombre</label>
        <input
          type="text"
          name="nombre"
          value={form.nombre}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.nombre ? 'border-red-300' : ''
          }`}
        />
        {errors.nombre && <p className="mt-1 text-sm text-red-600">{errors.nombre}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Apellidos</label>
        <input
          type="text"
          name="apellidos"
          value={form.apellidos}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.apellidos ? 'border-red-300' : ''
          }`}
        />
        {errors.apellidos && <p className="mt-1 text-sm text-red-600">{errors.apellidos}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Fecha de Nacimiento</label>
        <input
          type="date"
          name="fechaNacimiento"
          value={form.fechaNacimiento}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.fechaNacimiento ? 'border-red-300' : ''
          }`}
        />
        {errors.fechaNacimiento && <p className="mt-1 text-sm text-red-600">{errors.fechaNacimiento}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Sexo</label>
        <select
          name="sexo"
          value={form.sexo}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        >
          <option value="MASCULINO">Masculino</option>
          <option value="FEMENINO">Femenino</option>
        </select>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Teléfono</label>
        <input
          type="tel"
          name="telefono"
          value={form.telefono}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.telefono ? 'border-red-300' : ''
          }`}
        />
        {errors.telefono && <p className="mt-1 text-sm text-red-600">{errors.telefono}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Email</label>
        <input
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.email ? 'border-red-300' : ''
          }`}
        />
        {errors.email && <p className="mt-1 text-sm text-red-600">{errors.email}</p>}
      </div>
      
      <div className="flex justify-end space-x-2 pt-4">
        <Button onClick={onCancel} variant="secondary">
          Cancelar
        </Button>
        <Button type="submit" variant="primary">
          {patient ? 'Actualizar' : 'Crear'} Paciente
        </Button>
      </div>
    </form>
  );
}

export default PatientForm; 