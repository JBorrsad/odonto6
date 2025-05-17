import React from 'react';
import { cn } from '../../utils/classNames';

/**
 * Componente de cita para el calendario
 */
function Appointment({ id, patientName, startTime, endTime, treatment, status, color }) {
  const getStatusBadge = () => {
    let statusColor = "";
    switch (status) {
      case "finished":
        statusColor = "text-green-500";
        break;
      case "encounter":
        statusColor = "text-amber-500";
        break;
      case "registered":
        statusColor = "text-blue-500";
        break;
      case "waiting":
        statusColor = "text-orange-500";
        break;
      default:
        statusColor = "text-gray-500";
    }

    return (
      <div className="bg-white rounded-sm px-1 py-0.5 text-xs text-gray-800 flex items-center">
        <span className={`mr-0.5 ${statusColor}`}>•</span>
        <span>{status.charAt(0).toUpperCase() + status.slice(1)}</span>
      </div>
    );
  };

  const getBackgroundColor = () => {
    switch (color) {
      case "pink":
        return "bg-pink-100";
      case "green":
        return "bg-green-100";
      case "blue":
        return "bg-blue-100";
      case "yellow":
        return "bg-yellow-100";
      default:
        return "bg-gray-100";
    }
  };

  const getBorderColor = () => {
    switch (color) {
      case "pink":
        return "border-pink-200";
      case "green":
        return "border-green-200";
      case "blue":
        return "border-blue-200";
      case "yellow":
        return "border-yellow-200";
      default:
        return "border-gray-200";
    }
  };

  const getLetterColor = () => {
    switch (color) {
      case "pink":
        return "bg-pink-500";
      case "green":
        return "bg-green-500";
      case "blue":
        return "bg-blue-500";
      case "yellow":
        return "bg-yellow-500";
      default:
        return "bg-gray-500";
    }
  };

  const getLetter = () => {
    switch (color) {
      case "pink":
        return "P";
      case "green":
        return "G";
      case "blue":
        return "B";
      case "yellow":
        return "Y";
      default:
        return "X";
    }
  };

  return (
    <div className={cn("p-2 rounded-lg h-full border flex flex-col", getBackgroundColor(), getBorderColor())}>
      <div className="flex justify-between items-start mb-1">
        <div className="flex items-center gap-1.5">
          <div className={cn("w-5 h-5 rounded-sm flex items-center justify-center text-white text-xs font-medium", getLetterColor())}>
            {getLetter()}
          </div>
          <span className="font-medium">{patientName}</span>
        </div>
        {getStatusBadge()}
      </div>
      <div className="text-xs text-gray-500 mb-2">
        {startTime} - {endTime}
      </div>
      <div className="flex justify-start mt-auto">
        <div className="text-xs text-gray-700 border border-gray-200 rounded-sm px-2 py-0.5 bg-white text-left">
          {treatment}
        </div>
      </div>
    </div>
  );
}

export default Appointment; 