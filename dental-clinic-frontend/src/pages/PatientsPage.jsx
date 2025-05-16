import React, { useEffect, useState } from 'react';
import { getPatients } from '../services/api';
import { Link } from 'react-router-dom';
import Card from '../components/common/Card';

function PatientsPage() {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        const data = await getPatients();
        setPatients(data);
      } catch (err) {
        setError(err.message || "Error al cargar los pacientes");
      } finally {
        setLoading(false);
      }
    };

    fetchPatients();
  }, []); // Se ejecuta una vez al montar el componente

  const filteredPatients = patients.filter(patient => 
    patient.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    patient.apellidos?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Pacientes</h1>
        <Link to="/patients/new" className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
          Nuevo Paciente
        </Link>
      </div>

      <Card className="mb-6">
        <div className="p-4">
          <input
            type="text"
            placeholder="Buscar pacientes..."
            className="w-full p-2 border rounded"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </Card>

      {loading ? (
        <div className="text-center py-10">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
          <p className="mt-2">Cargando pacientes...</p>
        </div>
      ) : error ? (
        <Card>
          <div className="p-4 text-red-500">
            Error: {error}
          </div>
        </Card>
      ) : filteredPatients.length === 0 ? (
        <Card>
          <div className="p-4 text-center text-gray-500">
            No se encontraron pacientes
          </div>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredPatients.map(patient => (
            <Link key={patient.id} to={`/patients/${patient.id}`} className="block hover:no-underline">
              <Card className="h-full hover:shadow-md transition-shadow">
                <div className="p-4">
                  <div className="font-semibold text-lg text-gray-900 mb-2">
                    {patient.nombre} {patient.apellidos}
                  </div>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    <div className="text-gray-500">Edad:</div>
                    <div>{patient.edad} años</div>
                    
                    <div className="text-gray-500">Sexo:</div>
                    <div>{patient.sexo === 'M' ? 'Masculino' : 'Femenino'}</div>
                    
                    <div className="text-gray-500">Teléfono:</div>
                    <div>{patient.telefono}</div>
                    
                    <div className="text-gray-500">Email:</div>
                    <div className="truncate">{patient.email}</div>
                  </div>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}

export default PatientsPage;