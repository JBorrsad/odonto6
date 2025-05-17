import React from 'react';

function BreakTime({ className = "" }) {
  return (
    <div className={`relative h-full w-full ${className}`}>
      {/* Fondo blanco con rayas diagonales */}
      <div
        className="absolute inset-0 bg-white"
        style={{
          backgroundImage: `repeating-linear-gradient(
            45deg,
            transparent,
            transparent 8px,
            rgba(230, 230, 230, 0.4) 8px,
            rgba(230, 230, 230, 0.4) 16px
          )`,
        }}
      />
      
      {/* Contenido de BREAK TIME */}
      <div className="absolute inset-0 flex items-center justify-center text-xs text-gray-400 z-10">
        <span className="flex items-center gap-2">
          {/* Icono de café simple */}
          <svg className="h-4 w-4 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M18 8h1a4 4 0 0 1 0 8h-1"></path>
            <path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"></path>
            <line x1="6" y1="1" x2="6" y2="4"></line>
            <line x1="10" y1="1" x2="10" y2="4"></line>
            <line x1="14" y1="1" x2="14" y2="4"></line>
          </svg>
          BREAK TIME
        </span>
      </div>
    </div>
  );
}

export default BreakTime; 