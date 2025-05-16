import React from 'react';
import { Link, useLocation } from 'react-router-dom';

function Topbar() {
  const location = useLocation();
  
  // Función para determinar el título según la ruta actual
  const getPageTitle = () => {
    const path = location.pathname;
    
    if (path === '/') return 'API Test';
    if (path.startsWith('/patients')) return 'Pacientes';
    if (path.startsWith('/schedule')) return 'Horario';
    if (path.startsWith('/doctors')) return 'Staff List';
    
    return 'Odoonto';
  };
  
  return (
    <div className="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-4 shadow-sm fixed top-0 left-0 right-0 z-10 ml-64">
      <div className="flex items-center">
        <Link to="/" className="text-gray-700 mr-4">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </Link>
        <h1 className="text-lg font-medium text-gray-700">{getPageTitle()}</h1>
      </div>
      
      <div className="flex-1 px-8 max-w-2xl mx-auto">
        <div className="relative">
          <input
            type="text"
            placeholder="Buscar..."
            className="w-full pl-10 pr-4 py-2 rounded-full border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-200 text-sm"
          />
          <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
        </div>
      </div>
      
      <div className="flex items-center space-x-3">
        {/* Botón de ajustes - icono de engranaje */}
        <button className="p-2 text-gray-600 hover:bg-blue-50 rounded-full transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="12" cy="12" r="3"></circle>
            <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
          </svg>
        </button>
        
        <div className="flex items-center border-l border-gray-200 pl-3">
          <div className="text-right mr-3">
            <p className="text-sm font-medium text-gray-700">Admin</p>
            <p className="text-xs text-gray-500">Clínica Dental</p>
          </div>
          <div className="h-8 w-8 rounded-full flex items-center justify-center bg-blue-100 text-blue-700">
            <span className="font-semibold">A</span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Topbar; 