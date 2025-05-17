import React from 'react';

function NotAvailableArea() {
  return (
    <div className="h-full w-full relative overflow-hidden">
      <div className="absolute inset-0 flex items-center justify-center text-sm text-gray-400 font-medium z-10">NO DISPONIBLE</div>
      <div
        className="absolute inset-0 bg-gray-100 opacity-50"
        style={{
          backgroundImage: `repeating-linear-gradient(
            45deg,
            transparent,
            transparent 10px,
            rgba(200, 200, 200, 0.5) 10px,
            rgba(200, 200, 200, 0.5) 20px
          )`,
        }}
      />
    </div>
  );
}

export default NotAvailableArea; 