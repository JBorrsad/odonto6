import React from 'react';
import { Link, useLocation } from 'react-router-dom';

function Sidebar() {
  const location = useLocation();
  
  const menuItems = [
    { name: 'Inicio', path: '/', icon: 'home' },
    { name: 'Pacientes', path: '/patients', icon: 'users' },
    { name: 'Agenda', path: '/schedule', icon: 'calendar' },
    { name: 'Doctores', path: '/doctors', icon: 'user-md' }
  ];
  
  const isActive = (path) => {
    return location.pathname === path;
  };
  
  return (
    <div className="w-64 h-full bg-gray-800 text-white flex flex-col">
      <div className="p-4 border-b border-gray-700">
        <h1 className="text-xl font-semibold">Clínica Dental</h1>
      </div>
      
      <nav className="flex-1 py-4">
        <ul className="space-y-1">
          {menuItems.map((item) => (
            <li key={item.path}>
              <Link
                to={item.path}
                className={`flex items-center px-4 py-3 text-gray-300 hover:bg-gray-700 hover:text-white transition-colors ${
                  isActive(item.path) ? 'bg-gray-700 text-white' : ''
                }`}
              >
                <i className={`fas fa-${item.icon} w-6`}></i>
                <span>{item.name}</span>
              </Link>
            </li>
          ))}
        </ul>
      </nav>
      
      <div className="p-4 border-t border-gray-700">
        <p className="text-sm text-gray-400">© 2023 Clínica Dental</p>
      </div>
    </div>
  );
}

export default Sidebar; 