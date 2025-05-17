import React from 'react';

function TimeColumn() {
  const hours = [
    { time: "08:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "09:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "10:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "11:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "12:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "13:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "14:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "15:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "16:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "17:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "18:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "19:00", isHalfHour: false },
    { time: "", isHalfHour: true },
    { time: "20:00", isHalfHour: false },
  ];

  return (
    <div className="w-16 pr-2 text-right border-r relative">
      <div className="h-16 text-xs font-medium text-gray-500">
        
      </div>
      {hours.map((hour, index) => (
        <div
          key={index}
          className="h-12 relative"
        >
          {!hour.isHalfHour && (
            <div className="absolute -top-2 right-2 text-xs font-medium text-gray-500">
              {hour.time}
            </div>
          )}
        </div>
      ))}
    </div>
  );
}

export default TimeColumn; 