Odoonto — Dominio de negocio
Este documento describe la capa de dominio de Odoonto, la plataforma para clínicas dentales que estamos desarrollando. Incluye el alcance funcional, la organización en bounded-contexts y las convenciones que garantizan un modelo mantenible, probado y alineado con DDD.

1. Alcance funcional
Área	Responsabilidad principal
Pacientes	Datos personales, alergias, balance económico, historial de alta/baja.
Agenda	Planificación diaria de citas en franjas de 30 min; evita solapamientos y emite recordatorios.
Historial clínico	Odontograma digital (dientes, lesiones, tratamientos pasados y planificados) y entradas médicas.
Profesionales	Registro de doctores, especialidad y horario base.
Catálogo	Tarifa oficial de tratamientos y duraciones estándar.

En síntesis: la solución cubre quién viene, cuándo, con qué problema y con qué profesional, manteniendo coherencia clínica y financiera.

2. Visión DDD
2.1 Bounded-contexts
text
Copy
Edit
patients   ─┐
records    ─┤→  eventos  →  scheduling
staff      ─┘            ↘  catálogo (precios)
shared         (Kernel transversal: Money, EventId, Duration…)
patients : ciclo de vida del paciente.

records : historial clínico y odontograma.

scheduling : agenda diaria y gestión de disponibilidad.

staff : doctores y turnos base.

catalog : tratamientos y precios.

shared : objetos de valor y eventos comunes.

2.2 Comunicación
Solo por ID o Domain Events (EventId + TimestampValue).

Sin referencias directas a clases de otro contexto.

3. Estructura de carpetas
text
Copy
Edit
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
Cada agregado vive en su contexto y encapsula sus invariantes.

Las fábricas (OdontogramFactory, DefaultScheduleFactory, …) mantienen la lógica de construcción compleja fuera de los constructores.

Las políticas (SchedulingPolicy, MedicalRecordPolicy) concentran reglas reutilizables sin dependencias externas.

4. Principios y convenciones aplicadas
Categoría	Norma clave
Arquitectura	Driven Domain Design; el core no depende de frameworks.
SOLID	SRP, DIP e ISP validados por pruebas de arquitectura.
Clean Code	Métodos ≤ 30 líneas, nombres expresivos, sin duplicación.
Eventos	Todos los eventos implementan DomainEvent e incluyen EventId y TimestampValue.
Tests automáticos	10 reglas ArchUnit + Checkstyle bloquean violaciones de estructura y estilo.

5. Casos de uso esenciales
Caso	Flujo
Registro de paciente	PatientAggregate.register() → dispara PatientRegisteredEvent → records crea un MedicalRecordAggregate con odontograma inicial.
Reserva de cita	AppointmentAggregate.schedule() → SchedulingPolicy valida disponibilidad → emite AppointmentScheduledEvent.
Finalizar tratamiento	TreatmentEntity.complete() dentro de la cita → TreatmentCompletedEvent actualiza odontograma y balance del paciente.

6. Cómo validar la arquitectura
bash
Copy
Edit
# Maven
mvn test -Parchitecture

# Gradle
./gradlew test --tests "*architecture*"
Todos los tests deben pasar en verde antes de aceptar un merge request.

7. Guía rápida para contribuir
Ubica tu clase en el contexto y subcarpeta correctos; el nombre debe seguir la convención (*Aggregate, *Entity, *Value).

No uses anotaciones Spring ni JPA dentro de domain/.

Publica eventos cuando cambies estado observable del agregado.

Ejecuta ArchUnit + Checkstyle localmente antes de hacer push.

Si los tests aprueban y la convención se mantiene, tu contribución encajará en el dominio sin fricción.


2/2







