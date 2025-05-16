import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createPatient } from '../services/api';
import PatientForm from '../components/patient/PatientForm';
import Card from '../components/common/Card';

function PatientNewPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (patientData) => {
    setLoading(true);
    setError(null);
    
    try {
      const newPatient = await createPatient(patientData);
      // Redirigir a la página de detalle del paciente creado
      navigate(`/patients/${newPatient.id}`);
    } catch (err) {
      console.error('Error al crear paciente:', err);
      setError(err.message || 'Error al crear el paciente. Inténtalo de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/patients');
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Crear Nuevo Paciente</h1>
      
      {error && (
        <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-500 text-red-700">
          <p>{error}</p>
        </div>
      )}
      
      <Card>
        <div className="p-6">
          <PatientForm 
            onSubmit={handleSubmit} 
            onCancel={handleCancel}
            loading={loading}
          />
        </div>
      </Card>
    </div>
  );
}

export default PatientNewPage; 