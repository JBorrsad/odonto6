import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

// Layouts
import Sidebar from './components/layout/Sidebar';
import Topbar from './components/layout/Topbar';

// Páginas
import HomePage from './pages/HomePage';
import PatientsPage from './pages/PatientsPage';
import PatientNewPage from './pages/PatientNewPage';
import PatientDetailPage from './pages/PatientDetailPage';
import DoctorsPage from './pages/DoctorsPage';
import SchedulePage from './pages/SchedulePage';

function App() {
  return (
    <div className="flex flex-col min-h-screen bg-gray-50 text-gray-900">
      <Sidebar />
      <Topbar />
      
      <div className="ml-64 mt-16 flex-1">
        <div className="flex-1 overflow-x-hidden min-h-screen">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/patients" element={<PatientsPage />} />
            <Route path="/patients/new" element={<PatientNewPage />} />
            <Route path="/patients/:id" element={<PatientDetailPage />} />
            <Route path="/schedule/*" element={<SchedulePage />} />
            <Route path="/doctors" element={<DoctorsPage />} />
            
            {/* Redirección por defecto */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </div>
      </div>
    </div>
  );
}

export default App;
