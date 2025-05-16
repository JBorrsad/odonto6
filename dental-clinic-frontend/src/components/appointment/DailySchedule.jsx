import React from 'react';

function DailySchedule({ date, doctors, appointments, onNewAppointment, onEditAppointment }) {
  // Horas de operación de la clínica (de 9:00 a 16:30)
  const operatingHours = [
    { time: "09:00", isHalfHour: false },
    { time: "09:30", isHalfHour: true },
    { time: "10:00", isHalfHour: false },
    { time: "10:30", isHalfHour: true },
    { time: "11:00", isHalfHour: false },
    { time: "11:30", isHalfHour: true },
    { time: "12:00", isHalfHour: false },
    { time: "12:30", isHalfHour: true },
    { time: "13:00", isHalfHour: false },
    { time: "13:30", isHalfHour: true },
    { time: "14:00", isHalfHour: false },
    { time: "14:30", isHalfHour: true },
    { time: "15:00", isHalfHour: false },
    { time: "15:30", isHalfHour: true },
    { time: "16:00", isHalfHour: false },
    { time: "16:30", isHalfHour: true },
  ];
  
  // Helper para convertir hora en formato "HH:MM" a minutos desde medianoche
  const timeToMinutes = (timeString) => {
    const [hours, minutes] = timeString.split(':').map(Number);
    return hours * 60 + minutes;
  };
  
  // Helper para formatear fecha/hora
  const formatTime = (date) => {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  };
  
  // Función para encontrar citas para un doctor en un horario
  const findAppointment = (doctorId, timeSlot) => {
    const slotMinutes = timeToMinutes(timeSlot);
    
    return appointments.find(appt => {
      if (appt.doctorId !== doctorId) return false;
      
      const apptDate = new Date(appt.start);
      if (apptDate.toDateString() !== date.toDateString()) return false;
      
      const apptMinutes = apptDate.getHours() * 60 + apptDate.getMinutes();
      
      // Si el inicio de la cita coincide con este slot
      return apptMinutes === slotMinutes;
    });
  };
  
  // Obtener el color de fondo según el estado de la cita
  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDIENTE':
        return { bg: 'bg-yellow-100', border: 'border-yellow-200', text: 'text-yellow-800', icon: 'bg-yellow-500' };
      case 'CONFIRMADA':
        return { bg: 'bg-green-100', border: 'border-green-200', text: 'text-green-800', icon: 'bg-green-500' };
      case 'SALA_ESPERA':
        return { bg: 'bg-gray-100', border: 'border-gray-200', text: 'text-gray-800', icon: 'bg-gray-500' };
      case 'EN_CURSO':
        return { bg: 'bg-blue-100', border: 'border-blue-200', text: 'text-blue-800', icon: 'bg-blue-500' };
      case 'COMPLETADA':
        return { bg: 'bg-pink-100', border: 'border-pink-200', text: 'text-pink-800', icon: 'bg-pink-500' };
      case 'CANCELADA':
        return { bg: 'bg-red-100', border: 'border-red-200', text: 'text-red-800', icon: 'bg-red-500' };
      default:
        return { bg: 'bg-gray-100', border: 'border-gray-200', text: 'text-gray-800', icon: 'bg-gray-500' };
    }
  };
  
  // Obtener la letra del icono según el tipo de tratamiento
  const getIconLetter = (treatment) => {
    if (!treatment) return 'C';
    
    switch (treatment.toLowerCase()) {
      case 'extracción':
      case 'extraction':
        return 'E';
      case 'limpieza':
      case 'scaling':
        return 'L';
      case 'blanqueamiento':
      case 'bleaching':
        return 'B';
      case 'revisión':
      case 'checkup':
        return 'R';
      default:
        return treatment.charAt(0).toUpperCase();
    }
  };
  
  // Obtener el texto de estado para mostrar
  const getStatusText = (status) => {
    switch (status) {
      case 'COMPLETADA':
        return 'Finished';
      case 'EN_CURSO':
        return 'Encounter';
      case 'CONFIRMADA':
        return 'Confirmed';
      case 'PENDIENTE':
        return 'Pending';
      case 'SALA_ESPERA':
        return 'Waiting';
      case 'CANCELADA':
        return 'Cancelled';
      default:
        return status;
    }
  };
  
  // Manejar clic para crear nueva cita
  const handleSlotClick = (doctorId, timeSlot) => {
    // Verifica que no exista ya una cita en este slot
    const existingAppointment = findAppointment(doctorId, timeSlot);
    if (!existingAppointment) {
      const [hours, minutes] = timeSlot.split(':').map(Number);
      
      // Clonar la fecha seleccionada y establecer la hora del slot
      const appointmentDateTime = new Date(date);
      appointmentDateTime.setHours(hours, minutes, 0, 0);
      
      onNewAppointment({
        doctorId,
        start: appointmentDateTime.toISOString()
      });
    }
  };
  
  // Calcular cuántos slots ocupa una cita
  const getAppointmentSlots = (appointment) => {
    return appointment.durationSlots || 1;
  };
  
  // Verificar si una celda está ocupada por una cita que comienza en una fila anterior
  const isSlotOccupiedFromAbove = (doctorId, timeIndex) => {
    return appointments.some(appointment => {
      if (appointment.doctorId !== doctorId) return false;
      
      const apptDate = new Date(appointment.start);
      if (apptDate.toDateString() !== date.toDateString()) return false;
      
      const apptHour = apptDate.getHours();
      const apptMinutes = apptDate.getMinutes();
      const apptTimeStr = `${apptHour}:${apptMinutes === 0 ? '00' : '30'}`;
      
      // Encontrar el índice de la hora de inicio en operatingHours
      const startIndex = operatingHours.findIndex(h => h.time === apptTimeStr);
      if (startIndex === -1) return false;
      
      // Duración en bloques de 30 min
      const duration = getAppointmentSlots(appointment);
      
      // Verificar si la hora actual está dentro del rango de la cita
      return timeIndex > startIndex && timeIndex < startIndex + duration;
    });
  };
  
  // Hora actual para la línea de tiempo actual (formato "HH:MM")
  const getCurrentTime = () => {
    const now = new Date();
    const today = now.toDateString() === date.toDateString();
    
    if (!today) return null;
    
    const hours = now.getHours();
    const minutes = now.getMinutes();
    
    // Solo mostrar si estamos dentro del horario operativo
    if (hours < 9 || (hours === 16 && minutes > 30) || hours > 16) return null;
    
    return {
      time: `${hours}:${minutes < 10 ? '0' + minutes : minutes}`,
      // Posición relativa en la grilla (cada hora = 96px, cada minuto = 1.6px)
      position: (hours - 9) * 96 + minutes * 1.6
    };
  };
  
  const currentTime = getCurrentTime();
  
  return (
    <div className="daily-schedule">
      <div className="mt-4 flex border rounded-md overflow-hidden relative">
        {/* Columna de tiempo */}
        <div className="w-16 pr-2 text-right border-r relative">
          <div className="h-16 text-xs font-medium text-gray-500"></div>
          {operatingHours.map((hour, index) => (
            <div key={index} className="h-12 relative">
              {!hour.isHalfHour && (
                <div className="absolute -top-2 right-2 text-xs font-medium text-gray-500">
                  {hour.time}
                </div>
              )}
            </div>
          ))}
        </div>
        
        {/* Columnas de doctores */}
        {doctors.map(doctor => (
          <div key={doctor.id} className="flex-1 min-w-[250px] border-r">
            {/* Cabecera del doctor */}
            <div className="flex justify-between h-16 border-b border-gray-200 px-4 w-full">
              <div className="flex items-center">
                <div className="h-8 w-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center flex-shrink-0 mr-3">
                  <span className="font-semibold">{doctor.nombreCompleto?.charAt(0) || 'D'}</span>
                </div>
                <div className="flex flex-col justify-center">
                  <span className="font-medium">{doctor.nombreCompleto}</span>
                  <div className="text-xs text-gray-400">
                    Today's appointments: {
                      appointments.filter(a => 
                        a.doctorId === doctor.id && 
                        new Date(a.start).toDateString() === date.toDateString()
                      ).length
                    } patient(s)
                  </div>
                </div>
              </div>
              <div className="flex items-center">
                <button className="text-gray-400 hover:text-gray-600">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z" />
                  </svg>
                </button>
              </div>
            </div>
            
            {/* Celdas de horas */}
            {operatingHours.map((hour, index) => {
              const isHalfHour = hour.isHalfHour;
              const appointment = findAppointment(doctor.id, hour.time);
              const isOccupied = isSlotOccupiedFromAbove(doctor.id, index);
              
              return (
                <div
                  key={index}
                  className={`h-12 relative ${index > 0 ? "border-t border-gray-200" : ""} ${isHalfHour ? "border-t-dashed" : ""}`}
                  onClick={() => !appointment && !isOccupied && handleSlotClick(doctor.id, hour.time)}
                >
                  {appointment && !isOccupied ? (
                    <div
                      className="absolute z-10 px-1 py-0.5"
                      style={{
                        top: 0,
                        left: 4,
                        right: 4,
                        height: `${getAppointmentSlots(appointment) * 48}px`,
                      }}
                    >
                      <div className={`p-2 rounded-lg h-full border flex flex-col ${getStatusColor(appointment.status).bg} ${getStatusColor(appointment.status).border}`}>
                        <div className="flex justify-between items-start mb-1">
                          <div className="flex items-center gap-1.5">
                            <div className={`w-5 h-5 rounded-sm flex items-center justify-center text-white text-xs font-medium ${getStatusColor(appointment.status).icon}`}>
                              {getIconLetter(appointment.treatment)}
                            </div>
                            <span className="font-medium">{appointment.patientName || 'Paciente'}</span>
                          </div>
                          <div className="bg-white rounded-sm px-1 py-0.5 text-xs text-gray-800 flex items-center">
                            <span className={`mr-0.5 ${getStatusColor(appointment.status).text}`}>•</span>
                            <span>{getStatusText(appointment.status)}</span>
                          </div>
                        </div>
                        <div className="text-xs text-gray-500 mb-2">
                          {formatTime(new Date(appointment.start))} - {appointment.duration || '30'} min
                        </div>
                        <div className="flex justify-start mt-auto">
                          <div className="text-xs text-gray-700 border border-gray-200 rounded-sm px-2 py-0.5 bg-white text-left">
                            {appointment.treatment || 'Consulta'}
                          </div>
                        </div>
                      </div>
                    </div>
                  ) : !isOccupied ? (
                    <div className="w-full h-full cursor-pointer hover:bg-blue-50"></div>
                  ) : null}
                </div>
              );
            })}
          </div>
        ))}
        
        {/* Columna de adición de citas */}
        <div className="w-10 flex flex-col items-center border-l">
          <div className="h-16"></div>
          {operatingHours.map((_, index) => (
            <div
              key={index}
              className={`h-12 ${index > 0 ? "border-t border-gray-200" : ""} ${index % 2 !== 0 ? "border-t-dashed" : ""} w-full flex justify-center pt-2`}
            >
              {index === 15 && (
                <button className="h-6 w-6 text-blue-600 rounded-full hover:bg-blue-50 flex items-center justify-center">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd" />
                  </svg>
                </button>
              )}
            </div>
          ))}
        </div>
        
        {/* Indicador de tiempo actual */}
        {currentTime && (
          <>
            <div
              className="absolute left-0 right-0 border-t-2 border-red-500 z-10"
              style={{ top: `${currentTime.position + 64}px`, height: "2px" }}
            >
            </div>
            <div
              className="absolute bg-gray-800 text-white text-xs px-1.5 py-0.5 rounded-sm z-10 left-1/2 transform -translate-x-1/2"
              style={{ top: `${currentTime.position + 56}px` }}
            >
              {currentTime.time}
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default DailySchedule; 