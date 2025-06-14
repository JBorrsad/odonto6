# Odoonto — Dominio de negocio

Este documento describe la capa de dominio de Odoonto, la plataforma para clínicas dentales que estamos desarrollando. Incluye el alcance funcional, la organización en bounded-contexts y las convenciones que garantizan un modelo mantenible, probado y alineado con DDD.

## 📋 Tabla de contenidos

- [1. Alcance funcional](#1-alcance-funcional)
- [2. Visión DDD](#2-visión-ddd)
- [3. Arquitectura general](#3-arquitectura-general)
- [4. Bounded Contexts](#4-bounded-contexts)
- [5. Estructura de carpetas](#5-estructura-de-carpetas)
- [6. Principios y convenciones](#6-principios-y-convenciones)
- [7. Casos de uso esenciales](#7-casos-de-uso-esenciales)
- [8. Validación de arquitectura](#8-validación-de-arquitectura)
- [9. Guía para contribuir](#9-guía-para-contribuir)

## 1. Alcance funcional

| Área | Responsabilidad principal |
|------|---------------------------|
| **Pacientes** | Datos personales, alergias, balance económico, historial de alta/baja |
| **Agenda** | Planificación diaria de citas en franjas de 30 min; evita solapamientos y emite recordatorios |
| **Historial clínico** | Odontograma digital (dientes, lesiones, tratamientos pasados y planificados) y entradas médicas |
| **Profesionales** | Registro de doctores, especialidad y horario base |
| **Catálogo** | Tarifa oficial de tratamientos y duraciones estándar |

En síntesis: la solución cubre **quién viene**, **cuándo**, **con qué problema** y **con qué profesional**, manteniendo coherencia clínica y financiera.

## 2. Visión DDD

### 2.1 Bounded-contexts

```
patients   ─┐
records    ─┤→  eventos  →  scheduling
staff      ─┘            ↘  catalog (precios)
shared         (Kernel transversal: Money, EventId, Duration…)
```

- **patients**: ciclo de vida del paciente
- **records**: historial clínico y odontograma  
- **scheduling**: agenda diaria y gestión de disponibilidad
- **staff**: doctores y turnos base
- **catalog**: tratamientos y precios
- **shared**: objetos de valor y eventos comunes

### 2.2 Comunicación

- Solo por **ID** o **Domain Events** (EventId + TimestampValue)
- Sin referencias directas a clases de otro contexto

## 3. Arquitectura general

<!-- DIAGRAM: documentation/domain/domain-overview.mmd -->

## 4. Bounded Contexts

### Patients Context

Gestiona el ciclo de vida completo del paciente desde el registro hasta la gestión de su información personal y balance económico.

<!-- DIAGRAM: documentation/domain/patients/patients-context.mmd -->

### Records Context

Maneja el historial clínico completo incluyendo odontograma digital, tratamientos y entradas médicas.

<!-- DIAGRAM: documentation/domain/records/records-context.mmd -->

### Scheduling Context

Gestiona la agenda diaria, disponibilidad de doctores y programación de citas.

<!-- DIAGRAM: documentation/domain/scheduling/scheduling-context.mmd -->

### Staff Context

Administra la información de doctores, especialidades y horarios de trabajo.

<!-- DIAGRAM: documentation/domain/staff/staff-context.mmd -->

### Catalog Context

Mantiene el catálogo oficial de tratamientos con precios y duraciones estándar.

<!-- DIAGRAM: documentation/domain/catalog/catalog-context.mmd -->

### Shared Context

Contiene objetos de valor y eventos comunes utilizados por todos los bounded contexts.

<!-- DIAGRAM: documentation/domain/shared/shared-context.mmd -->

## 5. Estructura de carpetas

```
domain/
├─ model/
│   ├─ <context>/aggregates/
│   ├─ <context>/entities/
│   ├─ <context>/valueobjects/
│   ├─ <context>/factory/
│   └─ <context>/policy/
├─ service/            ← Domain Services
├─ repository/         ← Interfaces de persistencia
├─ events/             ← Domain Events por contexto
├─ exceptions/         ← Jerarquía DomainException
└─ specification/      ← Especificaciones de dominio
```

Cada agregado vive en su contexto y encapsula sus invariantes.

Las **fábricas** (OdontogramFactory, DefaultScheduleFactory, …) mantienen la lógica de construcción compleja fuera de los constructores.

Las **políticas** (SchedulingPolicy, MedicalRecordPolicy) concentran reglas reutilizables sin dependencias externas.

## 6. Principios y convenciones

| Categoría | Norma clave |
|-----------|-------------|
| **Arquitectura** | Domain Driven Design; el core no depende de frameworks |
| **SOLID** | SRP, DIP e ISP validados por pruebas de arquitectura |
| **Clean Code** | Métodos ≤ 30 líneas, nombres expresivos, sin duplicación |
| **Eventos** | Todos los eventos implementan DomainEvent e incluyen EventId y TimestampValue |
| **Tests automáticos** | 10 reglas ArchUnit + Checkstyle bloquean violaciones de estructura y estilo |

## 7. Casos de uso esenciales

| Caso | Flujo |
|------|-------|
| **Registro de paciente** | `PatientAggregate.register()` → dispara `PatientRegisteredEvent` → records crea un `MedicalRecordAggregate` con odontograma inicial |
| **Reserva de cita** | `AppointmentAggregate.schedule()` → `SchedulingPolicy` valida disponibilidad → emite `AppointmentScheduledEvent` |
| **Finalizar tratamiento** | `TreatmentEntity.complete()` dentro de la cita → `TreatmentCompletedEvent` actualiza odontograma y balance del paciente |

## 8. Validación de arquitectura

```bash
# Maven
mvn test -Parchitecture

# Gradle
./gradlew test --tests "*architecture*"
```

**Todos los tests deben pasar en verde** antes de aceptar un merge request.

## 9. Guía para contribuir

1. **Ubica tu clase** en el contexto y subcarpeta correctos; el nombre debe seguir la convención (`*Aggregate`, `*Entity`, `*Value`)

2. **No uses anotaciones** Spring ni JPA dentro de `domain/`

3. **Publica eventos** cuando cambies estado observable del agregado

4. **Ejecuta ArchUnit + Checkstyle** localmente antes de hacer push

Si los tests aprueban y la convención se mantiene, tu contribución encajará en el dominio sin fricción.

---

> 📚 **Documentación adicional**: Para más detalles sobre implementación específica, consulta los archivos de documentación en `documentation/`

---

> 🔄 **Auto-generado**: Este README se sincroniza automáticamente con los diagramas. Para actualizar, ejecuta: `node documentation/scripts/sync-diagrams.js` 