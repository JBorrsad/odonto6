import React from 'react';

function WeeklySchedule({ week, doctor, appointments, onNewAppointment, onEditAppointment }) {
  if (!doctor) {
    return (
      <div className="p-4 text-center bg-gray-50 rounded border">
        Por favor, selecciona un doctor para ver su horario semanal.
      </div>
    );
  }
  
  // Horas de operación de la clínica (de 9:00 a 18:00)
  const operatingHours = [];
  for (let hour = 9; hour < 18; hour++) {
    operatingHours.push(`${hour}:00`);
    operatingHours.push(`${hour}:30`);
  }
  
  // Obtener el array de días de la semana
  const weekDays = getWeekDays(week);
  
  // Función para obtener los días de la semana a partir de la fecha de inicio
  function getWeekDays(startDate) {
    const days = [];
    const currentDate = new Date(startDate);
    
    // Ajustar al lunes si no lo es
    const dayOfWeek = currentDate.getDay(); // 0 = Domingo, 1 = Lunes, ...
    const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek; // Ajustar para que la semana empiece en lunes
    currentDate.setDate(currentDate.getDate() + diff);
    
    // Generar los 7 días de la semana
    for (let i = 0; i < 7; i++) {
      const date = new Date(currentDate);
      date.setDate(date.getDate() + i);
      days.push(date);
    }
    
    return days;
  }
  
  // Función para encontrar citas para un día y horario específicos
  function findAppointments(day, timeSlot) {
    // Convertir la hora del slot a minutos desde medianoche
    const [hours, minutes] = timeSlot.split(':').map(Number);
    const slotMinutes = hours * 60 + minutes;
    
    return appointments.filter(appt => {
      const apptDate = new Date(appt.start);
      
      // Verificar si la cita es del doctor seleccionado
      if (appt.doctorId !== doctor.id) return false;
      
      // Verificar si la fecha coincide con el día de la semana
      if (apptDate.toDateString() !== day.toDateString()) return false;
      
      // Verificar si la hora coincide con el slot
      const apptMinutes = apptDate.getHours() * 60 + apptDate.getMinutes();
      return apptMinutes === slotMinutes;
    });
  }
  
  // Formatear horas para mostrar
  function formatTime(date) {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }
  
  // Obtener el color de fondo según el estado de la cita
  function getStatusColor(status) {
    const colors = {
      'PENDIENTE': 'bg-yellow-100 border-yellow-300',
      'CONFIRMADA': 'bg-green-100 border-green-300',
      'SALA_ESPERA': 'bg-gray-100 border-gray-300',
      'EN_CURSO': 'bg-blue-100 border-blue-300',
      'COMPLETADA': 'bg-gray-100 border-gray-300 opacity-60',
      'CANCELADA': 'bg-red-100 border-red-300 opacity-60'
    };
    
    return colors[status] || 'bg-gray-100 border-gray-300';
  }
  
  // Manejar clic para crear una nueva cita
  function handleSlotClick(day, timeSlot) {
    // Solo permitir crear cita si no hay una existente
    const existing = findAppointments(day, timeSlot);
    if (existing.length === 0) {
      const [hours, minutes] = timeSlot.split(':').map(Number);
      
      // Crear fecha para la cita
      const appointmentDateTime = new Date(day);
      appointmentDateTime.setHours(hours, minutes, 0, 0);
      
      onNewAppointment({
        doctorId: doctor.id,
        start: appointmentDateTime.toISOString()
      });
    }
  }
  
  // Formato corto para los nombres de días
  const dayNames = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
  
  return (
    <div className="weekly-schedule">
      <h2 className="text-lg font-semibold mb-2">
        Horario semanal: {doctor.nombreCompleto}
      </h2>
      <p className="text-sm text-gray-600 mb-4">
        Semana del {weekDays[0].toLocaleDateString()} al {weekDays[6].toLocaleDateString()}
      </p>
      
      <div className="overflow-x-auto">
        <div className="min-w-full">
          {/* Cabecera con días de la semana */}
          <div className="grid grid-cols-[100px_repeat(7,1fr)]">
            <div className="bg-gray-200 p-2 font-semibold border">Hora</div>
            
            {weekDays.map((day, index) => (
              <div key={day.toISOString()} className="bg-gray-200 p-2 font-semibold border text-center">
                <div>{dayNames[index]}</div>
                <div className="text-xs">{day.getDate()}/{day.getMonth() + 1}</div>
              </div>
            ))}
          </div>
          
          {/* Filas de horas */}
          {operatingHours.map(timeSlot => (
            <div key={timeSlot} className="grid grid-cols-[100px_repeat(7,1fr)]">
              <div className="p-2 border text-sm font-medium">{timeSlot}</div>
              
              {weekDays.map(day => {
                // Buscar citas en este día y slot
                const dayAppointments = findAppointments(day, timeSlot);
                const appointment = dayAppointments[0]; // Tomamos la primera (normalmente será única)
                
                // Renderizar cita si existe
                if (appointment) {
                  // Calcular cuántos slots ocupa
                  const slots = appointment.durationSlots || 1;
                  
                  return (
                    <div
                      key={day.toISOString()}
                      style={{ gridRow: `span ${slots}` }}
                      className={`p-2 border relative cursor-pointer ${getStatusColor(appointment.status)}`}
                      onClick={() => onEditAppointment(appointment)}
                    >
                      <div className="font-medium truncate text-xs">
                        {appointment.patientName || 'Paciente'}
                      </div>
                      <div className="text-xs mt-1">
                        {formatTime(new Date(appointment.start))}
                      </div>
                    </div>
                  );
                }
                
                // Celda vacía donde se puede crear cita
                return (
                  <div
                    key={day.toISOString()}
                    className="p-2 border cursor-pointer hover:bg-blue-50"
                    onClick={() => handleSlotClick(day, timeSlot)}
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

export default WeeklySchedule;