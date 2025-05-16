import React, { useState, useEffect } from 'react';
import Button from '../common/Button';

function PatientForm({ patient, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    nombre: '',
    apellido: '',
    fechaNacimiento: '',
    age: '',
    sexo: 'MALE',
    telefono: {
      numero: ''
    },
    email: {
      address: ''
    },
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
        apellido: patient.apellido || '',
        fechaNacimiento,
        age: patient.age || '',
        sexo: patient.sexo || 'MALE',
        telefono: {
          numero: patient.telefono?.numero || ''
        },
        email: {
          address: patient.email?.address || ''
        },
      });
    }
  }, [patient]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    // Manejar campos anidados
    if (name.includes('.')) {
      const [parent, child] = name.split('.');
      setForm({
        ...form,
        [parent]: {
          ...form[parent],
          [child]: value
        }
      });
    } else {
      setForm({
        ...form,
        [name]: value,
      });
    }
    
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
    if (!form.apellido) newErrors.apellido = 'El apellido es obligatorio';
    if (!form.fechaNacimiento) newErrors.fechaNacimiento = 'La fecha de nacimiento es obligatoria';
    
    if (!form.telefono.numero) {
      newErrors['telefono.numero'] = 'El teléfono es obligatorio';
    } else if (!/^\d{9}$/.test(form.telefono.numero)) {
      newErrors['telefono.numero'] = 'El teléfono debe tener 9 dígitos';
    }
    
    if (!form.email.address) {
      newErrors['email.address'] = 'El email es obligatorio';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.address)) {
      newErrors['email.address'] = 'El email no es válido';
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
        age: form.age ? parseInt(form.age, 10) : null,
      };
      
      onSubmit(patientData);
    }
  };

  const calculateAge = (birthdate) => {
    if (!birthdate) return;
    
    const today = new Date();
    const birthDate = new Date(birthdate);
    let age = today.getFullYear() - birthDate.getFullYear();
    const m = today.getMonth() - birthDate.getMonth();
    
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    
    setForm({
      ...form,
      age: age.toString()
    });
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
        <label className="block text-sm font-medium text-gray-700">Apellido</label>
        <input
          type="text"
          name="apellido"
          value={form.apellido}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.apellido ? 'border-red-300' : ''
          }`}
        />
        {errors.apellido && <p className="mt-1 text-sm text-red-600">{errors.apellido}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Fecha de Nacimiento</label>
        <input
          type="date"
          name="fechaNacimiento"
          value={form.fechaNacimiento}
          onChange={(e) => {
            handleChange(e);
            calculateAge(e.target.value);
          }}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors.fechaNacimiento ? 'border-red-300' : ''
          }`}
        />
        {errors.fechaNacimiento && <p className="mt-1 text-sm text-red-600">{errors.fechaNacimiento}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Edad</label>
        <input
          type="number"
          name="age"
          value={form.age}
          onChange={handleChange}
          min="0"
          max="120"
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Sexo</label>
        <select
          name="sexo"
          value={form.sexo}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        >
          <option value="MALE">Masculino</option>
          <option value="FEMALE">Femenino</option>
        </select>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Teléfono</label>
        <input
          type="tel"
          name="telefono.numero"
          value={form.telefono.numero}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors['telefono.numero'] ? 'border-red-300' : ''
          }`}
        />
        {errors['telefono.numero'] && <p className="mt-1 text-sm text-red-600">{errors['telefono.numero']}</p>}
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700">Email</label>
        <input
          type="email"
          name="email.address"
          value={form.email.address}
          onChange={handleChange}
          className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
            errors['email.address'] ? 'border-red-300' : ''
          }`}
        />
        {errors['email.address'] && <p className="mt-1 text-sm text-red-600">{errors['email.address']}</p>}
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