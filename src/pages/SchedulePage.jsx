import React, { useEffect, useState, useLayoutEffect } from 'react';
import { getAppointments, getDoctors, createAppointment, updateAppointment, deleteAppointment } from '../services/api';
import { useLocation, useNavigate } from 'react-router-dom';
import { useSearchParams } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import AppointmentForm from '../components/appointment/AppointmentForm';
import AppointmentDetail from '../components/appointment/AppointmentDetail';
import DailySchedule from '../components/appointment/DailySchedule';
import WeeklySchedule from '../components/appointment/WeeklySchedule';
import CalendarView from '../components/calendar/calendar-view';

// Datos simulados para mostrar en la UI
const MOCK_DOCTORS = [
  { id: "1", nombreCompleto: "Dr. Martínez", especialidad: "Ortodoncia" },
  { id: "2", nombreCompleto: "Dra. Rodríguez", especialidad: "Endodoncia" },
  { id: "3", nombreCompleto: "Dr. García", especialidad: "Periodoncia" },
  { id: "4", nombreCompleto: "Dra. López", especialidad: "Cirugía Oral" },
  { id: "5", nombreCompleto: "Dr. Hernández", especialidad: "Odontopediatría" }
];

// Función para generar una fecha simulada en mayo 16, 2025
const createMockDate = (hour, minute) => {
  return new Date(2025, 4, 16, hour, minute, 0);
};

// Citas simuladas para mostrar en la UI
const MOCK_APPOINTMENTS = [
  // Doctor 1 - Dr. Martínez
  {
    id: "1",
    patientId: "101",
    patientName: "María López",
    doctorId: "1",
    start: createMockDate(9, 0).toISOString(),
    end: createMockDate(10, 0).toISOString(),
    durationSlots: 2,
    status: "COMPLETADA",
    treatment: "Revisión General"
  },
  {
    id: "2",
    patientId: "102",
    patientName: "Juan Pérez",
    doctorId: "1",
    start: createMockDate(11, 30).toISOString(),
    end: createMockDate(12, 30).toISOString(),
    durationSlots: 2,
    status: "EN_CURSO",
    treatment: "Limpieza Dental"
  },
  {
    id: "3",
    patientId: "103",
    patientName: "Carlos Ruiz",
    doctorId: "1",
    start: createMockDate(15, 0).toISOString(),
    end: createMockDate(16, 0).toISOString(),
    durationSlots: 2,
    status: "CONFIRMADA",
    treatment: "Extracción"
  },
  
  // Doctor 2 - Dra. Rodríguez
  {
    id: "4",
    patientId: "104",
    patientName: "Ana Torres",
    doctorId: "2",
    start: createMockDate(9, 30).toISOString(),
    end: createMockDate(10, 30).toISOString(),
    durationSlots: 2,
    status: "COMPLETADA",
    treatment: "Blanqueamiento"
  },
  {
    id: "5",
    patientId: "105",
    patientName: "Elena García",
    doctorId: "2",
    start: createMockDate(12, 0).toISOString(),
    end: createMockDate(13, 0).toISOString(),
    durationSlots: 2,
    status: "PENDIENTE",
    treatment: "Ortodoncia"
  },
  {
    id: "6",
    patientId: "106",
    patientName: "Pablo Martín",
    doctorId: "2",
    start: createMockDate(14, 30).toISOString(),
    end: createMockDate(15, 30).toISOString(),
    durationSlots: 2,
    status: "CONFIRMADA",
    treatment: "Implante"
  },
  
  // Doctor 3 - Dr. García
  {
    id: "7",
    patientId: "107",
    patientName: "Laura Díaz",
    doctorId: "3",
    start: createMockDate(10, 0).toISOString(),
    end: createMockDate(11, 0).toISOString(),
    durationSlots: 2,
    status: "COMPLETADA",
    treatment: "Empaste"
  },
  {
    id: "8",
    patientId: "108",
    patientName: "Javier Sánchez",
    doctorId: "3",
    start: createMockDate(13, 0).toISOString(),
    end: createMockDate(14, 0).toISOString(),
    durationSlots: 2,
    status: "EN_CURSO",
    treatment: "Revisión de Brackets"
  },
  {
    id: "9",
    patientId: "109",
    patientName: "Sofía Hernández",
    doctorId: "3",
    start: createMockDate(16, 0).toISOString(),
    end: createMockDate(17, 0).toISOString(),
    durationSlots: 2,
    status: "PENDIENTE",
    treatment: "Radiografía"
  },
  
  // Doctor 4 - Dra. López
  {
    id: "10",
    patientId: "110",
    patientName: "Roberto Fernández",
    doctorId: "4",
    start: createMockDate(9, 0).toISOString(),
    end: createMockDate(10, 0).toISOString(),
    durationSlots: 2,
    status: "COMPLETADA",
    treatment: "Consulta Inicial"
  },
  {
    id: "11",
    patientId: "111",
    patientName: "Carmen Gómez",
    doctorId: "4",
    start: createMockDate(12, 30).toISOString(),
    end: createMockDate(13, 30).toISOString(),
    durationSlots: 2,
    status: "CONFIRMADA",
    treatment: "Endodoncia"
  },
  {
    id: "12",
    patientId: "112",
    patientName: "Miguel Torres",
    doctorId: "4",
    start: createMockDate(15, 30).toISOString(),
    end: createMockDate(16, 30).toISOString(),
    durationSlots: 2,
    status: "PENDIENTE",
    treatment: "Limpieza"
  },
  
  // Doctor 5 - Dr. Hernández
  {
    id: "13",
    patientId: "113",
    patientName: "Isabel Ramírez",
    doctorId: "5",
    start: createMockDate(10, 30).toISOString(),
    end: createMockDate(11, 30).toISOString(),
    durationSlots: 2,
    status: "COMPLETADA",
    treatment: "Consulta Pediátrica"
  },
  {
    id: "14",
    patientId: "114",
    patientName: "Daniel Ortiz",
    doctorId: "5",
    start: createMockDate(13, 30).toISOString(),
    end: createMockDate(14, 30).toISOString(),
    durationSlots: 2,
    status: "EN_CURSO",
    treatment: "Sellado de Fisuras"
  },
  {
    id: "15",
    patientId: "115",
    patientName: "Lucía Domínguez",
    doctorId: "5",
    start: createMockDate(16, 30).toISOString(),
    end: createMockDate(17, 30).toISOString(),
    durationSlots: 2,
    status: "PENDIENTE",
    treatment: "Fluorización"
  }
];

function SchedulePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const patientId = searchParams.get('patientId');
  
  // Inicializar con el 16 de mayo de 2025 (viernes)
  const [date, setDate] = useState(new Date(2025, 4, 16));
  const [viewMode, setViewMode] = useState('daily');
  const [doctors, setDoctors] = useState(MOCK_DOCTORS);
  const [appointments, setAppointments] = useState(MOCK_APPOINTMENTS);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [loading, setLoading] = useState(false);  // Cambiado a false para mostrar datos mock
  const [error, setError] = useState(null);
  // Estado para la vista alternativa del calendario (estilo moderno) - activada por defecto
  const [showModernView, setShowModernView] = useState(true);
  
  // Estado para modales - asegurar que todos los estados de modales estén inicialmente a false
  const [showNewModal, setShowNewModal] = useState(false);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [currentAppointment, setCurrentAppointment] = useState(null);
  
  // Forzar el cierre de todos los modales al inicio del montaje del componente
  useLayoutEffect(() => {
    setShowNewModal(false);
    setShowDetailModal(false);
    setShowDeleteConfirm(false);
    setCurrentAppointment(null);
    
    console.log('Estado inicial de modales restablecido');
  }, []);
  
  // Agregar un useEffect adicional para depuración
  useEffect(() => {
    if (showDeleteConfirm) {
      console.log('showDeleteConfirm cambió a true', { 
        currentAppointment, 
        showDetailModal,
        showNewModal 
      });
    }
  }, [showDeleteConfirm]);
  
  // Modificar la función que establece showDeleteConfirm para incluir debug y validación
  const openDeleteConfirmDialog = () => {
    if (!currentAppointment || !currentAppointment.id) {
      console.error('Intento de abrir modal de eliminación sin cita válida');
      return;
    }
    
    console.log('Intentando abrir el modal de confirmación de eliminación', { 
      currentAppointment 
    });
    setShowDeleteConfirm(true);
  };

  // Reemplazado el useEffect para usar datos simulados
  useEffect(() => {
    // Comentado el código que hace llamadas a la API real
    /*
    const fetchData = async () => {
      setLoading(true);
      try {
        const [appointmentsData, doctorsData] = await Promise.all([
          getAppointments(),
          getDoctors()
        ]);
        
        setAppointments(appointmentsData.map(appt => ({
          ...appt,
          patientName: appt.patientName || `Paciente ${Math.floor(Math.random() * 5) + 1}`,
          status: appt.status || ['PENDIENTE', 'CONFIRMADA', 'COMPLETADA', 'EN_CURSO'][Math.floor(Math.random() * 4)],
          treatment: appt.treatment || ['General Checkup', 'Scaling', 'Extraction', 'Bleaching'][Math.floor(Math.random() * 4)],
          durationSlots: appt.durationSlots || 1
        })));
        
        setDoctors(doctorsData);
        
        if (doctorsData.length > 0 && !selectedDoctor) {
          setSelectedDoctor(doctorsData[0].id);
        }
        
        // Asegurarnos de que los modales estén cerrados inicialmente
        setShowNewModal(false);
        setShowDetailModal(false);
        setShowDeleteConfirm(false);
        setCurrentAppointment(null);
      } catch (err) {
        console.error("Error fetching data:", err);
        setError("Error al cargar los datos. Por favor, intenta de nuevo más tarde.");
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
    */
    
    // Usar datos simulados directamente
    console.log("Cargando datos simulados:", MOCK_APPOINTMENTS.length, "citas y", MOCK_DOCTORS.length, "doctores");
    
    // Podemos actualizar cualquier estado si es necesario
    setLoading(false);
  }, []);
  
  // Navegación de fechas
  const handlePrevDay = () => {
    const newDate = new Date(date);
    newDate.setDate(date.getDate() - 1);
    setDate(newDate);
  };
  
  const handleNextDay = () => {
    const newDate = new Date(date);
    newDate.setDate(date.getDate() + 1);
    setDate(newDate);
  };
  
  const handlePrevWeek = () => {
    const newDate = new Date(date);
    newDate.setDate(date.getDate() - 7);
    setDate(newDate);
  };
  
  const handleNextWeek = () => {
    const newDate = new Date(date);
    newDate.setDate(date.getDate() + 7);
    setDate(newDate);
  };
  
  const handlePrevMonth = () => {
    const newDate = new Date(date);
    newDate.setMonth(date.getMonth() - 1);
    setDate(newDate);
  };
  
  const handleNextMonth = () => {
    const newDate = new Date(date);
    newDate.setMonth(date.getMonth() + 1);
    setDate(newDate);
  };
  
  const handleToday = () => {
    setDate(new Date());
  };
  
  // Gestión de citas
  const handleNewAppointment = (appointmentData) => {
    if (!appointmentData || !appointmentData.doctorId || !appointmentData.start) {
      console.log('Datos de cita incompletos o inválidos', appointmentData);
      return;
    }
    
    setCurrentAppointment({
      ...appointmentData,
      id: null,
      patientId: null,
      doctorId: appointmentData.doctorId,
      start: appointmentData.start,
      status: 'PENDIENTE'
    });
    setShowNewModal(true);
  };
  
  const handleAppointmentClick = (appointment) => {
    if (!appointment || !appointment.id) {
      console.log('Cita inválida o sin ID', appointment);
      return;
    }
    
    setCurrentAppointment(appointment);
    setShowDetailModal(true);
  };
  
  const handleCreateAppointment = async (formData) => {
    try {
      const newAppointment = await createAppointment(formData);
      setAppointments(prev => [...prev, newAppointment]);
      setShowNewModal(false);
    } catch (err) {
      console.error("Error creating appointment:", err);
      // Mostrar error en la UI
    }
  };
  
  const handleUpdateAppointment = async (formData) => {
    try {
      const updatedAppointment = await updateAppointment(formData.id, formData);
      setAppointments(prev => prev.map(appt => appt.id === formData.id ? updatedAppointment : appt));
      setShowDetailModal(false);
    } catch (err) {
      console.error("Error updating appointment:", err);
      // Mostrar error en la UI
    }
  };
  
  const handleDeleteAppointment = async () => {
    if (!currentAppointment) {
      setShowDeleteConfirm(false);
      return;
    }
    
    try {
      await deleteAppointment(currentAppointment.id);
      setAppointments(prev => prev.filter(appt => appt.id !== currentAppointment.id));
      setShowDetailModal(false);
      setShowDeleteConfirm(false);
      setCurrentAppointment(null);
    } catch (err) {
      console.error("Error deleting appointment:", err);
      // Mostrar error en la UI
    }
  };
  
  const handleDoctorChange = (e) => {
    setSelectedDoctor(e.target.value);
  };
  
  // Formateado de fecha para el encabezado
  const formattedDate = new Intl.DateTimeFormat('en-US', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  }).format(date);
  
  const appointmentsCount = appointments.filter(appointment => {
    const appointmentDate = new Date(appointment.start);
    return appointmentDate.toDateString() === date.toDateString();
  }).length;

  // Asegurarnos de limpiar currentAppointment cuando cerramos los modales
  const closeNewModal = () => {
    setShowNewModal(false);
    if (!showDetailModal && !showDeleteConfirm) {
      setCurrentAppointment(null);
    }
  };

  const closeDetailModal = () => {
    setShowDetailModal(false);
    if (!showNewModal && !showDeleteConfirm) {
      setCurrentAppointment(null);
    }
  };

  const closeDeleteConfirmModal = () => {
    setShowDeleteConfirm(false);
    if (!showNewModal && !showDetailModal) {
      setCurrentAppointment(null);
    }
  };

  // Toggle entre vistas
  const toggleView = () => {
    setShowModernView(!showModernView);
  };

  return (
    <div className="bg-white rounded-md">
      {/* Tabs de navegación */}
      <div className="flex border-b border-gray-200">
        <button
          className={`py-2 px-4 text-sm font-medium ${viewMode === 'daily' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700 hover:border-gray-300'}`}
          onClick={() => setViewMode('daily')}
        >
          Diario
        </button>
        <button
          className={`py-2 px-4 text-sm font-medium ${viewMode === 'weekly' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700 hover:border-gray-300'}`}
          onClick={() => setViewMode('weekly')}
        >
          Semanal
        </button>
        <button
          className={`py-2 px-4 text-sm font-medium ${viewMode === 'monthly' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700 hover:border-gray-300'}`}
          onClick={() => setViewMode('monthly')}
        >
          Mensual
        </button>
        
        {/* Agregar botón para cambiar entre vistas */}
        <div className="ml-auto mr-4">
          <Button 
            variant="outline" 
            onClick={toggleView} 
            className="text-sm"
          >
            {showModernView ? "Vista Clásica" : "Vista Moderna"}
          </Button>
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <p className="ml-2 text-gray-600">Cargando horario...</p>
        </div>
      ) : error ? (
        <div className="p-4 bg-red-50 text-red-700 rounded-md">
          {error}
        </div>
      ) : showModernView ? (
        // Vista moderna del calendario (usando el CalendarView con datos de ejemplo)
        <CalendarView />
      ) : viewMode === 'daily' && doctors.length > 0 ? (
        // Vista clásica del DailySchedule
        <div className="p-4">
          {/* Encabezado del calendario */}
          <div className="flex items-center justify-between py-4">
            <div className="flex items-center gap-2">
              <div className="flex items-center justify-center bg-gray-100 p-2 rounded-md">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-gray-700" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <span className="ml-2 font-semibold">{appointmentsCount}</span>
              </div>
              <div className="text-sm text-gray-500">total appointments</div>
            </div>

            <div className="flex items-center gap-2">
              <Button variant="outline" onClick={handleToday} className="rounded-full px-4 text-sm">
                Today
              </Button>
              <button className="h-8 w-8 flex items-center justify-center rounded-full hover:bg-gray-100" onClick={viewMode === 'daily' ? handlePrevDay : handlePrevWeek}>
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                </svg>
              </button>
              <div className="font-medium">{formattedDate}</div>
              <button className="h-8 w-8 flex items-center justify-center rounded-full hover:bg-gray-100" onClick={viewMode === 'daily' ? handleNextDay : handleNextWeek}>
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
              </button>
            </div>

            <div className="flex items-center gap-4">
              <div className="flex border rounded-md overflow-hidden">
                <button
                  className={`px-3 py-1 text-sm ${viewMode === 'daily' ? 'bg-blue-600 text-white' : 'bg-white text-gray-700'}`}
                  onClick={() => setViewMode('daily')}
                >
                  Day
                </button>
                <button
                  className={`px-3 py-1 text-sm ${viewMode === 'weekly' ? 'bg-blue-600 text-white' : 'bg-white text-gray-700'}`}
                  onClick={() => setViewMode('weekly')}
                >
                  Week
                </button>
              </div>

              <select
                value={selectedDoctor || ''}
                onChange={handleDoctorChange}
                className="w-[180px] bg-white border border-gray-300 rounded-md px-3 py-1 text-sm"
              >
                <option value="">Todos los Doctores</option>
                {doctors.map(doctor => (
                  <option key={doctor.id} value={doctor.id}>
                    {doctor.nombreCompleto}
                  </option>
                ))}
              </select>

              <Button variant="outline" className="flex items-center gap-2 text-sm">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                </svg>
                Filters
              </Button>
            </div>
          </div>
          
          <DailySchedule 
            date={date}
            doctors={selectedDoctor ? doctors.filter(d => d.id === selectedDoctor) : doctors}
            appointments={appointments || []} 
            onNewAppointment={handleNewAppointment}
            onEditAppointment={handleAppointmentClick}
          />
        </div>
      ) : viewMode === 'weekly' && doctors.length > 0 ? (
        <div className="p-4">
          {/* Header similar para vista semanal */}
          {/* ... (código similar al encabezado diario) ... */}
          <WeeklySchedule 
            week={date}
            doctor={selectedDoctor ? doctors.find(d => d.id === selectedDoctor) : null}
            appointments={appointments || []}
            onNewAppointment={handleNewAppointment}
            onEditAppointment={handleAppointmentClick}
          />
        </div>
      ) : (
        <div className="text-center p-8 text-gray-500">
          {doctors.length === 0 ? 'No hay doctores disponibles' : 'Vista mensual en desarrollo'}
        </div>
      )}

      {/* Modal para nueva cita */}
      <Modal 
        isOpen={showNewModal && currentAppointment !== null} 
        onClose={closeNewModal}
        title="Nueva Cita"
      >
        {currentAppointment && (
          <AppointmentForm
            appointment={currentAppointment}
            doctors={doctors}
            onSubmit={handleCreateAppointment}
            onCancel={closeNewModal}
          />
        )}
      </Modal>

      {/* Modal para detalles/edición de cita */}
      <Modal
        isOpen={showDetailModal && currentAppointment !== null}
        onClose={closeDetailModal}
        title="Detalles de la Cita"
      >
        {currentAppointment && (
          <AppointmentDetail
            appointment={currentAppointment}
            onEdit={handleUpdateAppointment}
            onDelete={openDeleteConfirmDialog}
            onClose={closeDetailModal}
            doctors={doctors}
          />
        )}
      </Modal>

      {/* Modal de confirmación para eliminar */}
      <Modal
        isOpen={showDeleteConfirm && currentAppointment !== null}
        onClose={closeDeleteConfirmModal}
        title="Confirmar Eliminación"
      >
        <div className="p-4">
          <p className="mb-4">¿Estás seguro de que deseas eliminar esta cita?</p>
          <div className="flex justify-end space-x-2">
            <Button
              variant="secondary"
              onClick={closeDeleteConfirmModal}
            >
              Cancelar
            </Button>
            <Button
              variant="danger"
              onClick={handleDeleteAppointment}
            >
              Eliminar
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

export default SchedulePage; 