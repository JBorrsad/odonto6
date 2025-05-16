import React, { useEffect, useState } from 'react';
import Card from '../components/common/Card';
import { Link } from 'react-router-dom';
import { getPatients, getDoctors, getAppointments, getOdontogramByPatientId } from '../services/api';

function HomePage() {
  const [data, setData] = useState({
    patients: { loading: true, data: null, error: null },
    doctors: { loading: true, data: null, error: null },
    appointments: { loading: true, data: null, error: null },
    odontogram: { loading: true, data: null, error: null }
  });

  useEffect(() => {
    // Función para cargar datos de un endpoint
    const fetchData = async (endpoint, fetchFunction, params) => {
      try {
        const result = await fetchFunction(params);
        setData(prev => ({
          ...prev,
          [endpoint]: { loading: false, data: result, error: null }
        }));
      } catch (error) {
        setData(prev => ({
          ...prev,
          [endpoint]: { loading: false, data: null, error: error.message || 'Error al cargar datos' }
        }));
      }
    };

    // Cargar datos de todos los endpoints
    fetchData('patients', getPatients);
    fetchData('doctors', getDoctors);
    fetchData('appointments', getAppointments);
    
    // Para odontograma necesitamos un ID, intentaremos obtener el primero disponible
    fetchData('odontogram', async () => {
      try {
        const patients = await getPatients();
        if (patients && patients.length > 0) {
          return await getOdontogramByPatientId(patients[0].id);
        }
        throw new Error('No hay pacientes disponibles para obtener odontograma');
      } catch (error) {
        throw error;
      }
    });

  }, []);

  // Función para renderizar un bloque de datos
  const renderDataBlock = (title, dataState, icon) => {
    return (
      <Card className="mb-6">
        <div className="p-4 border-b">
          <div className="flex items-center">
            <div className="text-2xl mr-3">{icon}</div>
            <h2 className="text-xl font-semibold">{title}</h2>
          </div>
        </div>
        
        <div className="p-4">
          {dataState.loading ? (
            <div className="text-center py-4">
              <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-gray-500"></div>
              <p className="mt-2 text-gray-500">Cargando datos...</p>
            </div>
          ) : dataState.error ? (
            <div className="bg-red-50 p-3 rounded text-red-600">
              <div className="font-semibold mb-1">❌ Error al cargar datos</div>
              <div>{dataState.error}</div>
            </div>
          ) : !dataState.data || (Array.isArray(dataState.data) && dataState.data.length === 0) ? (
            <div className="text-center py-4 text-gray-500">
              No hay datos disponibles
            </div>
          ) : (
            <div>
              <div className="mb-2 text-green-600 font-medium">✅ Datos cargados correctamente</div>
              <pre className="bg-gray-100 p-3 rounded overflow-auto max-h-64 text-xs">
                {JSON.stringify(dataState.data, null, 2)}
              </pre>
            </div>
          )}
        </div>
      </Card>
    );
  };

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">Prueba de Conexión con API</h1>
        <p className="text-gray-600">Resultados de peticiones GET a los endpoints principales</p>
      </div>

      <div className="mb-4 flex space-x-4">
        <Link to="/patients" className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
          Ver aplicación
        </Link>
      </div>

      {renderDataBlock('Pacientes (GET /api/patients)', data.patients, '👤')}
      {renderDataBlock('Doctores (GET /api/doctors)', data.doctors, '👨‍⚕️')}
      {renderDataBlock('Citas (GET /api/appointments)', data.appointments, '📅')}
      {renderDataBlock('Odontograma (GET /api/patients/{id}/odontogram)', data.odontogram, '🦷')}
    </div>
  );
}

export default HomePage; 