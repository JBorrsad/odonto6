# Cambios en los Servicios del Frontend

## Resumen de Adaptaciones

Este documento describe los cambios realizados en los servicios del frontend para adaptarlos a los nuevos endpoints del backend.

## Cambios Realizados

### 1. **PatientService.js**
- ✅ **Añadido**: Función `search()` para búsqueda de pacientes por nombre/apellido
- ✅ **Eliminado**: Funciones `addLesions()` y `removeLesions()` (movidas a odontogramService)
- ✅ **Mantenido**: Función `getOdontogram()` que usa el endpoint `/api/patients/{id}/odontogram`

### 2. **OdontogramService.js**
- ✅ **Actualizado**: Endpoints para usar la nueva estructura del backend:
  - `POST /api/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions`
  - `DELETE /api/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions`
- ✅ **Añadido**: Funciones para tratamientos:
  - `addTreatment()`
  - `removeTreatment()`
- ✅ **Añadido**: Función `getOdontogramById()` para obtener odontograma por ID
- ✅ **Actualizado**: Parámetros de funciones para usar `odontogramId` y `toothNumber` en lugar de `patientId` y `toothId`

### 3. **AppointmentService.js**
- ✅ **Eliminado**: Función `book()` (endpoint no existe en backend)
- ✅ **Añadido**: Función `confirm()` para confirmar citas
- ✅ **Añadido**: Función `cancel()` para cancelar citas con motivo opcional
- ✅ **Añadido**: Función `getByDoctorAndDateRange()` que usa el endpoint específico del backend
- ✅ **Actualizado**: Función `getByPatient()` para usar el endpoint específico `/api/appointments/patient/{patientId}`
- ✅ **Mantenido**: Funciones auxiliares `getByDate()` y `getByDoctor()` para compatibilidad

### 4. **DoctorService.js**
- ✅ **Añadido**: Función `search()` para búsqueda por nombre
- ✅ **Añadido**: Función `getByEspecialidad()` para búsqueda por especialidad

### 5. **MedicalRecordService.js**
- ✅ **Creado**: Nuevo servicio completo con funciones:
  - `getAll()` - Obtener todos los historiales
  - `getById()` - Obtener historial por ID
  - `getByPatientId()` - Obtener historial de un paciente
  - `addEntry()` - Añadir entrada al historial

### 6. **api.js**
- ✅ **Refactorizado**: Eliminadas funciones duplicadas
- ✅ **Actualizado**: Ahora re-exporta servicios modulares para mantener compatibilidad
- ✅ **Eliminado**: Endpoints incorrectos que no existen en el backend

## Endpoints del Backend Utilizados

### Pacientes
- `GET /api/patients` - Obtener todos
- `GET /api/patients/{id}` - Obtener por ID
- `POST /api/patients` - Crear
- `PUT /api/patients/{id}` - Actualizar
- `DELETE /api/patients/{id}` - Eliminar
- `GET /api/patients/search?query={query}` - Buscar
- `GET /api/patients/{id}/odontogram` - Obtener odontograma

### Doctores
- `GET /api/doctors` - Obtener todos
- `GET /api/doctors/{id}` - Obtener por ID
- `POST /api/doctors` - Crear
- `PUT /api/doctors/{id}` - Actualizar
- `DELETE /api/doctors/{id}` - Eliminar
- `GET /api/doctors/search?query={query}` - Buscar por nombre
- `GET /api/doctors/especialidad/{especialidad}` - Buscar por especialidad

### Citas
- `GET /api/appointments` - Obtener todas
- `GET /api/appointments/{id}` - Obtener por ID
- `POST /api/appointments` - Crear
- `PUT /api/appointments/{id}` - Actualizar
- `DELETE /api/appointments/{id}` - Eliminar
- `PUT /api/appointments/{id}/confirm` - Confirmar
- `DELETE /api/appointments/{id}/cancel?reason={reason}` - Cancelar
- `GET /api/appointments/doctor/{doctorId}?from={date}&to={date}` - Por doctor y rango
- `GET /api/appointments/patient/{patientId}` - Por paciente

### Odontogramas
- `GET /api/odontograms/{id}` - Obtener por ID
- `POST /api/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions?lesionType={type}` - Añadir lesión
- `DELETE /api/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions` - Eliminar lesión
- `POST /api/odontograms/{id}/teeth/{toothNumber}/treatments?treatmentType={type}` - Añadir tratamiento
- `DELETE /api/odontograms/{id}/teeth/{toothNumber}/treatments` - Eliminar tratamiento

### Historiales Médicos
- `GET /api/medical-records` - Obtener todos
- `GET /api/medical-records/{id}` - Obtener por ID
- `GET /api/patients/{patientId}/medical-record` - Obtener por paciente
- `POST /api/medical-records/{id}/entries?entryDescription={description}` - Añadir entrada

## Notas Importantes

1. **Cambio en estructura de odontograma**: Los endpoints ahora requieren el ID del odontograma en lugar del ID del paciente para operaciones de lesiones y tratamientos.

2. **Nuevas funciones de citas**: Se añadieron funciones para confirmar y cancelar citas que antes no existían.

3. **Búsquedas mejoradas**: Tanto pacientes como doctores ahora tienen funciones de búsqueda que usan los endpoints específicos del backend.

4. **Compatibilidad mantenida**: Las funciones auxiliares se mantienen para no romper código existente que las use.

5. **Servicio de historiales médicos**: Se creó un servicio completamente nuevo para manejar historiales médicos.

## Próximos Pasos

- Verificar que todos los componentes del frontend usen las nuevas funciones correctamente
- Actualizar cualquier código que use las funciones eliminadas
- Probar la integración completa con el backend 