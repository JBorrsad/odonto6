import React from 'react';

function DailySchedule({ date, doctors, appointments, onNewAppointment, onEditAppointment }) {
  // Horas de operación de la clínica (de 9:00 a 18:00)
  const operatingHours = [];
  for (let hour = 9; hour < 18; hour++) {
    operatingHours.push(`${hour}:00`);
    operatingHours.push(`${hour}:30`);
  }
  
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
    const colors = {
      'PENDIENTE': 'bg-yellow-100 border-yellow-300',
      'CONFIRMADA': 'bg-green-100 border-green-300',
      'EN_ESPERA': 'bg-blue-100 border-blue-300',
      'EN_CURSO': 'bg-indigo-100 border-indigo-300',
      'COMPLETADA': 'bg-gray-100 border-gray-300',
      'CANCELADA': 'bg-red-100 border-red-300'
    };
    
    return colors[status] || 'bg-gray-100 border-gray-300';
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
  
  // Manejar clic en una cita existente
  const handleAppointmentClick = (appointment) => {
    onEditAppointment(appointment);
  };
  
  return (
    <div className="daily-schedule">
      <h2 className="text-lg font-semibold mb-4">
        Horario para el {date.toLocaleDateString()}
      </h2>
      
      <div className="overflow-x-auto">
        <div className="min-w-full">
          {/* Cabecera con doctores */}
          <div className="grid grid-cols-[100px_repeat(auto-fill,minmax(180px,1fr))]">
            <div className="bg-gray-200 p-2 font-semibold border">Hora</div>
            
            {doctors.map(doctor => (
              <div key={doctor.id} className="bg-gray-200 p-2 font-semibold border truncate">
                {doctor.nombreCompleto}
              </div>
            ))}
          </div>
          
          {/* Filas de horas */}
          {operatingHours.map(timeSlot => (
            <div key={timeSlot} className="grid grid-cols-[100px_repeat(auto-fill,minmax(180px,1fr))]">
              <div className="p-2 border text-sm font-medium">{timeSlot}</div>
              
              {doctors.map(doctor => {
                const appointment = findAppointment(doctor.id, timeSlot);
                
                // Si hay una cita que empieza en este slot
                if (appointment) {
                  // Calcular cuántos slots ocupa
                  const slots = appointment.durationSlots || 1;
                  
                  return (
                    <div
                      key={doctor.id}
                      style={{ gridRow: `span ${slots}` }}
                      className={`p-2 border relative cursor-pointer ${getStatusColor(appointment.status)}`}
                      onClick={() => handleAppointmentClick(appointment)}
                    >
                      <div className="font-medium truncate">
                        {/* Mostrar nombre completo del paciente (simulado) */}
                        {appointment.patientName || 'Paciente'}
                      </div>
                      <div className="text-xs mt-1">
                        {formatTime(new Date(appointment.start))} - 
                        {/* Calcular hora fin basada en slots */}
                        {formatTime(new Date(new Date(appointment.start).getTime() + (slots * 30 * 60 * 1000)))}
                      </div>
                      <div className="text-xs mt-1 font-semibold">
                        {appointment.status}
                      </div>
                    </div>
                  );
                }
                
                // Para slots vacíos, mostrar celda que permite añadir cita
                return (
                  <div
                    key={doctor.id}
                    className="p-2 border cursor-pointer hover:bg-blue-50"
                    onClick={() => handleSlotClick(doctor.id, timeSlot)}
                  />
                );
              })}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default DailySchedule; 