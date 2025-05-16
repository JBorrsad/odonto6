import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getPatientById, getOdontogramByPatientId, getAppointmentsByPatient } from '../services/api';
import Card from '../components/common/Card';
import Odontogram from '../components/Odontogram';

function PatientDetailPage() {
  const { id } = useParams();
  const [patient, setPatient] = useState(null);
  const [odontogram, setOdontogram] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('info');

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
      case 'confirmada':
        return 'bg-green-100 text-green-800';
      case 'sin confirmar':
        return 'bg-yellow-100 text-yellow-800';
      case 'en sala de espera':
        return 'bg-blue-100 text-blue-800';
      case 'en curso':
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
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
        <h1 className="text-2xl font-bold text-gray-900">{patient.nombre} {patient.apellidos}</h1>
        <Link to="/patients" className="text-blue-500 hover:underline">
          Volver a pacientes
        </Link>
      </div>

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
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Información Personal</h3>
                <div className="grid grid-cols-2 gap-3">
                  <div className="text-gray-500">Edad:</div>
                  <div>{patient.edad} años</div>
                  
                  <div className="text-gray-500">Sexo:</div>
                  <div>{patient.sexo === 'M' ? 'Masculino' : 'Femenino'}</div>
                  
                  <div className="text-gray-500">Teléfono:</div>
                  <div>{patient.telefono}</div>
                  
                  <div className="text-gray-500">Email:</div>
                  <div>{patient.email}</div>
                </div>
              </div>
              
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Próxima Cita</h3>
                {appointments && appointments.length > 0 ? (
                  <div className="border rounded p-3">
                    <div className="font-medium">{formatDate(appointments[0].fecha)}</div>
                    <div className="text-sm text-gray-500">Dr. {appointments[0].doctor?.nombre || 'N/A'}</div>
                    <div className="text-sm">{appointments[0].tratamiento || 'N/A'}</div>
                    <div className={`text-xs mt-2 inline-block px-2 py-1 rounded ${getStatusClass(appointments[0].estado)}`}>
                      {appointments[0].estado || 'Sin estado'}
                    </div>
                  </div>
                ) : (
                  <div className="text-gray-500">No hay citas programadas</div>
                )}
              </div>
            </div>
          </div>
        </Card>
      )}

      {activeTab === 'odontogram' && (
        <Card>
          <div className="p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Odontograma</h3>
            {odontogram ? (
              <Odontogram data={odontogram} isChild={patient.edad < 18} />
            ) : (
              <div className="text-center py-8 text-gray-500">
                No hay datos del odontograma disponibles
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
                      <div className="font-medium">{formatDate(appointment.fecha)}</div>
                      <div className={`text-xs px-2 py-1 rounded ${getStatusClass(appointment.estado)}`}>
                        {appointment.estado || 'Sin estado'}
                      </div>
                    </div>
                    <div className="text-sm text-gray-500">Dr. {appointment.doctor?.nombre || 'N/A'}</div>
                    <div className="text-sm">{appointment.tratamiento || 'N/A'}</div>
                    {appointment.notas && (
                      <div className="text-sm mt-1 text-gray-500">{appointment.notas}</div>
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