import React from 'react';
import Button from '../common/Button';

/**
 * Encabezado del calendario con controles de navegación
 * @param {Object} props
 * @param {Date} props.date - La fecha actual
 * @param {Function} props.onPrevious - Función para ir a día/semana anterior
 * @param {Function} props.onNext - Función para ir a día/semana siguiente
 * @param {Function} props.onToday - Función para ir a hoy
 * @param {('day'|'week')} props.view - Vista actual (día/semana)
 * @param {Function} props.onViewChange - Función para cambiar la vista
 * @param {Function} props.onDoctorChange - Función para cambiar el doctor seleccionado
 * @param {Array} props.doctors - Lista de doctores
 * @param {string|null} props.selectedDoctor - ID del doctor seleccionado
 * @param {number} props.appointmentsCount - Número de citas para la fecha seleccionada
 */
function CalendarHeader({ 
  date, 
  onPrevious, 
  onNext, 
  onToday, 
  view, 
  onViewChange, 
  onDoctorChange,
  doctors = [],
  selectedDoctor,
  appointmentsCount = 0
}) {
  const formattedDate = new Intl.DateTimeFormat("es-ES", {
    weekday: "short",
    day: "numeric",
    month: "short",
    year: "numeric",
  }).format(date);

  return (
    <div className="flex items-center justify-between py-4">
      <div className="flex items-center gap-2">
        <div className="flex items-center justify-center bg-gray-100 p-2 rounded-md">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-gray-700" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          <span className="ml-2 font-semibold">{appointmentsCount}</span>
        </div>
        <div className="text-sm text-gray-500">citas en total</div>
      </div>

      <div className="flex items-center gap-2">
        <Button variant="outline" onClick={onToday} className="rounded-full px-4 text-sm">
          Hoy
        </Button>
        <button 
          className="h-8 w-8 flex items-center justify-center rounded-full hover:bg-gray-100" 
          onClick={onPrevious}
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <div className="font-medium">{formattedDate}</div>
        <button 
          className="h-8 w-8 flex items-center justify-center rounded-full hover:bg-gray-100" 
          onClick={onNext}
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>

      <div className="flex items-center gap-4">
        <div className="flex border rounded-md overflow-hidden">
          <button
            className={`px-3 py-1 text-sm ${view === "day" ? "bg-blue-600 text-white" : "bg-white text-gray-700"}`}
            onClick={() => onViewChange("day")}
          >
            Día
          </button>
          <button
            className={`px-3 py-1 text-sm ${view === "week" ? "bg-blue-600 text-white" : "bg-white text-gray-700"}`}
            onClick={() => onViewChange("week")}
          >
            Semana
          </button>
        </div>

        <select
          value={selectedDoctor || ''}
          onChange={onDoctorChange}
          className="w-[180px] bg-white border border-gray-300 rounded-md px-3 py-1 text-sm"
        >
          <option value="">Todos los Doctores</option>
          {doctors.map(doctor => (
            <option key={doctor.id} value={doctor.id}>
              {doctor.name || doctor.nombreCompleto}
            </option>
          ))}
        </select>

        <Button variant="outline" className="flex items-center gap-2 text-sm">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
          </svg>
          Filtros
        </Button>
      </div>
    </div>
  );
}

export default CalendarHeader; 