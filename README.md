# Odoonto - Sistema de Gestión para Clínicas Dentales

## Descripción

Odoonto es un Producto Mínimo Viable (MVP) para la gestión integral de clínicas dentales. El sistema está dividido en dos componentes principales:

- **Odoonto.Back**: Backend desarrollado con Spring Boot siguiendo una arquitectura Domain-Driven Design (DDD)
- **Odoonto.Front**: Frontend desarrollado con React y Vite

## Instrucciones Detalladas de Ejecución

### Requisitos Previos
Antes de comenzar, asegúrate de tener instalado:
1. Java 21 o superior
2. Maven 3.8.0 o superior
3. Node.js 18.0.0 o superior
4. Un editor de código (recomendamos Visual Studio Code)

### Paso 1: Iniciar el Backend

#### Opción A: Usando Terminal (Recomendado para desarrollo)
1. Abre una terminal (PowerShell o CMD en Windows)
2. Navega hasta la carpeta del backend:
   ```bash
   cd Odoonto.Back
   ```
3. Asegúrate de tener el archivo de credenciales de Google Cloud:
   - El archivo debe llamarse `service-account.json`
   - Debe estar en la raíz de la carpeta `Odoonto.Back`
4. Instala las dependencias (solo la primera vez):
   ```bash
   mvn clean install
   ```
5. Inicia el servidor:
   ```bash
   mvn spring-boot:run
   ```
6. Espera a que aparezca un mensaje indicando que el servidor está corriendo
7. El backend estará disponible en: `http://localhost:8080`
8. Verifica que el backend está funcionando visitando:
   ```
   http://localhost:8080/swagger-ui.html
   ```

#### Opción B: Usando IntelliJ IDEA
1. Abre IntelliJ IDEA
2. Selecciona "Open" o "File > Open"
3. Navega hasta la carpeta `Odoonto.Back` y selecciónala
4. Espera a que IntelliJ indexe el proyecto y descargue las dependencias
5. Asegúrate de tener el archivo de credenciales de Google Cloud:
   - El archivo debe llamarse `service-account.json`
   - Debe estar en la raíz de la carpeta `Odoonto.Back`
6. Localiza la clase principal:
   - Abre el proyecto en el panel de Project
   - Navega a `src/main/java/odoonto`
   - Busca la clase `OdoontoApplication.java`
7. Para ejecutar el proyecto:
   - Haz clic derecho en `OdoontoApplication.java`
   - Selecciona "Run 'OdoontoApplication'"
   - O usa el atajo de teclado (Shift + F10 en Windows)
8. Espera a que aparezca el mensaje de inicio en la consola de IntelliJ
9. El backend estará disponible en: `http://localhost:8080`
10. Verifica que el backend está funcionando visitando:
    ```
    http://localhost:8080/swagger-ui.html
    ```

#### Configuración Adicional en IntelliJ
1. Configuración de Java:
   - Ve a "File > Project Structure"
   - En "Project", asegúrate de que:
     - Project SDK está configurado a Java 21
     - Language level está configurado a 21
2. Configuración de Maven:
   - Abre la ventana de Maven (View > Tool Windows > Maven)
   - Verifica que todas las dependencias se han descargado correctamente
3. Configuración de Run/Debug:
   - Ve a "Run > Edit Configurations"
   - Verifica que la configuración de Spring Boot está correcta
   - Asegúrate de que la clase principal es `OdoontoApplication`

### Paso 2: Iniciar el Frontend
1. Abre una nueva terminal (mantén la anterior corriendo)
2. Navega hasta la carpeta del frontend:
   ```bash
   cd Odoonto.Front
   ```
3. Instala las dependencias (solo la primera vez):
   ```bash
   npm install
   ```
4. Inicia el servidor de desarrollo:
   ```bash
   npm run dev
   ```
5. Espera a que aparezca un mensaje indicando que el servidor está corriendo
6. Abre tu navegador y ve a: `http://localhost:5173`
7. Deberías ver la interfaz de Odoonto

### Verificación de la Instalación
1. El backend debe estar corriendo en `http://localhost:8080`
2. El frontend debe estar corriendo en `http://localhost:5173`
3. Puedes verificar que todo funciona correctamente:
   - Backend: Visita `http://localhost:8080/swagger-ui.html`
   - Frontend: Visita `http://localhost:5173`

### Solución de Problemas Comunes

#### Backend
- Si ves errores de Java:
  1. Verifica que tienes Java 21 instalado: `java -version`
  2. Asegúrate de que JAVA_HOME está configurado correctamente
- Si ves errores de Maven:
  1. Verifica que tienes Maven instalado: `mvn -version`
  2. Asegúrate de que MAVEN_HOME está configurado correctamente
- Si el puerto 8080 está ocupado:
  1. Abre `application.properties`
  2. Cambia el puerto en la configuración
- Si hay problemas con las credenciales de Google Cloud:
  1. Verifica que el archivo `service-account.json` existe
  2. Asegúrate de que está en la ubicación correcta
  3. Verifica que las credenciales son válidas

#### Frontend
- Si ves errores de dependencias:
  1. Elimina la carpeta `node_modules`
  2. Elimina el archivo `package-lock.json`
  3. Ejecuta `npm install` nuevamente
- Si el puerto 5173 está ocupado:
  1. Abre `vite.config.js`
  2. Cambia el puerto en la configuración
- Si no puede conectarse al backend:
  1. Verifica que el backend está corriendo
  2. Comprueba que el puerto 8080 está accesible
  3. Verifica la configuración de la URL del backend en el frontend

### Notas Importantes
- SIEMPRE inicia primero el backend y luego el frontend
- Mantén ambas terminales abiertas mientras uses la aplicación
- El frontend necesita el backend para funcionar correctamente
- Si cierras alguna de las terminales, necesitarás reiniciar ese componente
- Si reinicias el backend, es posible que necesites refrescar la página del frontend

## Documentación

### Backend (Odoonto.Back)

- [Documentación principal del Backend](Odoonto.Back/README.md)
  - [Capa de Dominio](Odoonto.Back/src/main/java/odoonto/domain/README.md) - Entidades, agregados y reglas de negocio
  - [Capa de Aplicación](Odoonto.Back/src/main/java/odoonto/application/README.md) - Casos de uso e implementación
  - [Capa de Infraestructura](Odoonto.Back/src/main/java/odoonto/infrastructure/README.md) - Persistencia y servicios externos
  - [Capa de Presentación](Odoonto.Back/src/main/java/odoonto/presentation/README.md) - API REST y controladores

### Frontend (Odoonto.Front)

- [Documentación del Frontend](Odoonto.Front/README.md)

## Iniciar la aplicación

Para iniciar toda la aplicación (backend y frontend), puede utilizar el script PowerShell incluido:

```powershell
.\iniciar-aplicacion.ps1
```

## Desarrollo

Para instrucciones detalladas sobre cómo configurar y desarrollar cada componente, consulte los READMEs específicos de cada módulo. 