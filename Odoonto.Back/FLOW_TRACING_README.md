# 🔍 Sistema de Trazado de Flujo de Clases - Odoonto

## ¿Qué es?

El **Sistema de Trazado** es una herramienta que intercepta automáticamente **todos los métodos** de tu aplicación y muestra en tiempo real el **flujo de ejecución** entre las diferentes capas de la arquitectura DDD.

## ✨ Características

### 🎯 **Trazado Automático por Capas**
- 🌐 **CONTROLLER** (Azul) - Controladores REST
- ⚙️ **SERVICE** (Verde) - Servicios de aplicación  
- 🔄 **DTO/MAPPER** (Cian) - DTOs y mappers
- 🗄️ **REPOSITORY** (Magenta) - Repositorios
- 🏛️ **DOMAIN** (Amarillo) - Agregados y entidades
- 💎 **VALUE_OBJECT** (Blanco) - Value Objects
- 🎯 **USE_CASE** (Rojo) - Casos de uso

### 📊 **Información Detallada**
- ✅ **Entrada y salida** de cada método
- 🕒 **Tiempo de ejecución** 
- 📋 **Argumentos y resultados**
- ❌ **Trazado de errores**
- 🔢 **IDs únicos** por request
- 🐌/⚡ **Indicadores de rendimiento**

## 🚀 Cómo Usar

### 1. **Iniciar la Aplicación**
```bash
./mvnw spring-boot:run
```

### 2. **Probar el Trazado**

#### **Opción A: Endpoints de Prueba**
```bash
# Prueba simple
curl http://localhost:8080/api/tracing/test-simple

# Prueba compleja  
curl -X POST http://localhost:8080/api/tracing/test-complex

# Prueba de errores
curl http://localhost:8080/api/tracing/test-error
```

#### **Opción B: Usar tus Endpoints Existentes**
Cualquier request a tus controladores existentes mostrará el trazado:
```bash
# Crear un paciente (si tienes el endpoint)
curl -X POST http://localhost:8080/api/patients -H "Content-Type: application/json" -d '{...}'

# Obtener citas
curl http://localhost:8080/api/appointments
```

### 3. **Ver el Trazado en los Logs**

Cuando hagas una request, verás algo así:

```
════════════════════════════════════════════════════════════════
🚀 NUEVA REQUEST: REQ-1 - Thread: http-nio-8080-exec-1
════════════════════════════════════════════════════════════════
┌─ 🌐 CONTROLLER REQ-1 → TracingTestController.testSimpleTrace()
  ┌─ 🌐 CONTROLLER REQ-1 → TracingTestController.simulateBusinessLogic()
    ┌─ 🌐 CONTROLLER REQ-1 → TracingTestController.processData("datos de ejemplo")
    └─ 🌐 CONTROLLER REQ-1 ← TracingTestController.processData() ✅ "Procesado: datos de ejemplo" (2ms) ⚡
  └─ 🌐 CONTROLLER REQ-1 ← TracingTestController.simulateBusinessLogic() ✅ null (4ms) ⚡
└─ 🌐 CONTROLLER REQ-1 ← TracingTestController.testSimpleTrace() ✅ ResponseEntity (15ms) ⚡
🏁 FIN REQUEST: REQ-1 - Tiempo total: 15ms ⚡
════════════════════════════════════════════════════════════════
```

## ⚙️ Configuración

### **Activar/Desactivar Dinámicamente**
```bash
# Activar trazado
curl -X POST "http://localhost:8080/api/tracing/toggle?enabled=true"

# Desactivar trazado  
curl -X POST "http://localhost:8080/api/tracing/toggle?enabled=false"
```

### **Configurar Capas Específicas**
```bash
# Solo trazar controladores y servicios
curl -X POST "http://localhost:8080/api/tracing/configure?controllers=true&services=true&repositories=false&domain=false"
```

### **Ver Estado Actual**
```bash
curl http://localhost:8080/api/tracing/status
```

### **Configuración en application.properties**
```properties
# Activar/desactivar globalmente
odoonto.tracing.enabled=true

# Controlar por capas
odoonto.tracing.trace-controllers=true
odoonto.tracing.trace-services=true
odoonto.tracing.trace-repositories=true
odoonto.tracing.trace-domain=true
odoonto.tracing.trace-dtos-mappers=true
odoonto.tracing.trace-use-cases=true

# Configuración avanzada
odoonto.tracing.max-depth=10
odoonto.tracing.slow-method-threshold=100
```

