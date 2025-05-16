import React, { useEffect, useState, useRef } from 'react';
import Appointment from './appointment';
import NotAvailableArea from './not-available-area';
import BreakTime from './break-time';

// Definición de tipos
/**
 * @typedef {Object} AppointmentData
 * @property {string} id
 * @property {string} patientName
 * @property {string} startTime
 * @property {string} endTime
 * @property {string} treatment
 * @property {('finished'|'encounter'|'registered'|'waiting')} status
 * @property {('pink'|'green'|'blue'|'yellow')} color
 * @property {number} startSlot - 0 = 8am, 1 = 8:30am, 2 = 9am, etc.
 * @property {number} endSlot - Slot where the appointment ends
 */

/**
 * @typedef {Object} DentistData
 * @property {string} id
 * @property {string} name
 * @property {string} avatar
 * @property {number} appointments
 * @property {number} patients
 */

/**
 * @typedef {Object} NotAvailableBlock
 * @property {number} startIndex
 * @property {number} endIndex
 */

/**
 * @param {Object} props
 * @param {DentistData} props.dentist
 * @param {AppointmentData[]} props.appointments
 * @param {boolean} [props.showNotAvailable=false]
 * @param {boolean} [props.showBreakTime=false]
 */
function DentistColumn({
  dentist,
  appointments,
  showNotAvailable = false,
  showBreakTime = false,
}) {
  // Create slots for each half hour (8am to 8pm)
  const slots = Array(25).fill(null);
  const [notAvailableBlocks, setNotAvailableBlocks] = useState([]);
  const prevBlocksRef = useRef([]);

  // Fill slots with appointments
  appointments.forEach((appointment) => {
    // Ajustar los slots para el nuevo horario (añadir +2 a los startSlot/endSlot para compensar el inicio a las 8:00)
    const adjustedStartSlot = appointment.startSlot + 2;
    const adjustedEndSlot = appointment.endSlot + 2;
    
    // Mark all slots that this appointment spans
    for (let i = adjustedStartSlot; i <= adjustedEndSlot; i++) {
      if (i === adjustedStartSlot) {
        slots[i] = { ...appointment, isStart: true };
      } else {
        slots[i] = { ...appointment, isStart: false };
      }
    }
  });

  // Función para agrupar celdas NOT AVAILABLE consecutivas
  useEffect(() => {
    if (!showNotAvailable) {
      if (notAvailableBlocks.length > 0) {
        setNotAvailableBlocks([]);
      }
      return;
    }

    const blocks = [];
    let currentBlock = null;

    // Encuentra bloques consecutivos de celdas vacías
    // El slot 14 ahora corresponde a las 15:00
    slots.forEach((slot, index) => {
      // Si el slot está vacío y estamos en una posición para mostrar áreas no disponibles
      if (index > 14 && !slot) {
        if (!currentBlock) {
          // Inicia un nuevo bloque
          currentBlock = { startIndex: index, endIndex: index };
        } else {
          // Extiende el bloque actual
          currentBlock.endIndex = index;
        }
      } else {
        // Si hay un bloque activo, guárdalo y reinicia
        if (currentBlock) {
          blocks.push(currentBlock);
          currentBlock = null;
        }
      }
    });

    // No olvides el último bloque si existe
    if (currentBlock) {
      blocks.push(currentBlock);
    }

    // Comparar si los bloques realmente cambiaron para evitar renderizados innecesarios
    const areBlocksEqual = blocks.length === prevBlocksRef.current.length &&
      blocks.every((block, index) =>
        block.startIndex === prevBlocksRef.current[index]?.startIndex &&
        block.endIndex === prevBlocksRef.current[index]?.endIndex
      );
    
    if (!areBlocksEqual) {
      prevBlocksRef.current = blocks;
      setNotAvailableBlocks(blocks);
    }
  }, [slots, showNotAvailable, notAvailableBlocks.length]);

  // Índice para BREAK TIME (1:00 PM)
  const breakTimeIndex = 10; // slot 10 corresponde a 1:00 PM (8:00 + 5 horas)

  return (
    <div className="flex-1 min-w-[250px] border-r">
      <div className="flex justify-between h-16 border-b border-gray-200 px-4 w-full">
        <div className="flex items-center">
          {/* Avatar simplificado */}
          <div className="h-8 w-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center flex-shrink-0 mr-3">
            <span className="font-semibold">{dentist.name.charAt(0)}</span>
          </div>
          <div className="flex flex-col justify-center">
            <span className="font-medium">{dentist.name}</span>
            <div className="text-xs text-gray-400">Citas de hoy: {dentist.appointments} paciente(s)</div>
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
      
      {slots.map((slot, index) => {
        // Only render appointment at its start slot
        const shouldRenderAppointment = slot && slot.isStart;
        const isHalfHour = index % 2 !== 0;

        // Verificar si este índice es el inicio de un bloque NOT AVAILABLE
        const isNotAvailableStart = notAvailableBlocks.some(block => block.startIndex === index);
        
        // Obtener el bloque si este índice es el inicio
        const notAvailableBlock = notAvailableBlocks.find(block => block.startIndex === index);
        
        // Verificar si este índice es parte de un bloque (pero no el inicio)
        const isPartOfNotAvailableBlock = notAvailableBlocks.some(
          block => index > block.startIndex && index <= block.endIndex
        );

        return (
          <div
            key={index}
            className={`h-12 ${index > 0 ? "border-t border-gray-200" : ""} ${isHalfHour ? "border-t-dashed" : ""} relative`}
          >
            {shouldRenderAppointment ? (
              <div
                className="absolute z-10 px-1 py-0.5"
                style={{
                  top: 0,
                  left: 4,
                  right: 4,
                  height: `${(slot.endSlot - slot.startSlot + 1) * 48}px`,
                }}
              >
                <Appointment
                  id={slot.id}
                  patientName={slot.patientName}
                  startTime={slot.startTime}
                  endTime={slot.endTime}
                  treatment={slot.treatment}
                  status={slot.status}
                  color={slot.color}
                />
              </div>
            ) : null}

            {index === breakTimeIndex && showBreakTime && !slot ? (
              <div className="absolute inset-0 z-10">
                <BreakTime />
              </div>
            ) : null}

            {/* Renderizar NOT AVAILABLE solo para el primer índice del bloque */}
            {isNotAvailableStart && notAvailableBlock && showNotAvailable ? (
              <div 
                className="absolute z-10"
                style={{
                  top: 0,
                  left: 0,
                  right: 0,
                  height: `${(notAvailableBlock.endIndex - notAvailableBlock.startIndex + 1) * 48}px`,
                }}
              >
                <NotAvailableArea />
              </div>
            ) : null}

            {/* Renderizar NOT AVAILABLE para celdas individuales que no forman parte de ningún bloque */}
            {!slot && showNotAvailable && index > 14 && !isNotAvailableStart && !isPartOfNotAvailableBlock ? (
              <div className="absolute inset-0 z-10">
                <NotAvailableArea />
              </div>
            ) : null}
          </div>
        );
      })}
    </div>
  );
}

export default DentistColumn; 