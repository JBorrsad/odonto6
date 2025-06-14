# Odoonto Domain Layer

## Arquitectura del Dominio

La capa de dominio de Odoonto implementa Domain-Driven Design (DDD) con bounded contexts claramente separados para gestionar la complejidad de una plataforma de clínica dental.

### Visión General del Dominio

```mermaid
%%{init: {'theme':'neutral'}}%%
graph TB
    subgraph "🏥 ODOONTO DOMAIN LAYER"
        subgraph "👥 Patients Context"
            PA["&lt;&lt;aggregate&gt;&gt;<br/>PatientAggregate"]
            PID["&lt;&lt;valueObject&gt;&gt;<br/>PatientId"]
            PN["&lt;&lt;valueObject&gt;&gt;<br/>PersonName"]
            EMAIL["&lt;&lt;valueObject&gt;&gt;<br/>EmailAddress"]
            PHONE["&lt;&lt;valueObject&gt;&gt;<br/>PhoneNumber"]
            ADDR["&lt;&lt;valueObject&gt;&gt;<br/>AddressValue"]
            BIRTH["&lt;&lt;valueObject&gt;&gt;<br/>BirthDateValue"]
            SEX["&lt;&lt;enumeration&gt;&gt;<br/>SexValue"]
            ALLERGY["&lt;&lt;valueObject&gt;&gt;<br/>AllergyValue"]
        end
        
        subgraph "📋 Records Context"
            MRA["&lt;&lt;aggregate&gt;&gt;<br/>MedicalRecordAggregate"]
            OE["&lt;&lt;entity&gt;&gt;<br/>OdontogramEntity"]
            TE["&lt;&lt;entity&gt;&gt;<br/>ToothEntity"]
            LE["&lt;&lt;entity&gt;&gt;<br/>LesionEntity"]
            TRE["&lt;&lt;entity&gt;&gt;<br/>TreatmentEntity"]
            MEE["&lt;&lt;entity&gt;&gt;<br/>MedicalEntryEntity"]
            OF["&lt;&lt;factory&gt;&gt;<br/>OdontogramFactory"]
            TPF["&lt;&lt;factory&gt;&gt;<br/>TreatmentPlanFactory"]
        end
        
        subgraph "📅 Scheduling Context"
            AA["&lt;&lt;aggregate&gt;&gt;<br/>AppointmentAggregate"]
            ACE["&lt;&lt;entity&gt;&gt;<br/>AvailabilityCalendarEntity"]
            AT["&lt;&lt;valueObject&gt;&gt;<br/>AppointmentTime"]
            AS["&lt;&lt;enumeration&gt;&gt;<br/>AppointmentStatus"]
            TSV["&lt;&lt;valueObject&gt;&gt;<br/>TimeSlotValue"]
            SP["&lt;&lt;policy&gt;&gt;<br/>SchedulingPolicy"]
        end
        
        subgraph "👨‍⚕️ Staff Context"
            DA["&lt;&lt;aggregate&gt;&gt;<br/>DoctorAggregate"]
            DID["&lt;&lt;valueObject&gt;&gt;<br/>DoctorId"]
            SPEC["&lt;&lt;enumeration&gt;&gt;<br/>SpecialtyValue"]
            SCHED["&lt;&lt;valueObject&gt;&gt;<br/>ScheduleValue"]
            DSF["&lt;&lt;factory&gt;&gt;<br/>DefaultScheduleFactory"]
        end
        
        subgraph "💰 Catalog Context"
            TCA["&lt;&lt;aggregate&gt;&gt;<br/>TreatmentCatalogAggregate"]
            TPE["&lt;&lt;entity&gt;&gt;<br/>TreatmentPricingEntity"]
            CID["&lt;&lt;valueObject&gt;&gt;<br/>CatalogId"]
        end
        
        subgraph "🔗 Shared Context"
            MV["&lt;&lt;valueObject&gt;&gt;<br/>MoneyValue"]
            EID["&lt;&lt;valueObject&gt;&gt;<br/>EventId"]
            DV["&lt;&lt;valueObject&gt;&gt;<br/>DurationValue"]
            TV["&lt;&lt;valueObject&gt;&gt;<br/>TimestampValue"]
        end
    end
    
    subgraph "⚙️ Domain Services"
        PVS["&lt;&lt;service&gt;&gt;<br/>PatientValidationService"]
        AVS["&lt;&lt;service&gt;&gt;<br/>AvailabilityService"]
        TPS["&lt;&lt;service&gt;&gt;<br/>TreatmentPlanService"]
        SGS["&lt;&lt;service&gt;&gt;<br/>ScheduleGeneratorService"]
    end
    
    subgraph "⚡ Domain Events"
        PRE["&lt;&lt;event&gt;&gt;<br/>PatientRegisteredEvent"]
        ASE["&lt;&lt;event&gt;&gt;<br/>AppointmentScheduledEvent"]
        ASCE["&lt;&lt;event&gt;&gt;<br/>AppointmentStatusChangedEvent"]
        TCE["&lt;&lt;event&gt;&gt;<br/>TreatmentCompletedEvent"]
        DCE["&lt;&lt;event&gt;&gt;<br/>DoctorCreatedEvent"]
        MRCE["&lt;&lt;event&gt;&gt;<br/>MedicalRecordCreatedEvent"]
    end
    
    %% Cross-context relationships (only by ID)
    PA -.->|PatientId| AA
    DA -.->|DoctorId| AA
    MRA -.->|PatientId| PA
    
    %% Shared dependencies
    PA --> MV
    AA --> MV
    AA --> DV
    TCA --> MV
    TCA --> DV
```
```

## Bounded Contexts

### 👥 Patients Context

Gestiona toda la información relacionada con los pacientes de la clínica.

**Componentes principales:**
- `PatientAggregate`: Raíz de agregado que encapsula la información del paciente
- Value Objects: `PatientId`, `PersonName`, `EmailAddress`, `PhoneNumber`, `AddressValue`, `BirthDateValue`, `SexValue`, `AllergyValue`

<!-- DIAGRAM: patients-context -->

### 📋 Records Context

Maneja los registros médicos y odontológicos de los pacientes.

**Componentes principales:**
- `MedicalRecordAggregate`: Raíz de agregado para historiales médicos
- Entidades: `OdontogramEntity`, `ToothEntity`, `LesionEntity`, `TreatmentEntity`, `MedicalEntryEntity`
- Factories: `OdontogramFactory`, `TreatmentPlanFactory`

<!-- DIAGRAM: records-context -->

### 📅 Scheduling Context

Gestiona las citas y disponibilidad de la clínica.

**Componentes principales:**
- `AppointmentAggregate`: Raíz de agregado para citas
- `AvailabilityCalendarEntity`: Gestión de disponibilidad
- Value Objects: `AppointmentTime`, `AppointmentStatus`, `TimeSlotValue`
- `SchedulingPolicy`: Políticas de programación

<!-- DIAGRAM: scheduling-context -->

### 👨‍⚕️ Staff Context

Administra el personal médico y sus horarios.

**Componentes principales:**
- `DoctorAggregate`: Raíz de agregado para doctores
- Value Objects: `DoctorId`, `SpecialtyValue`, `ScheduleValue`
- `DefaultScheduleFactory`: Factory para horarios por defecto

<!-- DIAGRAM: staff-context -->

### 💰 Catalog Context

Gestiona el catálogo de tratamientos y precios.

**Componentes principales:**
- `TreatmentCatalogAggregate`: Raíz de agregado para catálogo
- `TreatmentPricingEntity`: Entidad de precios
- Value Objects: `CatalogId`

<!-- DIAGRAM: catalog-context -->

### 🔗 Shared Context

Elementos compartidos entre todos los bounded contexts.

**Componentes principales:**
- Value Objects compartidos: `MoneyValue`, `EventId`, `DurationValue`, `TimestampValue`

<!-- DIAGRAM: shared-context -->

## Servicios de Dominio

- `PatientValidationService`: Validaciones específicas de pacientes
- `AvailabilityService`: Lógica de disponibilidad de citas
- `TreatmentPlanService`: Planificación de tratamientos
- `ScheduleGeneratorService`: Generación de horarios

## Eventos de Dominio

- `PatientRegisteredEvent`: Paciente registrado
- `AppointmentScheduledEvent`: Cita programada
- `AppointmentStatusChangedEvent`: Estado de cita cambiado
- `TreatmentCompletedEvent`: Tratamiento completado
- `DoctorCreatedEvent`: Doctor creado
- `MedicalRecordCreatedEvent`: Historial médico creado

## Principios Arquitectónicos

### Separación de Bounded Contexts
- Cada contexto es independiente y se comunica solo por eventos
- Referencias entre contextos únicamente por ID
- Shared context para elementos verdaderamente compartidos

### Inmutabilidad
- Todos los Value Objects son inmutables
- Campos final y sin setters
- Validación en constructores

### Factories de Dominio
- Para objetos complejos: `OdontogramFactory`, `TreatmentPlanFactory`, `DefaultScheduleFactory`
- Métodos estáticos que devuelven objetos inmutables

### Eventos con Trazabilidad
- Todos los eventos incluyen `EventId` y `TimestampValue`
- Garantizan idempotencia y auditabilidad

## Validación de Arquitectura

Este dominio está validado por tests de arquitectura que garantizan:
- Cumplimiento de DDD y jMolecules
- Separación correcta de bounded contexts
- Inmutabilidad de Value Objects
- Nomenclatura consistente
- Principios SOLID y Clean Code 