import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getPatientById, getOdontogramByPatientId, getAppointmentsByPatient, updatePatient, deletePatient } from '../services/api';
import Card from '../components/common/Card';
import Odontogram from '../components/patient/Odontogram';
import PatientForm from '../components/patient/PatientForm';
import Button from '../components/common/Button';

function PatientDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [patient, setPatient] = useState(null);
  const [odontogram, setOdontogram] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('info');
  const [isEditing, setIsEditing] = useState(false);
  const [saveLoading, setSaveLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const patientData = await getPatientById(id);
        setPatient(patientData);

        try {
          const odontogramData = await getOdontogramByPatientId(id);
          setOdontogram(odontogramData);
        } catch (odontogramErr) {
          console.error('Error al cargar odontograma:', odontogramErr);
          // Solo mostramos error si no es un problema de que no existe el odontograma
          if (odontogramErr.response?.status !== 404 && odontogramErr.response?.status !== 500) {
            console.error('Error inesperado al cargar odontograma:', odontogramErr);
          }
          setOdontogram(null);
          // No interrumpimos la carga completa si solo falla el odontograma
        }

        try {
          const appointmentsData = await getAppointmentsByPatient(id);
          setAppointments(appointmentsData);
        } catch (appointmentsErr) {
          console.error('Error al cargar citas:', appointmentsErr);
          // No interrumpimos la carga completa si solo fallan las citas
        }

      } catch (err) {
        setError(err.message || "Error al cargar los datos del paciente");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const calculateAge = (birthDate) => {
    if (!birthDate) return 'N/A';
    
    try {
      const birth = new Date(birthDate);
      const today = new Date();
      let age = today.getFullYear() - birth.getFullYear();
      const monthDiff = today.getMonth() - birth.getMonth();
      
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
        age--;
      }
      
      return age;
    } catch (error) {
      return 'N/A';
    }
  };

  const formatAddress = (direccion) => {
    if (!direccion) return "Sin dirección registrada";
    
    const parts = [];
    if (direccion.calle) parts.push(direccion.calle);
    if (direccion.numero) parts.push(direccion.numero);
    if (direccion.colonia) parts.push(direccion.colonia);
    if (direccion.ciudad) parts.push(direccion.ciudad);
    if (direccion.estado) parts.push(direccion.estado);
    
    return parts.length > 0 ? parts.join(', ') : "Sin dirección registrada";
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('es-ES', {
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric',
      hour: '2-digit', 
      minute: '2-digit'
    }).format(date);
  };

  const getStatusClass = (status) => {
    switch (status?.toLowerCase()) {
      case 'confirmed':
        return 'bg-green-100 text-green-800';
      case 'pending':
        return 'bg-yellow-100 text-yellow-800';
      case 'waiting_room':
        return 'bg-blue-100 text-blue-800';
      case 'in_progress':
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const handleSavePatient = async (updatedPatientData) => {
    setSaveLoading(true);
    try {
      const updated = await updatePatient(id, updatedPatientData);
      setPatient(updated);
      setIsEditing(false);
    } catch (err) {
      setError(err.message || "Error al actualizar el paciente");
    } finally {
      setSaveLoading(false);
    }
  };

  const handleDeletePatient = async () => {
    if (window.confirm('¿Estás seguro de que deseas eliminar este paciente? Esta acción no se puede deshacer.')) {
      setDeleteLoading(true);
      try {
        await deletePatient(id);
        navigate('/patients');
      } catch (err) {
        setError(err.message || "Error al eliminar el paciente");
        setDeleteLoading(false);
      }
    }
  };

  const handleOdontogramUpdate = (updatedOdontogram) => {
    setOdontogram(updatedOdontogram);
  };

  if (loading) {
    return (
      <div className="p-6 text-center">
        <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
        <p className="mt-2">Cargando datos del paciente...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-6">
        <Card>
          <div className="p-4 text-red-500">
            Error: {error}
          </div>
        </Card>
      </div>
    );
  }

  if (!patient) {
    return (
      <div className="p-6">
        <Card>
          <div className="p-4 text-center">
            No se encontró el paciente
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="mb-6 flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-900">{patient.nombre} {patient.apellido}</h1>
        <div className="flex space-x-2">
          {!isEditing && (
            <>
              <Button 
                onClick={() => setIsEditing(true)} 
                variant="secondary"
              >
                Editar
              </Button>
              <Button 
                onClick={handleDeletePatient} 
                variant="danger"
                loading={deleteLoading}
              >
                Eliminar
              </Button>
            </>
          )}
          <Link to="/patients" className="inline-block py-2 px-4 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
            Volver
          </Link>
        </div>
      </div>

      {error && (
        <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-500 text-red-700">
          <p>{error}</p>
        </div>
      )}

      <div className="mb-6">
        <div className="flex border-b border-gray-200">
          <button
            className={`py-2 px-4 font-medium ${activeTab === 'info' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500 hover:text-gray-700'}`}
            onClick={() => setActiveTab('info')}
          >
            Información
          </button>
          <button
            className={`py-2 px-4 font-medium ${activeTab === 'odontogram' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500 hover:text-gray-700'}`}
            onClick={() => setActiveTab('odontogram')}
          >
            Odontograma
          </button>
          <button
            className={`py-2 px-4 font-medium ${activeTab === 'appointments' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500 hover:text-gray-700'}`}
            onClick={() => setActiveTab('appointments')}
          >
            Historial de Citas
          </button>
        </div>
      </div>

      {activeTab === 'info' && (
        <Card>
          <div className="p-6">
            {isEditing ? (
              <PatientForm 
                patient={patient} 
                onSubmit={handleSavePatient} 
                onCancel={() => setIsEditing(false)}
                loading={saveLoading}
              />
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Información Personal</h3>
                  <div className="grid grid-cols-2 gap-3">
                    <div className="text-gray-500">Edad:</div>
                    <div>{calculateAge(patient.fechaNacimiento)} años</div>
                    
                    <div className="text-gray-500">Sexo:</div>
                    <div>{patient.sexo === 'MASCULINO' ? 'Masculino' : patient.sexo === 'FEMENINO' ? 'Femenino' : patient.sexo || 'No especificado'}</div>
                    
                    <div className="text-gray-500">Fecha de Nacimiento:</div>
                    <div>{patient.fechaNacimiento ? new Date(patient.fechaNacimiento).toLocaleDateString('es-ES') : 'No registrada'}</div>
                    
                    <div className="text-gray-500">Teléfono:</div>
                    <div>{patient.telefono || 'Sin teléfono'}</div>
                    
                    <div className="text-gray-500">Email:</div>
                    <div>{patient.email || 'Sin email'}</div>
                    
                    <div className="text-gray-500">Dirección:</div>
                    <div>{formatAddress(patient.direccion)}</div>
                  </div>
                </div>
                
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Próxima Cita</h3>
                  {appointments && appointments.length > 0 ? (
                    <div className="border rounded p-3">
                      <div className="font-medium">{formatDate(appointments[0].start)}</div>
                      <div className="text-sm text-gray-500">Dr. {appointments[0].doctorName || appointments[0].doctorId || 'N/A'}</div>
                      <div className="text-sm">{appointments[0].notes || 'Sin notas'}</div>
                      <div className={`text-xs mt-2 inline-block px-2 py-1 rounded ${getStatusClass(appointments[0].status)}`}>
                        {appointments[0].status || 'Sin estado'}
                      </div>
                    </div>
                  ) : (
                    <div className="text-gray-500">No hay citas programadas</div>
                  )}
                </div>
              </div>
            )}
          </div>
        </Card>
      )}

      {activeTab === 'odontogram' && (
        <Card>
          <div className="p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Odontograma</h3>
            {odontogram ? (
              <Odontogram 
                patientId={id}
                data={odontogram} 
                isChild={calculateAge(patient.fechaNacimiento) < 18} 
                isEditable={true}
                onUpdate={handleOdontogramUpdate}
              />
            ) : (
              <div className="text-center py-8">
                <div className="text-gray-500 mb-4">
                  Este paciente aún no tiene un odontograma registrado.
                </div>
                <div className="text-sm text-gray-400">
                  El odontograma se creará automáticamente cuando se registre la primera lesión o tratamiento.
                </div>
                <Odontogram 
                  patientId={id}
                  data={null} 
                  isChild={calculateAge(patient.fechaNacimiento) < 18} 
                  isEditable={true}
                  onUpdate={handleOdontogramUpdate}
                />
              </div>
            )}
          </div>
        </Card>
      )}

      {activeTab === 'appointments' && (
        <Card>
          <div className="p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-medium text-gray-900">Historial de Citas</h3>
              <Link to={`/schedule?patientId=${id}`} className="bg-blue-500 text-white px-3 py-1 rounded text-sm hover:bg-blue-600">
                Nueva Cita
              </Link>
            </div>
            
            {appointments && appointments.length > 0 ? (
              <div className="divide-y">
                {appointments.map((appointment) => (
                  <div key={appointment.id} className="py-3">
                    <div className="flex justify-between">
                      <div className="font-medium">{formatDate(appointment.start)}</div>
                      <div className={`text-xs px-2 py-1 rounded ${getStatusClass(appointment.status)}`}>
                        {appointment.status || 'Sin estado'}
                      </div>
                    </div>
                    <div className="text-sm text-gray-500">Dr. {appointment.doctorName || appointment.doctorId || 'N/A'}</div>
                    <div className="text-sm">{appointment.notes || 'Sin notas'}</div>
                    {appointment.notes && (
                      <div className="text-sm mt-1 text-gray-500">{appointment.notes}</div>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-8 text-gray-500">
                No hay citas en el historial
              </div>
            )}
          </div>
        </Card>
      )}
    </div>
  );
}

export default PatientDetailPage;