## 📋 Ejemplo de Flujo Completo

Cuando crees un **paciente**, verás un flujo como:

```
════════════════════════════════════════════════════════════════
🚀 NUEVA REQUEST: REQ-2 - Thread: http-nio-8080-exec-2
════════════════════════════════════════════════════════════════
┌─ 🌐 CONTROLLER REQ-2 → PatientController.createPatient(PatientDto[id=null])
  ┌─ ⚙️ SERVICE REQ-2 → PatientService.createPatient(PatientDto[id=null])
    ┌─ 🔄 DTO/MAPPER REQ-2 → PatientMapper.toEntity(PatientDto[id=null])
      ┌─ 🏛️ DOMAIN REQ-2 → Patient.<init>("Juan", "Pérez", LocalDate, ...)
        ┌─ 💎 VALUE_OBJECT REQ-2 → EmailAddress.validate("juan@email.com")
        └─ 💎 VALUE_OBJECT REQ-2 ← EmailAddress.validate() ✅ null (3ms) ⚡
      └─ 🏛️ DOMAIN REQ-2 ← Patient.<init>() ✅ Patient[id=abc123...] (8ms) ⚡
    └─ 🔄 DTO/MAPPER REQ-2 ← PatientMapper.toEntity() ✅ Patient[id=abc123...] (12ms) ⚡
    ┌─ 🗄️ REPOSITORY REQ-2 → PatientRepository.save(Patient[id=abc123...])
    └─ 🗄️ REPOSITORY REQ-2 ← PatientRepository.save() ✅ Patient[id=abc123...] (45ms) ⚡
    ┌─ 🔄 DTO/MAPPER REQ-2 → PatientMapper.toDto(Patient[id=abc123...])
    └─ 🔄 DTO/MAPPER REQ-2 ← PatientMapper.toDto() ✅ PatientDto[id=abc123...] (2ms) ⚡
  └─ ⚙️ SERVICE REQ-2 ← PatientService.createPatient() ✅ PatientDto[id=abc123...] (67ms) ⚡
└─ 🌐 CONTROLLER REQ-2 ← PatientController.createPatient() ✅ ResponseEntity (72ms) ⚡
🏁 FIN REQUEST: REQ-2 - Tiempo total: 72ms ⚡
════════════════════════════════════════════════════════════════
```

## 🔧 Solución de Problemas

### **No veo trazas**
1. Verifica que esté activado: `curl http://localhost:8080/api/tracing/status`
2. Revisa `application.properties`: `odoonto.tracing.enabled=true`
3. Asegúrate de hacer requests a endpoints que existan

### **Demasiadas trazas**
- Desactiva capas específicas: `/api/tracing/configure?domain=false`
- Reduce profundidad máxima en `application.properties`

### **Faltan colores**
- Asegúrate de que tu consola soporte códigos ANSI
- En Windows: usa Windows Terminal o PowerShell 7+

## 🎨 Leyenda de Colores y Símbolos

| Símbolo | Significado |
|---------|-------------|
| 🌐 | Controller (REST endpoints) |
| ⚙️ | Service (Lógica de aplicación) |
| 🔄 | DTO/Mapper (Transformación de datos) |
| 🗄️ | Repository (Acceso a datos) |
| 🏛️ | Domain (Entidades y agregados) |
| 💎 | Value Object (Objetos de valor) |
| 🎯 | Use Case (Casos de uso) |
| ✅ | Éxito |
| ❌ | Error |
| ⚡ | Método rápido (< 100ms) |
| 🐌 | Método lento (≥ 100ms) |
| ┌─ | Entrada a método |
| └─ | Salida de método |

## 🚀 Próximos Pasos

1. **Ejecuta la aplicación** y prueba los endpoints de trazado
2. **Haz requests** a tus endpoints existentes para ver el flujo real
3. **Experimenta** con la configuración para ajustar el nivel de detalle
4. **Úsalo mañana** para debuggear y entender mejor tu aplicación

¡Con esto tendrás visibilidad completa del flujo de tu aplicación! 🎉 