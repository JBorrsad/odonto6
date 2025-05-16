import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import './index.css';

// Configurar interceptor global para axios (opcional)
import axios from 'axios';

// Configurar base URL para el entorno de desarrollo
axios.defaults.baseURL = import.meta.env.DEV ? 'http://localhost:8080' : '';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>,
);
