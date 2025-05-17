import React, { useEffect, useState, useLayoutEffect } from 'react';
import { getAppointments, getDoctors, createAppointment, updateAppointment, deleteAppointment } from '../services/api';
import { useLocation, useNavigate } from 'react-router-dom';
import { useSearchParams } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import AppointmentForm from '../components/appointment/AppointmentForm';
import AppointmentDetail from '../components/appointment/AppointmentDetail';
import CalendarView from '../components/calendar/calendar-view';

function SchedulePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const patientId = searchParams.get('patientId');
  
  // Inicializar con el 16 de mayo de 2025 (viernes)
  const [date, setDate] = useState(new Date(2025, 4, 16));
  const [viewType, setViewType] = useState('day'); // 'day' o 'week'
  const [doctors, setDoctors] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [loading, setLoading] = useState(false); // Cambiado a false para mostrar datos mock
  const [error, setError] = useState(null);
  
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

  useEffect(() => {
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
    setDate(new Date(2025, 4, 16)); // Fijar a 16 de mayo de 2025 para demostración
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
  
  // Formatear fecha para mostrar al estilo "Mon, May 16, 2022"
  const formattedDateEnglish = new Intl.DateTimeFormat('en-US', {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  }).format(date);

  // Contar citas para hoy
  const todayAppointments = appointments.filter(appointment => {
    const apptDate = new Date(appointment.start);
    return apptDate.toDateString() === date.toDateString();
  });

  // Contar el número total de citas (usaremos un número fijo para demostración)
  const totalAppointments = 16; // En la imagen se muestra 16 como el número total de citas

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

  return (
    <div className="bg-white rounded-md h-full flex flex-col">
      {/* Eliminamos la barra de navegación duplicada y dejamos solo la que viene del layout principal */}
      
      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <p className="ml-2 text-gray-600">Cargando horario...</p>
        </div>
      ) : error ? (
        <div className="p-4 bg-red-50 text-red-700 rounded-md">
          {error}
        </div>
      ) : (
        <div className="flex flex-col flex-grow overflow-hidden">
          {/* Nueva barra superior de calendario según el diseño proporcionado - ahora con posición sticky */}
          <div className="sticky top-0 z-50 px-6 py-4 flex justify-between items-center border-b border-gray-200 bg-white shadow-sm">
            {/* Contador de citas a la izquierda */}
            <div className="flex items-center">
              <div className="flex items-center">
                <div className="flex items-center justify-center bg-gray-50 border border-gray-200 rounded-md p-1.5 mr-3">
                  <svg className="w-5 h-5 text-gray-500 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span className="font-semibold text-gray-700">{totalAppointments}</span>
                </div>
                <span className="text-gray-500 text-sm">total appointments</span>
              </div>
            </div>

            {/* Navegación y fecha en el centro */}
            <div className="flex items-center space-x-4">
              <button
                onClick={handleToday}
                className="px-4 py-2 bg-white border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                Today
              </button>
              
              <button
                onClick={viewType === 'day' ? handlePrevDay : handlePrevWeek}
                className="p-1.5 rounded-md hover:bg-gray-100"
              >
                <svg className="w-5 h-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" />
                </svg>
              </button>
              
              <span className="text-gray-800 font-medium">{formattedDateEnglish}</span>
              
              <button
                onClick={viewType === 'day' ? handleNextDay : handleNextWeek}
                className="p-1.5 rounded-md hover:bg-gray-100"
              >
                <svg className="w-5 h-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7" />
                </svg>
              </button>
            </div>

            {/* Botones a la derecha */}
            <div className="flex items-center space-x-2">
              <div className="flex rounded-md overflow-hidden border border-gray-300">
                <button
                  onClick={() => setViewType('day')}
                  className={`px-4 py-2 text-sm font-medium ${
                    viewType === 'day' 
                      ? 'bg-blue-600 text-white' 
                      : 'bg-white text-gray-700 hover:bg-gray-50'
                  }`}
                >
                  Day
                </button>
                <button
                  onClick={() => setViewType('week')}
                  className={`px-4 py-2 text-sm font-medium ${
                    viewType === 'week' 
                      ? 'bg-blue-600 text-white' 
                      : 'bg-white text-gray-700 hover:bg-gray-50'
                  }`}
                >
                  Week
                </button>
              </div>
              
              {/* Selector de doctores */}
              <div className="relative">
                <select
                  value={selectedDoctor || ''}
                  onChange={handleDoctorChange}
                  className="appearance-none bg-white border border-gray-300 rounded-md pl-3 pr-8 py-2 text-sm font-medium text-gray-700 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="">Todos los Doctores</option>
                  {doctors.map(doctor => (
                    <option key={doctor.id} value={doctor.id}>
                      {doctor.nombreCompleto || `Doctor ${doctor.id}`}
                    </option>
                  ))}
                </select>
                <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                  <svg className="h-4 w-4" fill="none" viewBox="0 0 20 20" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M7 7l3-3 3 3m0 6l-3 3-3-3" />
                  </svg>
                </div>
              </div>
              
              {/* Botón de filtros */}
              <button className="flex items-center px-3 py-2 border border-gray-300 rounded-md bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                <svg className="w-4 h-4 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                </svg>
                Filtros
              </button>
            </div>
          </div>
          
          {/* Contenido del calendario con scroll - ahora con overflow-auto */}
          <div className="flex-grow overflow-auto">
            <CalendarView />
          </div>
        </div>
      )}

      {/* Modales - no cambian */}
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