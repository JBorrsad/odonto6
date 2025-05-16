import React, { useEffect, useState } from 'react';
import { getAppointments, getDoctors, createAppointment, updateAppointment, deleteAppointment } from '../services/api';
import { useLocation, useNavigate } from 'react-router-dom';
import { useSearchParams } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import AppointmentForm from '../components/appointment/AppointmentForm';
import AppointmentDetail from '../components/appointment/AppointmentDetail';

// Componente para la vista diaria
function DailySchedule({ date, appointments, doctors, onAppointmentClick }) {
  const businessHours = [];
  for (let i = 8; i < 20; i++) {
    businessHours.push(`${i}:00`);
    businessHours.push(`${i}:30`);
  }

  const formatTime = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return 'bg-green-100 text-green-800 border-green-500';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800 border-yellow-500';
      case 'WAITING_ROOM':
        return 'bg-blue-100 text-blue-800 border-blue-500';
      case 'IN_PROGRESS':
        return 'bg-purple-100 text-purple-800 border-purple-500';
      case 'COMPLETED':
        return 'bg-gray-100 text-gray-800 border-gray-500';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800 border-red-500';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-500';
    }
  };

  const getAppointmentsByHourAndDoctor = (hour, doctorId) => {
    return appointments.filter(appointment => {
      const startTime = new Date(appointment.start);
      const hourStr = `${startTime.getHours()}:${startTime.getMinutes() === 0 ? '00' : '30'}`;
      return hourStr === hour && appointment.doctorId === doctorId;
    });
  };

  const getAppointmentDuration = (appointment) => {
    return appointment.durationSlots || 1;
  };

  return (
    <div className="daily-schedule">
      <h2 className="text-xl font-semibold mb-4">
        Horario del día {date.toLocaleDateString('es-ES')}
      </h2>
      
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-20">
                Hora
              </th>
              {doctors.map(doctor => (
                <th key={doctor.id} className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Dr. {doctor.nombreCompleto}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {businessHours.map((hour, index) => (
              <tr key={hour} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 border-r">
                  {hour}
                </td>
                {doctors.map(doctor => {
                  const appts = getAppointmentsByHourAndDoctor(hour, doctor.id);
                  
                  // Si ya hay una cita que ocupa esta celda desde una fila anterior, no mostramos nada
                  const isOccupiedFromAbove = appointments.some(appointment => {
                    if (appointment.doctorId !== doctor.id) return false;
                    
                    const startTime = new Date(appointment.start);
                    const appointmentHour = `${startTime.getHours()}:${startTime.getMinutes() === 0 ? '00' : '30'}`;
                    
                    // Encontrar el índice de la hora de inicio en businessHours
                    const startIndex = businessHours.indexOf(appointmentHour);
                    if (startIndex === -1) return false;
                    
                    // Duración en bloques de 30 min
                    const duration = getAppointmentDuration(appointment);
                    
                    // Verificar si la hora actual está dentro del rango de la cita
                    const currentIndex = businessHours.indexOf(hour);
                    return currentIndex > startIndex && currentIndex < startIndex + duration;
                  });
                  
                  if (isOccupiedFromAbove) {
                    return <td key={`${doctor.id}-${hour}`} className="px-6 py-4"></td>;
                  }
                  
                  return (
                    <td key={`${doctor.id}-${hour}`} className="px-6 py-4 border-r">
                      {appts.length > 0 ? appts.map(appointment => {
                        const duration = getAppointmentDuration(appointment);
                        
                        return (
                          <div 
                            key={appointment.id} 
                            className={`p-2 rounded border-l-4 mb-1 cursor-pointer hover:shadow ${getStatusClass(appointment.status)}`}
                            style={{ height: `${duration * 35}px` }}
                            onClick={() => onAppointmentClick(appointment)}
                          >
                            <div className="font-medium">{appointment.patientName || 'Paciente'}</div>
                            <div className="text-xs">
                              {formatTime(appointment.start)} - {duration * 30} min
                            </div>
                            <div className="text-xs mt-1">{appointment.treatment || 'Consulta'}</div>
                          </div>
                        );
                      }) : null}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// Componente para la vista semanal
function WeeklySchedule({ selectedDate, appointments, doctors, onAppointmentClick }) {
  const [selectedDoctor, setSelectedDoctor] = useState(doctors[0]?.id || '');
  
  // Generar los días de la semana
  const getWeekDays = (date) => {
    const curr = new Date(date);
    const first = curr.getDate() - curr.getDay();
    
    const days = [];
    for (let i = 0; i < 7; i++) {
      const day = new Date(curr);
      day.setDate(first + i);
      days.push(day);
    }
    return days;
  };
  
  const weekDays = getWeekDays(selectedDate);
  
  const formatDate = (date) => {
    return date.toLocaleDateString('es-ES', { weekday: 'short', day: 'numeric' });
  };
  
  const formatTime = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
  };
  
  const getStatusClass = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return 'bg-green-100 text-green-800 border-green-500';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800 border-yellow-500';
      case 'WAITING_ROOM':
        return 'bg-blue-100 text-blue-800 border-blue-500';
      case 'IN_PROGRESS':
        return 'bg-purple-100 text-purple-800 border-purple-500';
      case 'COMPLETED':
        return 'bg-gray-100 text-gray-800 border-gray-500';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800 border-red-500';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-500';
    }
  };
  
  const getAppointmentsForDayAndDoctor = (day, doctorId) => {
    return appointments.filter(appointment => {
      const appointmentDate = new Date(appointment.start);
      return (
        appointmentDate.toDateString() === day.toDateString() && 
        appointment.doctorId === doctorId
      );
    });
  };
  
  return (
    <div className="weekly-schedule">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">
          Semana del {weekDays[0].toLocaleDateString('es-ES')} al {weekDays[6].toLocaleDateString('es-ES')}
        </h2>
        
        <select 
          className="border rounded p-2"
          value={selectedDoctor}
          onChange={(e) => setSelectedDoctor(e.target.value)}
        >
          <option value="">Seleccionar doctor</option>
          {doctors.map(doctor => (
            <option key={doctor.id} value={doctor.id}>
              Dr. {doctor.nombreCompleto}
            </option>
          ))}
        </select>
      </div>
      
      {selectedDoctor ? (
        <div className="grid grid-cols-7 gap-2">
          {weekDays.map(day => (
            <div key={day.toISOString()} className="border rounded">
              <div className="p-2 bg-gray-100 font-medium text-center border-b">
                {formatDate(day)}
              </div>
              <div className="p-2 h-64 overflow-y-auto">
                {getAppointmentsForDayAndDoctor(day, selectedDoctor).length > 0 ? (
                  getAppointmentsForDayAndDoctor(day, selectedDoctor).map(appointment => (
                    <div 
                      key={appointment.id} 
                      className={`p-2 text-sm rounded border-l-4 mb-2 cursor-pointer hover:shadow ${getStatusClass(appointment.status)}`}
                      onClick={() => onAppointmentClick(appointment)}
                    >
                      <div className="font-medium">{appointment.patientName || 'Paciente'}</div>
                      <div className="text-xs">{formatTime(appointment.start)}</div>
                    </div>
                  ))
                ) : (
                  <div className="text-center text-gray-500 text-sm py-4">
                    No hay citas
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center py-8 text-gray-500">
          Selecciona un doctor para ver su agenda semanal
        </div>
      )}
    </div>
  );
}

// Componente para la vista mensual
function CalendarView({ selectedDate, appointments, doctors, onDateSelect }) {
  const [selectedDoctor, setSelectedDoctor] = useState('');
  
  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    
    // Primer día del mes
    const firstDay = new Date(year, month, 1);
    // Último día del mes
    const lastDay = new Date(year, month + 1, 0);
    
    // Día de la semana del primer día (0 = Domingo, 1 = Lunes, etc.)
    const firstDayOfWeek = firstDay.getDay();
    // Total de días en el mes
    const daysInMonth = lastDay.getDate();
    
    // Array para almacenar todos los días que se mostrarán
    const days = [];
    
    // Días del mes anterior para completar la primera semana
    const prevMonthLastDay = new Date(year, month, 0).getDate();
    for (let i = firstDayOfWeek - 1; i >= 0; i--) {
      const day = new Date(year, month - 1, prevMonthLastDay - i);
      days.push({ date: day, isCurrentMonth: false });
    }
    
    // Días del mes actual
    for (let i = 1; i <= daysInMonth; i++) {
      const day = new Date(year, month, i);
      days.push({ date: day, isCurrentMonth: true });
    }
    
    // Días del mes siguiente para completar la última semana
    const remainingDays = 42 - days.length; // 6 semanas * 7 días = 42
    for (let i = 1; i <= remainingDays; i++) {
      const day = new Date(year, month + 1, i);
      days.push({ date: day, isCurrentMonth: false });
    }
    
    return days;
  };
  
  const filteredAppointments = selectedDoctor
    ? appointments.filter(appt => appt.doctorId === selectedDoctor)
    : appointments;
  
  const getAppointmentsForDay = (day) => {
    return filteredAppointments.filter(appointment => {
      const appointmentDate = new Date(appointment.start);
      return appointmentDate.toDateString() === day.toDateString();
    });
  };
  
  const days = getDaysInMonth(selectedDate);
  const weekDays = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
  
  return (
    <div className="calendar-view">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">
          {selectedDate.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })}
        </h2>
        
        <select 
          className="border rounded p-2"
          value={selectedDoctor}
          onChange={(e) => setSelectedDoctor(e.target.value)}
        >
          <option value="">Todos los doctores</option>
          {doctors.map(doctor => (
            <option key={doctor.id} value={doctor.id}>
              Dr. {doctor.nombreCompleto}
            </option>
          ))}
        </select>
      </div>
      
      <div className="grid grid-cols-7 gap-1">
        {weekDays.map(day => (
          <div key={day} className="p-2 text-center font-medium bg-gray-100">
            {day}
          </div>
        ))}
        
        {days.map((day, index) => {
          const dayAppointments = getAppointmentsForDay(day.date);
          const isToday = day.date.toDateString() === new Date().toDateString();
          const isSelected = day.date.toDateString() === selectedDate.toDateString();
          
          return (
            <div 
              key={index} 
              className={`p-1 border min-h-[80px] ${
                day.isCurrentMonth ? 'bg-white' : 'bg-gray-50 text-gray-400'
              } ${isToday ? 'border-blue-500 border-2' : ''}
              ${isSelected ? 'bg-blue-50' : ''}
              cursor-pointer hover:bg-blue-50`}
              onClick={() => onDateSelect(day.date)}
            >
              <div className="text-right p-1">
                {day.date.getDate()}
              </div>
              
              {dayAppointments.length > 0 && (
                <div className="p-1">
                  {dayAppointments.length > 3 
                    ? <div className="text-xs bg-blue-100 p-1 rounded text-center">{dayAppointments.length} citas</div>
                    : dayAppointments.map(appointment => (
                      <div key={appointment.id} className="text-xs p-1 truncate bg-blue-100 rounded mb-1">
                        {appointment.patientName || 'Paciente'}
                      </div>
                    ))
                  }
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

function SchedulePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const patientId = searchParams.get('patientId');
  
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [viewMode, setViewMode] = useState('daily');
  const [selectedDate, setSelectedDate] = useState(new Date());
  
  const [showAppointmentModal, setShowAppointmentModal] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [formLoading, setFormLoading] = useState(false);
  
  useEffect(() => {
    fetchData();
  }, [selectedDate]);
  
  const fetchData = async () => {
    try {
      setLoading(true);
      
      // Formatear la fecha para filtrar citas
      const dateQuery = selectedDate.toISOString().split('T')[0];
      
      const [appointmentsData, doctorsData] = await Promise.all([
        getAppointments(new Date(dateQuery)),
        getDoctors()
      ]);
      
      // Enriquecer citas con nombres de pacientes y doctores para mostrar
      const enrichedAppointments = appointmentsData.map(appointment => {
        return {
          ...appointment,
          // Estos campos deberían venir del backend, pero los simulamos para la UI
          patientName: `Paciente ${appointment.patientId?.substring(0, 5)}`,
          doctorName: `Dr. ${appointment.doctorId?.substring(0, 5)}`
        };
      });
      
      setAppointments(enrichedAppointments);
      setDoctors(doctorsData);
    } catch (err) {
      setError(err.message || "Error al cargar datos");
    } finally {
      setLoading(false);
    }
  };
  
  const handleViewChange = (mode) => {
    setViewMode(mode);
  };
  
  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };
  
  const handlePrevDay = () => {
    const newDate = new Date(selectedDate);
    newDate.setDate(newDate.getDate() - 1);
    setSelectedDate(newDate);
  };
  
  const handleNextDay = () => {
    const newDate = new Date(selectedDate);
    newDate.setDate(newDate.getDate() + 1);
    setSelectedDate(newDate);
  };
  
  const handlePrevWeek = () => {
    const newDate = new Date(selectedDate);
    newDate.setDate(newDate.getDate() - 7);
    setSelectedDate(newDate);
  };
  
  const handleNextWeek = () => {
    const newDate = new Date(selectedDate);
    newDate.setDate(newDate.getDate() + 7);
    setSelectedDate(newDate);
  };
  
  const handlePrevMonth = () => {
    const newDate = new Date(selectedDate);
    newDate.setMonth(newDate.getMonth() - 1);
    setSelectedDate(newDate);
  };
  
  const handleNextMonth = () => {
    const newDate = new Date(selectedDate);
    newDate.setMonth(newDate.getMonth() + 1);
    setSelectedDate(newDate);
  };
  
  const handleNewAppointment = () => {
    setSelectedAppointment(null);
    setShowAppointmentModal(true);
  };
  
  const handleAppointmentClick = (appointment) => {
    setSelectedAppointment(appointment);
    setShowDetailsModal(true);
  };
  
  const handleAppointmentUpdate = (updatedAppointment) => {
    // Actualizar la cita en la lista
    setAppointments(
      appointments.map(appt => 
        appt.id === updatedAppointment.id ? updatedAppointment : appt
      )
    );
    setShowDetailsModal(false);
  };
  
  const handleSubmitAppointment = async (appointmentData) => {
    setFormLoading(true);
    try {
      let result;
      
      if (selectedAppointment) {
        // Actualizar cita existente
        result = await updateAppointment(selectedAppointment.id, appointmentData);
        
        // Actualizar lista local
        setAppointments(
          appointments.map(appt => 
            appt.id === selectedAppointment.id ? result : appt
          )
        );
      } else {
        // Crear nueva cita
        result = await createAppointment(appointmentData);
        
        // Añadir a lista local
        setAppointments([...appointments, result]);
      }
      
      setShowAppointmentModal(false);
      
    } catch (err) {
      setError(err.message || "Error al guardar la cita");
    } finally {
      setFormLoading(false);
    }
  };
  
  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Horario</h1>
        <Button onClick={handleNewAppointment} variant="primary">
          Nueva Cita
        </Button>
      </div>
      
      {error && (
        <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-500 text-red-700">
          <p>{error}</p>
        </div>
      )}
      
      <Card className="mb-6">
        <div className="p-4">
          <div className="flex flex-wrap items-center justify-between">
            <div className="flex space-x-2 mb-4 md:mb-0">
              <button
                onClick={() => handleViewChange('daily')}
                className={`px-3 py-1 rounded ${viewMode === 'daily' ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
              >
                Diario
              </button>
              <button
                onClick={() => handleViewChange('weekly')}
                className={`px-3 py-1 rounded ${viewMode === 'weekly' ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
              >
                Semanal
              </button>
              <button
                onClick={() => handleViewChange('monthly')}
                className={`px-3 py-1 rounded ${viewMode === 'monthly' ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
              >
                Mensual
              </button>
            </div>
            
            <div className="flex items-center space-x-2">
              {viewMode === 'daily' && (
                <>
                  <button
                    onClick={handlePrevDay}
                    className="p-1 hover:bg-gray-100 rounded"
                  >
                    &lt;
                  </button>
                  <span className="font-medium">Hoy: {selectedDate.toLocaleDateString()}</span>
                  <button
                    onClick={handleNextDay}
                    className="p-1 hover:bg-gray-100 rounded"
                  >
                    &gt;
                  </button>
                </>
              )}
              
              {viewMode === 'weekly' && (
                <>
                  <button
                    onClick={handlePrevWeek}
                    className="p-1 hover:bg-gray-100 rounded"
                  >
                    &lt;
                  </button>
                  <span className="font-medium">Semana de {selectedDate.toLocaleDateString()}</span>
                  <button
                    onClick={handleNextWeek}
                    className="p-1 hover:bg-gray-100 rounded"
                  >
                    &gt;
                  </button>
                </>
              )}
              
              {viewMode === 'monthly' && (
                <>
                  <button
                    onClick={handlePrevMonth}
                    className="p-1 hover:bg-gray-100 rounded"
                  >
                    &lt;
                  </button>
                  <span className="font-medium">
                    {selectedDate.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })}
                  </span>
                  <button
                    onClick={handleNextMonth}
                    className="p-1 hover:bg-gray-100 rounded"
                  >
                    &gt;
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      </Card>
      
      {loading ? (
        <div className="text-center py-10">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
          <p className="mt-2">Cargando datos...</p>
        </div>
      ) : (
        <Card>
          <div className="p-4">
            {viewMode === 'daily' && (
              <DailySchedule 
                date={selectedDate} 
                appointments={appointments} 
                doctors={doctors}
                onAppointmentClick={handleAppointmentClick}
              />
            )}
            
            {viewMode === 'weekly' && (
              <WeeklySchedule 
                selectedDate={selectedDate} 
                appointments={appointments} 
                doctors={doctors}
                onAppointmentClick={handleAppointmentClick}
              />
            )}
            
            {viewMode === 'monthly' && (
              <CalendarView 
                selectedDate={selectedDate} 
                appointments={appointments} 
                doctors={doctors}
                onDateSelect={handleDateSelect}
              />
            )}
          </div>
        </Card>
      )}
      
      {showAppointmentModal && (
        <Modal
          title={selectedAppointment ? "Editar Cita" : "Nueva Cita"}
          onClose={() => setShowAppointmentModal(false)}
        >
          <AppointmentForm
            appointment={selectedAppointment}
            onSubmit={handleSubmitAppointment}
            onCancel={() => setShowAppointmentModal(false)}
            patientId={patientId}
            loading={formLoading}
          />
        </Modal>
      )}
      
      {showDetailsModal && selectedAppointment && (
        <Modal
          title="Detalles de la Cita"
          onClose={() => setShowDetailsModal(false)}
        >
          <AppointmentDetail
            appointment={selectedAppointment}
            onClose={() => setShowDetailsModal(false)}
            onUpdate={handleAppointmentUpdate}
          />
        </Modal>
      )}
    </div>
  );
}

export default SchedulePage;