import React, { useState, useEffect } from 'react';
import Button from '../common/Button';

function PatientForm({ patient, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    nombre: '',
    apellido: '',
    fechaNacimiento: '',
    sexo: 'MASCULINO',
    telefono: '',
    email: '',
    direccion: {
      calle: '',
      numero: '',
      colonia: '',
      codigoPostal: '',
      ciudad: '',
      estado: '',
      pais: ''
    }
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
        sexo: patient.sexo || 'MASCULINO',
        telefono: patient.telefono || '',
        email: patient.email || '',
        direccion: {
          calle: patient.direccion?.calle || '',
          numero: patient.direccion?.numero || '',
          colonia: patient.direccion?.colonia || '',
          codigoPostal: patient.direccion?.codigoPostal || '',
          ciudad: patient.direccion?.ciudad || '',
          estado: patient.direccion?.estado || '',
          pais: patient.direccion?.pais || ''
        }
      });
    }
  }, [patient]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    // Manejar campos anidados de dirección
    if (name.startsWith('direccion.')) {
      const field = name.split('.')[1];
      setForm({
        ...form,
        direccion: {
          ...form.direccion,
          [field]: value
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
    
    if (!form.telefono) {
      newErrors.telefono = 'El teléfono es obligatorio';
    } else if (!/^\d{9,}$/.test(form.telefono.replace(/\D/g, ''))) {
      newErrors.telefono = 'El teléfono debe tener al menos 9 dígitos';
    }
    
    if (!form.email) {
      newErrors.email = 'El email es obligatorio';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      newErrors.email = 'El email no es válido';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validate()) {
      // Formatear datos para enviar al backend
      const patientData = {
        nombre: form.nombre,
        apellido: form.apellido,
        fechaNacimiento: form.fechaNacimiento,
        sexo: form.sexo,
        telefono: form.telefono,
        email: form.email,
        direccion: form.direccion
      };
      
      onSubmit(patientData);
    }
  };

  // Función para calcular edad (solo para mostrar, no se envía)
  const calculateAge = (birthdate) => {
    if (!birthdate) return '';
    
    const today = new Date();
    const birthDate = new Date(birthdate);
    let age = today.getFullYear() - birthDate.getFullYear();
    const m = today.getMonth() - birthDate.getMonth();
    
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    
    return age;
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
          {form.fechaNacimiento && (
            <p className="mt-1 text-sm text-gray-500">Edad: {calculateAge(form.fechaNacimiento)} años</p>
          )}
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
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Teléfono</label>
          <input
            type="tel"
            name="telefono"
            value={form.telefono}
            onChange={handleChange}
            placeholder="Ej: 612345678"
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
            placeholder="ejemplo@correo.com"
            className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 ${
              errors.email ? 'border-red-300' : ''
            }`}
          />
          {errors.email && <p className="mt-1 text-sm text-red-600">{errors.email}</p>}
        </div>
      </div>

      <div className="border-t pt-4">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Dirección</h3>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Calle</label>
            <input
              type="text"
              name="direccion.calle"
              value={form.direccion.calle}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Número</label>
            <input
              type="text"
              name="direccion.numero"
              value={form.direccion.numero}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Colonia</label>
            <input
              type="text"
              name="direccion.colonia"
              value={form.direccion.colonia}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Código Postal</label>
            <input
              type="text"
              name="direccion.codigoPostal"
              value={form.direccion.codigoPostal}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Ciudad</label>
            <input
              type="text"
              name="direccion.ciudad"
              value={form.direccion.ciudad}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Estado</label>
            <input
              type="text"
              name="direccion.estado"
              value={form.direccion.estado}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">País</label>
            <input
              type="text"
              name="direccion.pais"
              value={form.direccion.pais}
              onChange={handleChange}
              placeholder="España"
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>
        </div>
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