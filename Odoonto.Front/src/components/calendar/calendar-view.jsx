import React, { useState } from 'react';
import CalendarHeader from './calendar-header';
import TimeColumn from './time-column';
import DentistColumn from './dentist-column';

/**
 * Componente principal de la vista de calendario
 */
function CalendarView() {
  // Inicializar con el 16 de mayo de 2025 (fecha específica)
  const [date, setDate] = useState(new Date(2025, 4, 16));
  const [view, setView] = useState("day");
  const [selectedDoctor, setSelectedDoctor] = useState(null);

  // Manejadores de navegación
  const handlePrevious = () => {
    const newDate = new Date(date);
    if (view === "day") {
      newDate.setDate(date.getDate() - 1);
    } else {
      newDate.setDate(date.getDate() - 7);
    }
    setDate(newDate);
  };

  const handleNext = () => {
    const newDate = new Date(date);
    if (view === "day") {
      newDate.setDate(date.getDate() + 1);
    } else {
      newDate.setDate(date.getDate() + 7);
    }
    setDate(newDate);
  };

  const handleToday = () => {
    setDate(new Date(2025, 4, 16));
  };

  const handleDoctorChange = (e) => {
    setSelectedDoctor(e.target.value || null);
  };

  // Datos de ejemplo actualizados para mejor visualización
  const dentists = [
    {
      id: "1",
      name: "Dr. Martínez",
      avatar: "/placeholder.svg",
      appointments: 3,
      patients: 3,
    },
    {
      id: "2",
      name: "Dra. Rodríguez",
      avatar: "/placeholder.svg",
      appointments: 3,
      patients: 3,
    },
    {
      id: "3",
      name: "Dr. García",
      avatar: "/placeholder.svg",
      appointments: 3,
      patients: 3,
    },
  ];

  // Nota: Los slots ahora van de 0 (8:00) a 24 (20:00)
  // Por lo tanto, los slots antiguos deben ajustarse restando 2 para que las citas aparezcan en el mismo lugar
  const allAppointments = [
    {
      dentistId: "1",
      appointments: [
        {
          id: "1",
          patientName: "María López",
          startTime: "09:00",
          endTime: "10:00",
          treatment: "Revisión General",
          status: "finished",
          color: "pink",
          startSlot: 0, // Antes era 0 (9:00), ahora es 0 (8:00) + 2 = 2 (9:00)
          endSlot: 1,   // Antes era 1 (9:30), ahora es 1 (8:30) + 2 = 3 (9:30)
        },
        {
          id: "2",
          patientName: "Juan Pérez",
          startTime: "11:30",
          endTime: "12:30",
          treatment: "Limpieza Dental",
          status: "encounter",
          color: "green",
          startSlot: 5, // 11:30
          endSlot: 6, // 12:00
        },
        {
          id: "3",
          patientName: "Carlos Ruiz",
          startTime: "15:00",
          endTime: "16:00",
          treatment: "Extracción",
          status: "registered",
          color: "blue",
          startSlot: 12, // 15:00
          endSlot: 14, // 16:00
        },
      ],
    },
    {
      dentistId: "2",
      appointments: [
        {
          id: "4",
          patientName: "Ana Torres",
          startTime: "09:30",
          endTime: "10:30",
          treatment: "Blanqueamiento",
          status: "finished",
          color: "green",
          startSlot: 1, // 9:30
          endSlot: 2, // 10:00
        },
        {
          id: "5",
          patientName: "Elena García",
          startTime: "12:00",
          endTime: "13:00",
          treatment: "Ortodoncia",
          status: "waiting",
          color: "yellow",
          startSlot: 6, // 12:00
          endSlot: 7, // 12:30
        },
        {
          id: "6",
          patientName: "Pablo Martín",
          startTime: "14:30",
          endTime: "15:30",
          treatment: "Implante",
          status: "registered",
          color: "pink",
          startSlot: 11, // 14:30
          endSlot: 12, // 15:00
        },
      ],
    },
    {
      dentistId: "3",
      appointments: [
        {
          id: "7",
          patientName: "Laura Díaz",
          startTime: "10:00",
          endTime: "11:00",
          treatment: "Empaste",
          status: "finished",
          color: "blue",
          startSlot: 2, // 10:00
          endSlot: 3, // 10:30
        },
        {
          id: "8",
          patientName: "Javier Sánchez",
          startTime: "13:00",
          endTime: "14:00",
          treatment: "Revisión de Brackets",
          status: "encounter",
          color: "green",
          startSlot: 8, // 13:00
          endSlot: 9, // 13:30
        },
        {
          id: "9",
          patientName: "Sofía Hernández",
          startTime: "16:00",
          endTime: "17:00",
          treatment: "Radiografía",
          status: "waiting",
          color: "yellow",
          startSlot: 14, // 16:00
          endSlot: 15, // 16:30
        },
      ],
    },
  ];

  // Filtrar doctores si hay uno seleccionado
  const filteredDentists = selectedDoctor
    ? dentists.filter(d => d.id === selectedDoctor)
    : dentists;

  // Contar todas las citas del día
  const totalAppointments = allAppointments.reduce((total, dentistAppts) => {
    return total + dentistAppts.appointments.length;
  }, 0);

  // Obtener hora actual (para la línea de tiempo)
  const now = new Date();
  const currentHour = now.getHours();
  const currentMinute = now.getMinutes();
  
  // Calcular posición relativa para la línea de tiempo actual
  // Ahora el rango va de 8:00 (0px) a 20:00 (25 slots * 48px de alto)
  // La altura total de cada hora son 48px (cada slot 24px * 2 slots por hora)
  const timePosition = ((currentHour - 8) * 60 + currentMinute) / 60 * 48;
  
  return (
    <div className="bg-white h-full">
      <div className="p-4 h-full">
        {/* El encabezado ya no se muestra aquí, ahora está en SchedulePage */}
        
        <div className="h-full flex border rounded-md overflow-hidden relative">
          <TimeColumn />

          {filteredDentists.map((dentist, index) => {
            const dentistAppointments = allAppointments.find((a) => a.dentistId === dentist.id)?.appointments || [];
            return (
              <DentistColumn
                key={dentist.id}
                dentist={dentist}
                appointments={dentistAppointments}
                showNotAvailable={index === 2} // Dr. García con áreas "NO DISPONIBLE"
                showBreakTime={true} // Mostrar BREAK TIME en todas las columnas
              />
            );
          })}

          <div className="w-10 flex flex-col items-center border-l">
            <div className="h-16"></div>
            {Array(25)
              .fill(null)
              .map((_, index) => (
                <div
                  key={index}
                  className={`h-12 ${index > 0 ? "border-t border-gray-200" : ""} ${index % 2 !== 0 ? "border-t-dashed" : ""} w-full flex justify-center pt-2`}
                >
                  {index === 24 && (
                    <button className="h-6 w-6 text-blue-600 rounded-full hover:bg-blue-50 flex items-center justify-center">
                      <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <line x1="12" y1="5" x2="12" y2="19"></line>
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                      </svg>
                    </button>
                  )}
                </div>
              ))}
          </div>

          {/* Indicador de hora actual - solo visible si la hora actual está dentro del rango de visualización */}
          {currentHour >= 8 && currentHour < 20 && (
            <>
              <div
                className="absolute left-0 right-0 border-t-2 border-red-500 z-10"
                style={{ 
                  top: `${timePosition + 64}px`, // 64px para el encabezado de la columna
                  height: "2px" 
                }}
              />

              {/* Marcador de hora actual */}
              <div
                className="absolute bg-gray-800 text-white text-xs px-1.5 py-0.5 rounded-sm z-10 left-1/2 transform -translate-x-1/2"
                style={{ 
                  top: `${timePosition + 54}px` // Ajuste para posicionar justo encima de la línea
                }}
              >
                {`${currentHour}:${currentMinute.toString().padStart(2, '0')}`}
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default CalendarView; 