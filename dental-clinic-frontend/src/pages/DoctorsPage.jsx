import React, { useEffect, useState } from 'react';
import { getDoctors, createDoctor, updateDoctor, deleteDoctor } from '../services/api';
import Modal from '../components/common/Modal';
import Button from '../components/common/Button';
import DoctorForm from '../components/doctor/DoctorForm';

// Componente de avatar para doctores
function DoctorAvatar({ nombre, apellido, initial = null }) {
  // Generar iniciales para el avatar
  const initials = initial || `${nombre?.charAt(0) || ''}${apellido?.charAt(0) || ''}`;
  
  // Colores para los avatares según la imagen de referencia
  const getAvatarColor = (letter) => {
    const colors = {
      'R': 'bg-pink-100 text-pink-700',
      'D': 'bg-blue-100 text-blue-700',
      'P': 'bg-blue-100 text-blue-700',
      'J': 'bg-green-100 text-green-700',
      'M': 'bg-violet-100 text-violet-700',
      'DR': 'bg-amber-100 text-amber-700',
    };
    
    return colors[initials] || 'bg-gray-100 text-gray-700';
  };
  
  return (
    <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${getAvatarColor(initials)}`}>
      {initials}
    </div>
  );
}

// Componente para mostrar días de trabajo
function WorkingDays({ days }) {
  const allDays = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
  
  return (
    <div className="flex space-x-1">
      {allDays.map((day, index) => {
        const isWorkingDay = days.includes(index);
        return (
          <div 
            key={index} 
            className={`w-6 h-6 rounded-full flex items-center justify-center text-xs ${isWorkingDay 
              ? 'bg-blue-100 text-blue-600' 
              : 'bg-gray-100 text-gray-400'}`}
          >
            {day}
          </div>
        );
      })}
    </div>
  );
}

function DoctorsPage() {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedTab, setSelectedTab] = useState("Doctor Staff");
  const [viewMode, setViewMode] = useState("table");
  const [showForm, setShowForm] = useState(false);
  const [editingDoctor, setEditingDoctor] = useState(null);
  const [formLoading, setFormLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(null);

  useEffect(() => {
    fetchDoctors();
  }, []);

  const fetchDoctors = async () => {
    console.log("Iniciando fetchDoctors...");
    try {
      setLoading(true);
      const response = await getDoctors();
      
      console.log("Datos recibidos de la API:", response);
      
      if (!response || response.length === 0) {
        console.warn("La API devolvió un array vacío o null");
        setDoctors([]);
        return;
      }
      
      // Transformar los datos para incluir información adicional
      const enhancedDoctors = response.map(doctor => {
        // Crear iniciales a partir del nombre
        const nameParts = doctor.nombreCompleto ? doctor.nombreCompleto.split(' ') : ['D', 'R'];
        const initials = `${nameParts[0].charAt(0)}${nameParts.length > 1 ? nameParts[1].charAt(0) : ''}`;
        
        // Generar días de trabajo fijos basados en el id del doctor
        const workingDays = [];
        // Usar los últimos 4 caracteres del ID para determinar días de trabajo
        const idSeed = doctor.id.slice(-4);
        for (let i = 0; i < 7; i++) {
          // Si el valor ASCII del carácter en esa posición del ID es par, agregar el día
          if (idSeed.charCodeAt(i % idSeed.length) % 2 === 0) {
            workingDays.push(i);
          }
        }
        
        // Asignar teléfono fijo basado en id para que sea consistente
        const idSum = doctor.id.split('').reduce((sum, char) => sum + char.charCodeAt(0), 0);
        const phone = `${100 + (idSum % 900)} 555-${(1000 + (idSum % 9000)).toString().padStart(4, '0')}`;
        
        // Crear email basado en nombre
        const email = `${nameParts[0].toLowerCase()}@avicena.com`;
        
        // Asignar servicios basados en la especialidad
        const services = [];
        if (doctor.especialidad.includes('1') || doctor.especialidad.includes('3') || doctor.especialidad.includes('5')) {
          services.push('Dental service');
        }
        if (doctor.especialidad.includes('2') || doctor.especialidad.includes('4')) {
          services.push('Oral Disease service');
        }
        if (services.length === 0) {
          services.push('Dental service');
        }
        
        // Asignar tipo de jornada basado en el id
        const isFullTime = doctor.id.charCodeAt(0) % 2 === 0;
        
        return {
          ...doctor,
          initials,
          workingDays,
          phone,
          email,
          services,
          isFullTime
        };
      });
      
      setDoctors(enhancedDoctors);
    } catch (err) {
      console.error("Error al cargar doctores:", err);
      setError(err.message || "Error al cargar los doctores");
    } finally {
      setLoading(false);
    }
  };

  const handleAddDoctor = () => {
    setEditingDoctor(null);
    setShowForm(true);
  };

  const handleEditDoctor = (doctor) => {
    setEditingDoctor(doctor);
    setShowForm(true);
  };

  const handleDeleteDoctor = async (doctorId) => {
    if (window.confirm('¿Estás seguro de que deseas eliminar este doctor? Esta acción no se puede deshacer.')) {
      try {
        setDeleteLoading(doctorId);
        await deleteDoctor(doctorId);
        setDoctors(prevDoctors => prevDoctors.filter(doc => doc.id !== doctorId));
      } catch (err) {
        setError(err.message || "Error al eliminar el doctor");
      } finally {
        setDeleteLoading(null);
      }
    }
  };

  const handleSubmitForm = async (formData) => {
    setFormLoading(true);
    try {
      if (editingDoctor) {
        const updatedDoctor = await updateDoctor(editingDoctor.id, formData);
        setDoctors(prevDoctors => prevDoctors.map(doc => 
          doc.id === editingDoctor.id ? updatedDoctor : doc
        ));
      } else {
        const newDoctor = await createDoctor(formData);
        setDoctors(prevDoctors => [...prevDoctors, newDoctor]);
      }
      setShowForm(false);
    } catch (err) {
      setError(err.message || `Error al ${editingDoctor ? 'actualizar' : 'crear'} el doctor`);
    } finally {
      setFormLoading(false);
    }
  };

  const filteredDoctors = doctors;

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-semibold text-gray-800">Staff List</h1>
        <div className="flex items-center space-x-2">
          <div className="relative">
            <input
              type="text"
              placeholder="Search for anything here..."
              className="pl-10 pr-4 py-2 border rounded-full w-64 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <svg className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          
          <button className="bg-blue-600 text-white rounded-full p-2">
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
          </button>
          
          <button className="text-blue-600 p-2">
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </button>
          
          <button className="text-blue-600 p-2">
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
          </button>
        </div>
      </div>
      
      {/* Tabs */}
      <div className="border-b border-gray-200 mb-6">
        <div className="flex">
          <button
            onClick={() => setSelectedTab("Doctor Staff")}
            className={`py-2 px-4 text-sm font-medium border-b-2 ${
              selectedTab === "Doctor Staff" 
                ? "border-blue-500 text-blue-600" 
                : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
            }`}
          >
            Doctor Staff
          </button>
          <button
            onClick={() => setSelectedTab("General Staff")}
            className={`py-2 px-4 text-sm font-medium border-b-2 ${
              selectedTab === "General Staff" 
                ? "border-blue-500 text-blue-600" 
                : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
            }`}
          >
            General Staff
          </button>
        </div>
      </div>
      
      {/* Doctor count and add doctor button */}
      <div className="flex justify-between items-center mb-6">
        <div className="flex items-center">
          <svg className="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          <span className="text-xl font-semibold text-gray-800 mr-2">120</span>
          <span className="text-sm text-gray-500">Doctor</span>
        </div>
        
        <div className="flex items-center space-x-2">
          <button className="flex items-center space-x-1 bg-gray-100 hover:bg-gray-200 text-sm font-medium text-gray-700 px-3 py-1.5 rounded">
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
            </svg>
            <span>Filters</span>
          </button>
          
          <button 
            onClick={handleAddDoctor}
            className="bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium px-3 py-1.5 rounded transition duration-150"
          >
            Add Doctor
          </button>
        </div>
      </div>
      
      {error && (
        <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-500 text-red-700">
          <p>{error}</p>
        </div>
      )}
      
      {loading ? (
        <div className="text-center py-10">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
          <p className="mt-2 text-gray-700">Cargando doctores...</p>
        </div>
      ) : filteredDoctors.length === 0 ? (
        <div className="p-4 bg-white rounded-md shadow text-center text-gray-500">
          No se encontraron doctores en la base de datos. Por favor, verifica la conexión al servidor o agrega nuevos doctores.
        </div>
      ) : (
        <div className="bg-white rounded-md shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-white">
              <tr>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  NAME
                  <span className="ml-1 text-gray-400 inline-block">▼</span>
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  CONTACT
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  WORKING DAYS
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  ASSIGNED TREATMENT
                </th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  TYPE
                </th>
                <th scope="col" className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredDoctors.map((doctor) => (
                <tr key={doctor.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <DoctorAvatar nombre="" apellido="" initial={doctor.initials} />
                      <div className="ml-3">
                        <div className="text-sm font-medium text-gray-900">{doctor.nombreCompleto}</div>
                        <div className="text-xs text-gray-500">Pediatric Dentistry</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-blue-600">{doctor.phone}</div>
                    <div className="text-xs text-blue-600">{doctor.email}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <WorkingDays days={doctor.workingDays} />
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900">
                      {doctor.services.join(', ')}
                      {doctor.services.length > 1 && (
                        <span className="ml-1 text-xs text-gray-500">+{doctor.services.length - 1}</span>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      doctor.isFullTime ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {doctor.isFullTime ? 'FULL-TIME' : 'PART-TIME'}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button className="text-gray-400 hover:text-gray-500">
                      <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z" />
                      </svg>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showForm && (
        <Modal
          title={editingDoctor ? "Editar Doctor" : "Crear Doctor"}
          onClose={() => setShowForm(false)}
        >
          <DoctorForm 
            doctor={editingDoctor} 
            onSubmit={handleSubmitForm} 
            onCancel={() => setShowForm(false)}
            loading={formLoading}
          />
        </Modal>
      )}
    </div>
  );
}

export default DoctorsPage; 