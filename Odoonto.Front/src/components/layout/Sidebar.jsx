import React from 'react';
import { Link, useLocation } from 'react-router-dom';

function Sidebar() {
  const location = useLocation();
  
  const isActive = (path) => {
    return location.pathname.startsWith(path);
  };
  
  return (
    <div className="w-64 h-screen fixed left-0 top-0 bg-white text-gray-700 flex flex-col border-r border-gray-200 shadow-sm">
      {/* Logo y nombre de la clínica */}
      <div className="p-4 border-b border-gray-100">
        <div className="flex items-center space-x-2">
          <div className="h-6 w-6 bg-blue-600 rounded flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-white" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 3c1.5 0 2.825.618 3.793 1.585L10 8.379 6.207 4.585A5.5 5.5 0 0110 3zm7 7a7 7 0 11-14 0 7 7 0 0114 0zm-5-2a3 3 0 00-4 0V9h4v1a1 1 0 01-1 1h-2a1 1 0 01-1-1V8z" clipRule="evenodd" />
            </svg>
          </div>
          <span className="font-semibold text-lg text-blue-700">Odoonto</span>
        </div>
      </div>
      
      {/* Información de la clínica */}
      <div className="p-4 border-b border-gray-100">
        <div className="flex items-center">
          <div className="h-5 w-5 text-gray-500 mr-2">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
          </div>
          <div>
            <div className="text-sm font-medium">Clinica 1</div>
            <div className="text-xs text-gray-500">Direccion 1</div>
          </div>
        </div>
      </div>
      
      {/* Menú principal */}
      <div className="mt-2">
        <div className="text-xs font-medium text-gray-400 uppercase px-4 py-2">MENÚ</div>
        <nav>
          <Link
            to="/"
            className={`flex items-center px-4 py-2 text-sm ${
              isActive('/') && !isActive('/patients') && !isActive('/doctors') && !isActive('/schedule')
                ? 'text-blue-600 bg-blue-50 border-l-4 border-blue-600' 
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            <div className="h-5 w-5 mr-3">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 9l3 3-3 3m5 0h3M5 20h14a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <span>API Test</span>
          </Link>
          
          <Link
            to="/patients"
            className={`flex items-center px-4 py-2 text-sm ${
              isActive('/patients') 
                ? 'text-blue-600 bg-blue-50 border-l-4 border-blue-600' 
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            <div className="h-5 w-5 mr-3">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <span>Pacientes</span>
          </Link>
          
          <Link
            to="/schedule"
            className={`flex items-center px-4 py-2 text-sm ${
              isActive('/schedule') 
                ? 'text-blue-600 bg-blue-50 border-l-4 border-blue-600' 
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            <div className="h-5 w-5 mr-3">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <span>Horario</span>
          </Link>
          
          <Link
            to="/doctors"
            className={`flex items-center px-4 py-2 text-sm ${
              isActive('/doctors') 
                ? 'text-blue-600 bg-blue-50 border-l-4 border-blue-600' 
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            <div className="h-5 w-5 mr-3">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <span>Staff List</span>
          </Link>
        </nav>
      </div>
    </div>
  );
}

export default Sidebar; 