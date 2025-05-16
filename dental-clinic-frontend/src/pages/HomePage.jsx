import React, { useEffect, useState } from 'react';
import { getPatients, getDoctors, getAppointments, getOdontogramByPatientId } from '../services/api';
import axios from 'axios';

function HomePage() {
  const [data, setData] = useState({
    patients: { loading: true, data: null, error: null },
    doctors: { loading: true, data: null, error: null },
    appointments: { loading: true, data: null, error: null },
    odontogram: { loading: true, data: null, error: null }
  });

  const [expandedSection, setExpandedSection] = useState({
    patients: false,
    doctors: false,
    appointments: false,
    odontogram: false
  });

  useEffect(() => {
    // Function to load data from an endpoint
    const fetchData = async (endpoint, fetchFunction, params) => {
      try {
        const result = await fetchFunction(params);
        
        // Check if the response is HTML instead of JSON
        if (typeof result === 'string' && result.includes('<!doctype html>')) {
          throw new Error('API responded with HTML instead of JSON. Check if backend is running correctly.');
        }
        
        setData(prev => ({
          ...prev,
          [endpoint]: { loading: false, data: result, error: null }
        }));
      } catch (error) {
        setData(prev => ({
          ...prev,
          [endpoint]: { loading: false, data: null, error: error.message || 'Error loading data' }
        }));
      }
    };

    // Load data from all endpoints
    fetchData('patients', getPatients);
    fetchData('doctors', getDoctors);
    fetchData('appointments', getAppointments);
    
    // For odontogram we need an ID, we'll try to get the first available
    fetchData('odontogram', async () => {
      try {
        const patients = await getPatients();
        
        // Check if the response is HTML
        if (typeof patients === 'string' && patients.includes('<!doctype html>')) {
          throw new Error('API responded with HTML instead of JSON. Check if backend is running correctly.');
        }
        
        if (patients && patients.length > 0) {
          return await getOdontogramByPatientId(patients[0].id);
        }
        throw new Error('No patients available to get odontogram');
      } catch (error) {
        throw error;
      }
    });

  }, []);

  const toggleSection = (section) => {
    setExpandedSection(prev => ({
      ...prev,
      [section]: !prev[section]
    }));
  };

  // Function to render a data block
  const renderEndpoint = (title, path, method, dataState, section) => {
    const statusColor = dataState.error 
      ? 'text-red-600 bg-red-50' 
      : !dataState.loading && dataState.data 
        ? 'text-green-600 bg-green-50' 
        : 'text-gray-600 bg-gray-50';

    const statusText = dataState.loading 
      ? 'LOADING' 
      : dataState.error 
        ? 'ERROR' 
        : dataState.data 
          ? 'SUCCESS' 
          : 'NO DATA';

    return (
      <div className="mb-4 border rounded-md overflow-hidden">
        <div className="flex items-center border-b p-4 bg-white cursor-pointer" onClick={() => toggleSection(section)}>
          <div className={`px-2 py-1 rounded text-xs font-medium ${method === 'GET' ? 'bg-blue-100 text-blue-700' : 'bg-green-100 text-green-700'} mr-3`}>
            {method}
          </div>
          <div className="font-medium text-gray-700">{path}</div>
          <div className="ml-auto text-gray-500 text-sm">{title}</div>
          <div className={`ml-4 px-2 py-1 rounded text-xs font-medium ${statusColor}`}>
            {statusText}
          </div>
        </div>
        
        {expandedSection[section] && (
          <div className="p-4 bg-gray-50 border-b">
            {dataState.loading ? (
              <div className="text-center py-4">
                <div className="inline-block animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600"></div>
                <p className="mt-2 text-gray-600 text-sm">Loading data...</p>
              </div>
            ) : dataState.error ? (
              <div className="bg-red-50 p-3 rounded border border-red-100">
                <div className="font-medium text-red-700 mb-1">Request failed</div>
                <div className="text-red-600 text-sm">{dataState.error}</div>
              </div>
            ) : !dataState.data || (Array.isArray(dataState.data) && dataState.data.length === 0) ? (
              <div className="text-center py-4 text-gray-500 text-sm">
                No data available
              </div>
            ) : (
              <div>
                <div className="mb-3">
                  <div className="text-sm font-medium text-gray-700 mb-1">Response</div>
                  <div className="bg-gray-900 p-3 rounded text-white overflow-auto max-h-64 text-xs font-mono">
                    <pre>{JSON.stringify(dataState.data, null, 2)}</pre>
                  </div>
                </div>
                <div>
                  <div className="text-sm font-medium text-gray-700 mb-1">Response Schema</div>
                  <div className="text-xs text-gray-600">
                    {Array.isArray(dataState.data) 
                      ? `Array[${dataState.data.length}]` 
                      : 'Object'}
                  </div>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    );
  };

  const allHaveErrors = Object.values(data).every(item => item.error !== null && !item.loading);

  return (
    <div className="p-6 bg-white">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">API Documentation</h1>
        <p className="text-gray-600">
          This page provides documentation for all available API endpoints in the system.
          Click on each endpoint to view request and response details.
        </p>
      </div>

      {allHaveErrors && (
        <div className="mb-6 p-4 bg-red-50 border border-red-100 rounded text-red-700">
          <h3 className="font-bold">Connection Error</h3>
          <p className="mt-2 text-sm">
            Unable to connect to the backend API. Please ensure your backend server is running.
          </p>
          <p className="mt-2 text-sm">
            The application is trying to connect to: <code className="bg-red-100 px-2 py-1 rounded">{axios.defaults.baseURL || 'http://localhost:8080'}</code>
          </p>
        </div>
      )}

      <div className="mb-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-3">Patient Endpoints</h2>
        {renderEndpoint('Get all patients', '/api/patients', 'GET', data.patients, 'patients')}
      </div>

      <div className="mb-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-3">Doctor Endpoints</h2>
        {renderEndpoint('Get all doctors', '/api/doctors', 'GET', data.doctors, 'doctors')}
      </div>

      <div className="mb-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-3">Appointment Endpoints</h2>
        {renderEndpoint('Get all appointments', '/api/appointments', 'GET', data.appointments, 'appointments')}
      </div>

      <div className="mb-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-3">Odontogram Endpoints</h2>
        {renderEndpoint('Get patient odontogram', '/api/patients/{id}/odontogram', 'GET', data.odontogram, 'odontogram')}
      </div>
    </div>
  );
}

export default HomePage; 