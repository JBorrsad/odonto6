import React, { useState, useEffect } from 'react';
import Button from '../common/Button';

function DoctorForm({ doctor, onSubmit, onCancel, loading = false }) {
  const [form, setForm] = useState({
    nombreCompleto: '',
    especialidad: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (doctor) {
      setForm({
        nombreCompleto: doctor.nombreCompleto || '',
        especialidad: doctor.especialidad || '',
      });
    }
  }, [doctor]);

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
    
    if (!form.nombreCompleto) newErrors.nombreCompleto = 'El nombre completo es obligatorio';
    if (!form.especialidad) newErrors.especialidad = 'La especialidad es obligatoria';
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validate()) {
      onSubmit(form);
    }
  };

  // Lista de especialidades dentales comunes
  const especialidades = [
    'Odontología General',
    'Endodoncia',
    'Ortodoncia',
    'Periodoncia',
    'Odontopediatría',
    'Cirugía Oral',
    'Implantología',
    'Prostodoncia',
    'Estética Dental',
    'Radiología Oral'
  ];

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700">Nombre Completo</label>
        <input
          type="text"
          name="nombreCompleto"
          value={form.nombreCompleto}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.nombreCompleto ? 'border-red-300' : ''
          }`}
        />
        {errors.nombreCompleto && <p className="mt-1 text-sm text-red-600">{errors.nombreCompleto}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Especialidad</label>
        <input
          type="text"
          name="especialidad"
          value={form.especialidad}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.especialidad ? 'border-red-300' : ''
          }`}
        />
        {errors.especialidad && <p className="mt-1 text-sm text-red-600">{errors.especialidad}</p>}
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
          {doctor ? 'Actualizar' : 'Crear'} Doctor
        </Button>
      </div>
    </form>
  );
}

export default DoctorForm; 