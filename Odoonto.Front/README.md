# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.

# Odoonto Frontend

## Requisitos Previos
- Node.js 18.0.0 o superior
- npm 9.0.0 o superior

## Instrucciones de Instalación y Ejecución

### 1. Configuración del Entorno
1. Asegúrate de tener Node.js instalado. Puedes verificarlo ejecutando:
   ```bash
   node --version
   ```

2. Verifica que npm esté instalado:
   ```bash
   npm --version
   ```

### 2. Instalación de Dependencias
1. Abre una terminal en la carpeta raíz del proyecto (Odoonto.Front)
2. Ejecuta el siguiente comando para instalar todas las dependencias:
   ```bash
   npm install
   ```

### 3. Ejecución del Proyecto
1. Para iniciar el servidor de desarrollo, ejecuta:
   ```bash
   npm run dev
   ```

2. La aplicación se iniciará por defecto en `http://localhost:5173`

3. Para construir la versión de producción:
   ```bash
   npm run build
   ```

4. Para previsualizar la versión de producción:
   ```bash
   npm run preview
   ```

### Solución de Problemas Comunes
- Si encuentras errores de dependencias, intenta eliminar la carpeta `node_modules` y el archivo `package-lock.json`, luego ejecuta `npm install` nuevamente
- Si hay problemas con el puerto, puedes cambiarlo en el archivo `vite.config.js`
- Para problemas de compilación, asegúrate de tener todas las dependencias instaladas correctamente
