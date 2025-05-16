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

  // Datos de ejemplo
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
          startSlot: 0,
          endSlot: 1,
        },
        {
          id: "2",
          patientName: "Juan Pérez",
          startTime: "11:30",
          endTime: "12:30",
          treatment: "Limpieza Dental",
          status: "encounter",
          color: "green",
          startSlot: 5,
          endSlot: 6,
        },
        {
          id: "3",
          patientName: "Carlos Ruiz",
          startTime: "15:00",
          endTime: "16:00",
          treatment: "Extracción",
          status: "registered",
          color: "blue",
          startSlot: 12,
          endSlot: 14,
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
          startSlot: 1,
          endSlot: 2,
        },
        {
          id: "5",
          patientName: "Elena García",
          startTime: "12:00",
          endTime: "13:00",
          treatment: "Ortodoncia",
          status: "waiting",
          color: "yellow",
          startSlot: 6,
          endSlot: 7,
        },
        {
          id: "6",
          patientName: "Pablo Martín",
          startTime: "14:30",
          endTime: "15:30",
          treatment: "Implante",
          status: "registered",
          color: "pink",
          startSlot: 11,
          endSlot: 12,
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
          startSlot: 2,
          endSlot: 3,
        },
        {
          id: "8",
          patientName: "Javier Sánchez",
          startTime: "13:00",
          endTime: "14:00",
          treatment: "Revisión de Brackets",
          status: "encounter",
          color: "green",
          startSlot: 8,
          endSlot: 9,
        },
        {
          id: "9",
          patientName: "Sofía Hernández",
          startTime: "16:00",
          endTime: "17:00",
          treatment: "Radiografía",
          status: "waiting",
          color: "yellow",
          startSlot: 14,
          endSlot: 15,
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

  return (
    <div className="bg-white rounded-md">
      <div className="p-4">
        <CalendarHeader
          date={date}
          onPrevious={handlePrevious}
          onNext={handleNext}
          onToday={handleToday}
          view={view}
          onViewChange={setView}
          onDoctorChange={handleDoctorChange}
          doctors={dentists}
          selectedDoctor={selectedDoctor}
          appointmentsCount={totalAppointments}
        />

        <div className="mt-4 flex border rounded-md overflow-hidden relative">
          <TimeColumn />

          {filteredDentists.map((dentist, index) => {
            const dentistAppointments = allAppointments.find((a) => a.dentistId === dentist.id)?.appointments || [];
            return (
              <DentistColumn
                key={dentist.id}
                dentist={dentist}
                appointments={dentistAppointments}
                showNotAvailable={index === 2}
                showBreakTime={index === 1}
                showWaitingPayment={index === 0}
                showPlayButton={index === 1}
              />
            );
          })}

          <div className="w-10 flex flex-col items-center border-l">
            <div className="h-16"></div>
            {Array(16)
              .fill(null)
              .map((_, index) => (
                <div
                  key={index}
                  className={`h-12 ${index > 0 ? "border-t border-gray-200" : ""} ${index % 2 !== 0 ? "border-t-dashed" : ""} w-full flex justify-center pt-2`}
                >
                  {index === 15 && (
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

          {/* Indicador de hora actual */}
          <div
            className="absolute left-0 right-0 border-t-2 border-red-500 z-10"
            style={{ top: "288px", height: "2px" }}
          />

          {/* Marcador de hora actual */}
          <div
            className="absolute bg-gray-800 text-white text-xs px-1.5 py-0.5 rounded-sm z-10 left-1/2 transform -translate-x-1/2"
            style={{ top: "280px" }}
          >
            13:30
          </div>
        </div>
      </div>
    </div>
  );
}

export default CalendarView; 