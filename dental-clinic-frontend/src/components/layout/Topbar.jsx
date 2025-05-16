import React, { useState } from 'react';

function Topbar() {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  // Usuario estático sin autenticación
  const user = { name: 'Dr. Martínez', role: 'Administrador' };
  
  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };
  
  return (
    <div className="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-4 shadow-sm">
      <div className="flex items-center">
        <span className="font-medium text-lg text-gray-700">
          Clínica Dental
        </span>
      </div>
      
      <div className="relative">
        <button
          onClick={toggleDropdown}
          className="flex items-center text-gray-700 focus:outline-none"
        >
          <span className="mr-2">{user.name}</span>
          <div className="h-8 w-8 rounded-full bg-blue-500 flex items-center justify-center text-white">
            {user.name.charAt(0)}
          </div>
          <svg className="h-5 w-5 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
          </svg>
        </button>
        
        {isDropdownOpen && (
          <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10">
            <div className="px-4 py-2 text-sm text-gray-700 border-b">
              <p className="font-medium">{user.name}</p>
              <p className="text-gray-500">{user.role}</p>
            </div>
            <a href="#profile" className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
              Mi Perfil
            </a>
            <a href="#settings" className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
              Configuración
            </a>
          </div>
        )}
      </div>
    </div>
  );
}

export default Topbar; 