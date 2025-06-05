import React, { useEffect, useState } from 'react';
import { getPatients } from '../services/api';
import { Link } from 'react-router-dom';

// Componente de avatar para pacientes
function PatientAvatar({ nombre, apellido, initial = null }) {
  // Generar iniciales para el avatar
  const initials = initial || `${nombre?.charAt(0) || ''}${apellido?.charAt(0) || ''}`;
  
  // Colores para los avatares según la imagen de referencia
  const getAvatarColor = (letter) => {
    const colors = {
      'W': 'bg-orange-100 text-orange-700',
      'M': 'bg-blue-100 text-blue-700',
      'T': 'bg-blue-100 text-blue-700',
      'D': 'bg-pink-100 text-pink-700',
      'N': 'bg-green-100 text-green-700',
      'B': 'bg-amber-100 text-amber-700',
      'A': 'bg-blue-100 text-blue-700',
      'J': 'bg-emerald-100 text-emerald-700',
      'K': 'bg-rose-100 text-rose-700',
      'MM': 'bg-blue-100 text-blue-700',
      'JH': 'bg-green-100 text-green-700'
    };
    
    return colors[initials] || 'bg-gray-100 text-gray-700';
  };
  
  return (
    <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${getAvatarColor(initials)}`}>
      {initials}
    </div>
  );
}

function PatientsPage() {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [viewMode, setViewMode] = useState("table");

  // Función para formatear la dirección desde el objeto direccion del backend
  const formatAddress = (direccion) => {
    if (!direccion) return "Sin dirección registrada";
    
    const parts = [];
    if (direccion.calle) parts.push(direccion.calle);
    if (direccion.numero) parts.push(direccion.numero);
    if (direccion.colonia) parts.push(direccion.colonia);
    if (direccion.ciudad) parts.push(direccion.ciudad);
    
    return parts.length > 0 ? parts.join(', ') : "Sin dirección registrada";
  };

  // Función para formatear fecha desde el backend
  const formatDate = (dateString) => {
    if (!dateString) return "Sin fecha";
    
    try {
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('es-ES', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      }).format(date);
    } catch (error) {
      return "Fecha inválida";
    }
  };

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        // Cargamos los datos reales de la API
        const data = await getPatients();
        
        // Transformamos los datos para adaptarlos a nuestro formato de visualización
        const formattedPatients = data.map(patient => {
          const initial = `${patient.nombre?.charAt(0) || ''}${patient.apellido?.charAt(0) || ''}`;
          
          return {
            id: patient.id,
            // Usar datos reales del backend
            name: `${patient.nombre || 'Sin nombre'} ${patient.apellido || 'Sin apellido'}`.trim(),
            phone: patient.telefono || 'Sin teléfono',
            email: patient.email || 'Sin email',
            address: formatAddress(patient.direccion),
            
            // Placeholders para mantener la estructura visual
            registered: formatDate(patient.fechaNacimiento) || 'Mar 06, 2021', // Usando fechaNacimiento como placeholder hasta tener fechaRegistro
            lastVisit: '01 May 2021', // Placeholder - este dato no está en el backend aún
            lastTreatment: 'Tooth Scaling', // Placeholder - este dato no está en el backend aún
            
            initials: initial
          };
        });
        
        setPatients(formattedPatients);
      } catch (err) {
        console.error("Error al cargar pacientes:", err);
        setError(err.message || "Error al cargar los pacientes");
      } finally {
        setLoading(false);
      }
    };

    fetchPatients();
  }, []);

  return (
    <div className="p-6">
      {/* Contador de pacientes y botones de vista */}
      <div className="flex justify-between items-center mb-4">
        <div className="flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-gray-400 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2.002 2.002 0 114 0 2.002 2.002 0 01-4 0z" />
          </svg>
          <span className="text-base font-medium">{patients.length || 0}</span>
          <span className="text-sm text-gray-500 ml-1">total patients</span>
        </div>
        
        <div className="flex items-center gap-x-2">
          <div className="text-sm text-gray-500">Filters</div>
          
          {/* Botón de filtro */}
          <button className="p-2 bg-gray-100 rounded hover:bg-gray-200">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
            </svg>
          </button>
          
          {/* Botones de vista */}
          <div className="flex">
            <button 
              onClick={() => setViewMode("grid")} 
              className={`p-2 rounded-l border border-gray-300 ${viewMode === "grid" ? 'bg-blue-50' : 'bg-white'}`}
            >
              <svg xmlns="http://www.w3.org/2000/svg" className={`h-5 w-5 ${viewMode === "grid" ? 'text-blue-600' : 'text-gray-500'}`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
              </svg>
            </button>
            <button 
              onClick={() => setViewMode("table")} 
              className={`p-2 rounded-r border border-gray-300 border-l-0 ${viewMode === "table" ? 'bg-blue-50' : 'bg-white'}`}
            >
              <svg xmlns="http://www.w3.org/2000/svg" className={`h-5 w-5 ${viewMode === "table" ? 'text-blue-600' : 'text-gray-500'}`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 10h16M4 14h16M4 18h16" />
              </svg>
            </button>
          </div>
          
          {/* Botón de añadir paciente */}
          <Link 
            to="/patients/new" 
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md transition-colors text-sm flex items-center"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            Add Patient
          </Link>
        </div>
      </div>
      
      {/* Tabla de pacientes */}
      {loading ? (
        <div className="text-center py-10">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
          <p className="mt-2 text-gray-700">Cargando pacientes...</p>
        </div>
      ) : error ? (
        <div className="p-4 text-red-500 bg-white rounded-md shadow">
          Error: {error}
        </div>
      ) : patients.length === 0 ? (
        <div className="p-4 text-center text-gray-500 bg-white rounded-md shadow">
          No se encontraron pacientes
        </div>
      ) : (
        <div className="bg-white rounded-md shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-white">
              <tr>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  Patient Name
                  <span className="ml-1 text-gray-400 inline-block">▼</span>
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  Phone
                  <span className="ml-1 text-gray-400 inline-block">▼</span>
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  Email
                  <span className="ml-1 text-gray-400 inline-block">▼</span>
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  Address
                  <span className="ml-1 text-gray-400 inline-block">▼</span>
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  Registered
                  <span className="ml-1 text-gray-400 inline-block">▼</span>
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider whitespace-nowrap">
                  Last Visit
                  <span className="ml-1 text-gray-400 inline-block">▼</span>
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Last Treatment
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {patients.map((patient) => (
                <tr key={patient.id} className="hover:bg-gray-50 cursor-pointer">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <PatientAvatar nombre="" apellido="" initial={patient.initials} />
                      <div className="ml-3">
                        <div className="text-sm font-medium text-gray-900">{patient.name}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-500 flex items-center">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-gray-400 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                      </svg>
                      {patient.phone}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-500 flex items-center">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-gray-400 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                      </svg>
                      {patient.email}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-500">{patient.address}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-500">{patient.registered}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-500">{patient.lastVisit}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900">{patient.lastTreatment}</div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default PatientsPage;