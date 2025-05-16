import React, { useState } from 'react';

function AppointmentCalendar({ onSelectDate, appointments }) {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  
  // Obtener el primer día del mes actual
  const firstDayOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1);
  
  // Obtener el último día del mes actual
  const lastDayOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0);
  
  // Obtener el día de la semana del primer día (0 = domingo, 1 = lunes, ..., 6 = sábado)
  const firstDayOfWeek = firstDayOfMonth.getDay();
  
  // Ajustar para que la semana comience en lunes (0 = lunes, ..., 6 = domingo)
  const startOffset = firstDayOfWeek === 0 ? 6 : firstDayOfWeek - 1;
  
  // Generar los días del calendario
  const calendarDays = [];
  
  // Añadir días del mes anterior para completar la primera semana
  const daysFromPrevMonth = startOffset;
  const prevMonth = new Date(currentMonth);
  prevMonth.setMonth(prevMonth.getMonth() - 1);
  const prevMonthLastDay = new Date(prevMonth.getFullYear(), prevMonth.getMonth() + 1, 0).getDate();
  
  for (let i = prevMonthLastDay - daysFromPrevMonth + 1; i <= prevMonthLastDay; i++) {
    calendarDays.push({
      date: new Date(prevMonth.getFullYear(), prevMonth.getMonth(), i),
      isCurrentMonth: false
    });
  }
  
  // Añadir días del mes actual
  for (let i = 1; i <= lastDayOfMonth.getDate(); i++) {
    calendarDays.push({
      date: new Date(currentMonth.getFullYear(), currentMonth.getMonth(), i),
      isCurrentMonth: true
    });
  }
  
  // Añadir días del mes siguiente para completar la última semana
  const remainingDays = 7 - (calendarDays.length % 7);
  if (remainingDays < 7) {
    const nextMonth = new Date(currentMonth);
    nextMonth.setMonth(nextMonth.getMonth() + 1);
    
    for (let i = 1; i <= remainingDays; i++) {
      calendarDays.push({
        date: new Date(nextMonth.getFullYear(), nextMonth.getMonth(), i),
        isCurrentMonth: false
      });
    }
  }
  
  // Nombre de los meses
  const monthNames = [
    'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
    'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
  ];
  
  // Cambiar al mes anterior
  const prevMonthHandler = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1));
  };
  
  // Cambiar al mes siguiente
  const nextMonthHandler = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1));
  };
  
  // Ir al mes actual
  const currentMonthHandler = () => {
    setCurrentMonth(new Date());
  };
  
  // Verificar si un día tiene citas
  const hasAppointments = (date) => {
    if (!appointments) return false;
    
    return appointments.some(appt => {
      const apptDate = new Date(appt.start);
      return apptDate.toDateString() === date.toDateString();
    });
  };
  
  // Obtener el número de citas para un día específico
  const getAppointmentCount = (date) => {
    if (!appointments) return 0;
    
    return appointments.filter(appt => {
      const apptDate = new Date(appt.start);
      return apptDate.toDateString() === date.toDateString();
    }).length;
  };
  
  // Verificar si una fecha es hoy
  const isToday = (date) => {
    const today = new Date();
    return date.toDateString() === today.toDateString();
  };
  
  return (
    <div className="appointment-calendar">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold">
          {monthNames[currentMonth.getMonth()]} {currentMonth.getFullYear()}
        </h2>
        
        <div className="flex space-x-2">
          <button
            onClick={prevMonthHandler}
            className="p-1 rounded hover:bg-gray-200"
            aria-label="Mes anterior"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          
          <button
            onClick={currentMonthHandler}
            className="px-2 py-1 text-sm rounded hover:bg-gray-200"
          >
            Hoy
          </button>
          
          <button
            onClick={nextMonthHandler}
            className="p-1 rounded hover:bg-gray-200"
            aria-label="Mes siguiente"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      </div>
      
      <div className="grid grid-cols-7 gap-1">
        {/* Cabecera con nombres de días */}
        {['L', 'M', 'X', 'J', 'V', 'S', 'D'].map(day => (
          <div key={day} className="text-center font-medium text-sm py-2">
            {day}
          </div>
        ))}
        
        {/* Días del calendario */}
        {calendarDays.map((dayObj, index) => (
          <div
            key={index}
            onClick={() => onSelectDate(dayObj.date)}
            className={`
              calendar-day p-2 h-16 border rounded relative cursor-pointer
              ${!dayObj.isCurrentMonth ? 'text-gray-400 bg-gray-50' : ''}
              ${isToday(dayObj.date) ? 'bg-blue-50 border-blue-300' : ''}
              hover:bg-gray-100
            `}
          >
            <div className="text-right text-sm">{dayObj.date.getDate()}</div>
            
            {hasAppointments(dayObj.date) && (
              <div className="absolute bottom-1 left-1 right-1">
                <div className="bg-blue-100 text-blue-800 text-xs rounded px-1 py-px text-center">
                  {getAppointmentCount(dayObj.date)} {getAppointmentCount(dayObj.date) === 1 ? 'cita' : 'citas'}
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default AppointmentCalendar; 