import React, { useEffect, useState } from 'react';
import { getAppointments, getDoctors, updateAppointmentStatus } from '../services/api';
import { useLocation, useNavigate, Routes, Route } from 'react-router-dom';
import Card from '../components/common/Card';

// Componente para la vista diaria
function DailySchedule({ date, appointments, doctors }) {
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
    switch (status?.toLowerCase()) {
      case 'confirmada':
        return 'bg-green-100 text-green-800 border-green-500';
      case 'sin confirmar':
        return 'bg-yellow-100 text-yellow-800 border-yellow-500';
      case 'en sala de espera':
        return 'bg-blue-100 text-blue-800 border-blue-500';
      case 'en curso':
        return 'bg-purple-100 text-purple-800 border-purple-500';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-500';
    }
  };

  const getAppointmentsByHourAndDoctor = (hour, doctorId) => {
    return appointments.filter(appointment => {
      const startTime = new Date(appointment.fecha);
      const hourStr = `${startTime.getHours()}:${startTime.getMinutes() === 0 ? '00' : '30'}`;
      return hourStr === hour && appointment.doctor?.id === doctorId;
    });
  };

  // Calcular la duración en número de bloques de 30 min
  const getAppointmentDuration = (appointment) => {
    const durationMinutes = appointment.duracion || 30; // Default to 30 minutes
    return Math.ceil(durationMinutes / 30);
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
                  Dr. {doctor.nombre} {doctor.apellidos}
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
                    if (appointment.doctor?.id !== doctor.id) return false;
                    
                    const startTime = new Date(appointment.fecha);
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
                            className={`p-2 rounded border-l-4 mb-1 ${getStatusClass(appointment.estado)}`}
                            style={{ height: `${duration * 35}px` }}
                          >
                            <div className="font-medium">{appointment.paciente?.nombre} {appointment.paciente?.apellidos}</div>
                            <div className="text-xs">
                              {formatTime(appointment.fecha)} - Duración: {appointment.duracion || 30} min
                            </div>
                            <div className="text-xs mt-1">{appointment.tratamiento}</div>
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
function WeeklySchedule({ selectedDate, appointments, doctors }) {
  const [selectedDoctor, setSelectedDoctor] = useState(doctors[0]?.id || null);
  
  // Generar los días de la semana
  const getWeekDays = (date) => {
    const curr = new Date(date);
    const first = curr.getDate() - curr.getDay();
    
    const days = [];
    for (let i = 0; i < 7; i++) {
      const day = new Date(curr.setDate(first + i));
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
    switch (status?.toLowerCase()) {
      case 'confirmada':
        return 'bg-green-100 text-green-800 border-green-500';
      case 'sin confirmar':
        return 'bg-yellow-100 text-yellow-800 border-yellow-500';
      case 'en sala de espera':
        return 'bg-blue-100 text-blue-800 border-blue-500';
      case 'en curso':
        return 'bg-purple-100 text-purple-800 border-purple-500';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-500';
    }
  };
  
  const getAppointmentsForDayAndDoctor = (day, doctorId) => {
    return appointments.filter(appointment => {
      const appointmentDate = new Date(appointment.fecha);
      return (
        appointmentDate.toDateString() === day.toDateString() && 
        appointment.doctor?.id === doctorId
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
          value={selectedDoctor || ''}
          onChange={(e) => setSelectedDoctor(e.target.value)}
        >
          {doctors.map(doctor => (
            <option key={doctor.id} value={doctor.id}>
              Dr. {doctor.nombre} {doctor.apellidos}
            </option>
          ))}
        </select>
      </div>
      
      <div className="grid grid-cols-7 gap-2">
        {weekDays.map(day => (
          <div key={day.toISOString()} className="border rounded">
            <div className={`text-center p-2 font-medium ${day.toDateString() === new Date().toDateString() ? 'bg-blue-100' : 'bg-gray-100'}`}>
              {formatDate(day)}
            </div>
            
            <div className="p-2 min-h-40">
              {getAppointmentsForDayAndDoctor(day, selectedDoctor).map(appointment => (
                <div 
                  key={appointment.id}
                  className={`p-2 text-xs rounded border-l-4 mb-1 ${getStatusClass(appointment.estado)}`}
                >
                  <div>{formatTime(appointment.fecha)}</div>
                  <div className="font-medium">{appointment.paciente?.nombre}</div>
                  <div>{appointment.tratamiento}</div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// Componente para la vista de calendario mensual
function CalendarView({ selectedDate, appointments, onDateSelect }) {
  const [currentMonth, setCurrentMonth] = useState(new Date(selectedDate));
  
  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    
    // Determinar el primer día del mes
    const firstDay = new Date(year, month, 1).getDay();
    
    // Crear array para todos los días
    const days = [];
    
    // Añadir días vacíos para alinear el primer día
    for (let i = 0; i < firstDay; i++) {
      days.push({ day: null, date: null });
    }
    
    // Añadir todos los días del mes
    for (let i = 1; i <= daysInMonth; i++) {
      const dayDate = new Date(year, month, i);
      days.push({ 
        day: i, 
        date: dayDate,
        appointments: appointments.filter(appointment => {
          const appointmentDate = new Date(appointment.fecha);
          return appointmentDate.toDateString() === dayDate.toDateString();
        })
      });
    }
    
    return days;
  };
  
  const days = getDaysInMonth(currentMonth);
  const weekdays = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  
  const prevMonth = () => {
    const date = new Date(currentMonth);
    date.setMonth(date.getMonth() - 1);
    setCurrentMonth(date);
  };
  
  const nextMonth = () => {
    const date = new Date(currentMonth);
    date.setMonth(date.getMonth() + 1);
    setCurrentMonth(date);
  };
  
  return (
    <div className="calendar-view">
      <div className="flex justify-between items-center mb-4">
        <button 
          onClick={prevMonth}
          className="bg-gray-200 p-2 rounded hover:bg-gray-300"
        >
          &lt;
        </button>
        
        <h2 className="text-xl font-semibold">
          {currentMonth.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })}
        </h2>
        
        <button 
          onClick={nextMonth}
          className="bg-gray-200 p-2 rounded hover:bg-gray-300"
        >
          &gt;
        </button>
      </div>
      
      <div className="grid grid-cols-7 gap-1">
        {weekdays.map(day => (
          <div key={day} className="text-center p-2 font-medium text-sm text-gray-500">
            {day}
          </div>
        ))}
        
        {days.map((day, index) => (
          <div 
            key={index} 
            className={`border p-1 min-h-24 ${
              day.date && day.date.toDateString() === new Date().toDateString()
                ? 'bg-blue-50'
                : day.date ? 'hover:bg-gray-50 cursor-pointer' : 'bg-gray-100'
            }`}
            onClick={() => day.date && onDateSelect(day.date)}
          >
            {day.day && (
              <>
                <div className="text-right">{day.day}</div>
                <div>
                  {day.appointments.length > 0 && (
                    <div className="text-xs mt-1">
                      <div className="bg-blue-100 text-blue-800 rounded-full px-2 py-1 inline-block">
                        {day.appointments.length} {day.appointments.length === 1 ? 'cita' : 'citas'}
                      </div>
                    </div>
                  )}
                </div>
              </>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

function SchedulePage() {
  const location = useLocation();
  const navigate = useNavigate();
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [viewMode, setViewMode] = useState('daily');

  // Extraer patientId de URL si existe
  const queryParams = new URLSearchParams(location.search);
  const patientId = queryParams.get('patientId');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Obtener citas
        const appointmentsData = await getAppointments();
        setAppointments(appointmentsData);

        // Obtener doctores
        const doctorsData = await getDoctors();
        setDoctors(doctorsData);

      } catch (err) {
        setError(err.message || "Error al cargar los datos");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleViewChange = (mode) => {
    setViewMode(mode);
  };

  const handleDateSelect = (date) => {
    setSelectedDate(date);
    setViewMode('daily');
  };

  if (loading) {
    return (
      <div className="p-6 text-center">
        <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
        <p className="mt-2">Cargando horario...</p>
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

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Agenda</h1>
        
        <button 
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          onClick={() => navigate('/schedule/new')}
        >
          Nueva Cita
        </button>
      </div>

      <div className="mb-6">
        <div className="flex border-b border-gray-200 mb-4">
          <button
            className={`py-2 px-4 font-medium ${viewMode === 'daily' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500 hover:text-gray-700'}`}
            onClick={() => handleViewChange('daily')}
          >
            Diario
          </button>
          <button
            className={`py-2 px-4 font-medium ${viewMode === 'weekly' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500 hover:text-gray-700'}`}
            onClick={() => handleViewChange('weekly')}
          >
            Semanal
          </button>
          <button
            className={`py-2 px-4 font-medium ${viewMode === 'calendar' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500 hover:text-gray-700'}`}
            onClick={() => handleViewChange('calendar')}
          >
            Calendario
          </button>
        </div>

        <Card>
          <div className="p-4">
            {viewMode === 'daily' && (
              <DailySchedule 
                date={selectedDate} 
                appointments={appointments} 
                doctors={doctors} 
              />
            )}
            
            {viewMode === 'weekly' && (
              <WeeklySchedule 
                selectedDate={selectedDate} 
                appointments={appointments} 
                doctors={doctors} 
              />
            )}
            
            {viewMode === 'calendar' && (
              <CalendarView 
                selectedDate={selectedDate} 
                appointments={appointments} 
                onDateSelect={handleDateSelect} 
              />
            )}
          </div>
        </Card>
      </div>

      <Routes>
        <Route path="/new" element={<div>Formulario de nueva cita aquí</div>} />
      </Routes>
    </div>
  );
}

export default SchedulePage;