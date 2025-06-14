---
description: Estructura estricta obligatoria del dominio DDD con bounded contexts separados
globs: src/main/java/odoonto/domain/**/*.java
alwaysApply: true
---

<rule>
  <meta>
    <title>Domain Structure - Strict DDD Architecture</title>
    <description>Estructura estricta obligatoria del dominio DDD con bounded contexts separados</description>
    <created-at utc-timestamp="1744240920">January 10, 2025, 09:22 AM</created-at>
    <last-updated-at utc-timestamp="1744240920">January 10, 2025, 09:22 AM</last-updated-at>
    <applies-to>
      <file-matcher glob="src/main/java/odoonto/domain/**/*.java">Todos los archivos del dominio</file-matcher>
      <action-matcher action="domain-creation">Creación de clases de dominio</action-matcher>
    </applies-to>
  </meta>

  <requirements>
    <non-negotiable priority="critical">
      <description>Seguir estrictamente la estructura de dominio obligatoria sin crear archivos fuera de la estructura definida.</description>
      <examples>
        <example title="Domain Structure Compliance">
          <correct-example title="Proper domain structure" conditions="Creating domain classes" expected-result="Passes architecture tests" correctness-criteria="Follows exact structure"><![CDATA[
domain/
├─ model/
│   ├─ patients/
│   │   ├─ aggregates/
│   │   │   └─ PatientAggregate.java
│   │   ├─ entities/
│   │   │   └─ AttachmentEntity.java
│   │   └─ valueobjects/
│   │       ├─ PatientId.java
│   │       ├─ EmailAddress.java
│   │       ├─ PhoneNumber.java
│   │       ├─ AddressValue.java
│   │       ├─ BirthDateValue.java
│   │       ├─ SexValue.java
│   │       └─ AllergyValue.java
│   │
│   ├─ records/
│   │   ├─ aggregates/
│   │   │   └─ MedicalRecordAggregate.java
│   │   ├─ entities/
│   │   │   ├─ OdontogramEntity.java
│   │   │   ├─ ToothEntity.java
│   │   │   ├─ LesionEntity.java
│   │   │   ├─ TreatmentEntity.java
│   │   │   └─ MedicalEntryEntity.java
│   │   ├─ valueobjects/
│   │   │   ├─ MedicalRecordId.java
│   │   │   ├─ OdontogramId.java
│   │   │   ├─ ToothNumber.java
│   │   │   ├─ DentitionType.java
│   │   │   ├─ LesionType.java
│   │   │   ├─ TreatmentType.java
│   │   │   ├─ ToothFace.java
│   │   │   ├─ TreatmentId.java
│   │   │   └─ TimestampValue.java
│   │   └─ factory/
│   │       ├─ OdontogramFactory.java
│   │       └─ TreatmentPlanFactory.java
│   │
│   ├─ scheduling/
│   │   ├─ aggregates/
│   │   │   └─ AppointmentAggregate.java
│   │   ├─ entities/
│   │   │   └─ AvailabilityCalendarEntity.java
│   │   ├─ valueobjects/
│   │   │   ├─ AppointmentId.java
│   │   │   ├─ AppointmentTime.java
│   │   │   ├─ DurationValue.java
│   │   │   ├─ AppointmentStatus.java
│   │   │   ├─ TimeSlotVO.java
│   │   │   ├─ PatientId.java
│   │   │   ├─ DoctorId.java
│   │   │   ├─ ToothNumber.java
│   │   │   └─ TreatmentId.java
│   │   └─ policy/
│   │       └─ SchedulingPolicy.java
│   │
│   ├─ staff/
│   │   ├─ aggregates/
│   │   │   └─ DoctorAggregate.java
│   │   ├─ valueobjects/
│   │   │   ├─ DoctorId.java
│   │   │   ├─ SpecialtyValue.java
│   │   │   └─ ScheduleValue.java
│   │   └─ factory/
│   │       └─ DefaultScheduleFactory.java
│   │
│   ├─ catalog/
│   │   ├─ aggregates/
│   │   │   └─ TreatmentCatalogAggregate.java
│   │   ├─ entities/
│   │   │   └─ TreatmentPricingEntity.java
│   │   └─ valueobjects/
│   │       ├─ TreatmentId.java
│   │       ├─ MoneyValue.java
│   │       └─ DurationValue.java
│   │
│   └─ shared/
│       └─ valueobjects/
│           ├─ MoneyValue.java
│           ├─ EventId.java
│           └─ DurationValue.java
│
├─ service/
│   ├─ patients/PatientValidationService.java
│   ├─ scheduling/AvailabilityService.java
│   ├─ records/TreatmentPlanService.java
│   └─ staff/ScheduleGeneratorService.java
│
├─ repository/
│   ├─ patients/PatientRepository.java
│   ├─ scheduling/AppointmentRepository.java
│   ├─ records/MedicalRecordRepository.java
│   ├─ staff/DoctorRepository.java
│   └─ catalog/TreatmentCatalogRepository.java
│
├─ events/
│   ├─ shared/
│   │   ├─ DomainEvent.java
│   │   └─ EventPublisher.java
│   ├─ patients/PatientRegisteredEvent.java
│   ├─ scheduling/
│   │   ├─ AppointmentScheduledEvent.java
│   │   ├─ AppointmentStatusChangedEvent.java
│   │   └─ TreatmentCompletedEvent.java
│   └─ staff/DoctorCreatedEvent.java
│
├─ policy/
│   ├─ scheduling/SchedulingPolicy.java
│   └─ records/MedicalRecordPolicy.java
│
├─ exceptions/
│   ├─ DomainException.java
│   ├─ patients/InvalidEmailException.java
│   ├─ scheduling/AppointmentOverlapException.java
│   └─ records/InvalidTreatmentException.java
│
└─ specification/
    ├─ shared/Specification.java
    ├─ patients/PatientSpecification.java
    └─ scheduling/AppointmentSpecification.java
]]></correct-example>
          <incorrect-example title="Wrong structure" conditions="Creating domain classes" expected-result="Passes architecture tests" incorrectness-criteria="Files outside defined structure"><![CDATA[
domain/
├─ model/
│   ├─ Patient.java  // Wrong - should be in patients/aggregates/
│   ├─ PatientService.java  // Wrong - should be in service/patients/
│   └─ general/  // Wrong - not allowed directory
│       └─ CommonUtils.java
├─ utils/  // Wrong - not allowed directory
│   └─ DomainUtils.java
]]></incorrect-example>
        </example>
      </examples>
    </non-negotiable>

    <requirement priority="critical">
      <description>Mantener bounded contexts completamente separados sin dependencias cruzadas excepto shared.</description>
      <examples>
        <example title="Bounded Context Separation">
          <correct-example title="Proper separation" conditions="Creating classes in different contexts" expected-result="No cross-dependencies" correctness-criteria="Uses only shared for common elements"><![CDATA[
// In patients context
public class PatientAggregate {
    private final PatientId patientId;
    private final MoneyValue balance; // From shared
}

// In scheduling context  
public class AppointmentAggregate {
    private final AppointmentId appointmentId;
    private final MoneyValue cost; // From shared
}
]]></correct-example>
          <incorrect-example title="Cross-context dependency" conditions="Creating classes in different contexts" expected-result="No cross-dependencies" incorrectness-criteria="Direct dependency between contexts"><![CDATA[
// In scheduling context
public class AppointmentAggregate {
    private final AppointmentId appointmentId;
    private final PatientAggregate patient; // Wrong - direct dependency
}
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <requirement priority="critical">
      <description>Crear repositorios únicamente como interfaces puras sin implementación JPA ni anotaciones de infraestructura.</description>
      <examples>
        <example title="Pure Repository Interfaces">
          <correct-example title="Pure domain repository" conditions="Creating repository" expected-result="Clean domain interface" correctness-criteria="No infrastructure dependencies"><![CDATA[
package odoonto.domain.repository.patients;

public interface PatientRepository {
    void save(PatientAggregate patient);
    Optional<PatientAggregate> findById(PatientId id);
    List<PatientAggregate> findByEmail(EmailAddress email);
}
]]></correct-example>
          <incorrect-example title="Infrastructure-coupled repository" conditions="Creating repository" expected-result="Clean domain interface" incorrectness-criteria="JPA annotations present"><![CDATA[
package odoonto.domain.repository.patients;

@Repository
public interface PatientRepository extends JpaRepository<PatientAggregate, String> {
    // Wrong - JPA and Spring annotations in domain
}
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <requirement priority="high">
      <description>Ubicar todas las clases compartidas exclusivamente en el directorio shared para evitar duplicación.</description>
      <examples>
        <example title="Shared Classes Location">
          <correct-example title="Proper shared location" conditions="Creating shared value objects" expected-result="Single location for shared classes" correctness-criteria="Only in shared directory"><![CDATA[
// In domain/model/shared/valueobjects/
public final class MoneyValue {
    private final BigDecimal amount;
    private final String currency;
}

// In domain/model/shared/valueobjects/
public final class DurationValue {
    private final int minutes;
}
]]></correct-example>
          <incorrect-example title="Duplicated shared classes" conditions="Creating shared value objects" expected-result="Single location for shared classes" incorrectness-criteria="Same class in multiple contexts"><![CDATA[
// In domain/model/catalog/valueobjects/
public final class MoneyValue { ... }

// In domain/model/scheduling/valueobjects/  
public final class MoneyValue { ... } // Wrong - duplicated
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <requirement priority="high">
      <description>Mantener máxima simplicidad sin añadir código innecesario que no sea estrictamente requerido por los tests.</description>
      <examples>
        <example title="Domain Simplicity">
          <correct-example title="Minimal necessary code" conditions="Creating domain classes" expected-result="Passes tests with minimal code" correctness-criteria="Only essential elements"><![CDATA[
public class PatientAggregate {
    private final PatientId patientId;
    private final EmailAddress email;
    
    public PatientAggregate(PatientId patientId, EmailAddress email) {
        this.patientId = patientId;
        this.email = email;
    }
    
    public void updateEmail(EmailAddress newEmail) {
        // minimal business logic
    }
}
]]></correct-example>
          <incorrect-example title="Unnecessary complexity" conditions="Creating domain classes" expected-result="Passes tests with minimal code" incorrectness-criteria="Adds unnecessary elements"><![CDATA[
public class PatientAggregate {
    private final PatientId patientId;
    private final EmailAddress email;
    private final Logger logger; // Unnecessary
    private final ValidationService validator; // Unnecessary
    
    public void updateEmail(EmailAddress newEmail) {
        logger.info("Updating email"); // Unnecessary
        validator.validate(newEmail); // Unnecessary
        // business logic
    }
    
    public void logActivity() { } // Unnecessary method
    public void generateReport() { } // Unnecessary method
}
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <requirement priority="medium">
      <description>Cumplir estrictamente con los tests de arquitectura ArchUnit para bounded contexts y dependencias.</description>
      <examples>
        <example title="ArchUnit Compliance">
          <correct-example title="Passes ArchUnit tests" conditions="Creating domain structure" expected-result="All architecture tests pass" correctness-criteria="Follows ArchUnit rules"><![CDATA[
// Structure that passes:
// slices().matching("odoonto.domain.model.(*)..")
//         .should().notDependOnEachOther()
//         .ignoreDependency("odoonto.domain.model.shared..", "..");

domain/model/patients/aggregates/PatientAggregate.java
domain/model/scheduling/aggregates/AppointmentAggregate.java
domain/model/shared/valueobjects/MoneyValue.java
]]></correct-example>
          <incorrect-example title="Fails ArchUnit tests" conditions="Creating domain structure" expected-result="All architecture tests pass" incorrectness-criteria="Violates ArchUnit rules"><![CDATA[
// Structure that fails ArchUnit:
domain/model/patients/aggregates/PatientAggregate.java
// Contains import from scheduling context:
import odoonto.domain.model.scheduling.aggregates.AppointmentAggregate;
]]></incorrect-example>
        </example>
      </examples>
    </requirement>
  </requirements>

  <context description="Consideraciones de arquitectura">
    <context-item title="Bounded Contexts">Los bounded contexts definidos son: patients, records, scheduling, staff, catalog, shared</context-item>
    <context-item title="ArchUnit Tests">La estructura debe pasar todos los tests de src/test/java/odoonto/architecture</context-item>
    <context-item title="No Infrastructure">El dominio no debe contener anotaciones ni dependencias de infraestructura</context-item>
    <context-item title="Strict Structure">No se permite crear archivos fuera de la estructura definida</context-item>
  </context>

  <references>
    <reference as="dependency" href=".cursor/rules/rules.md" reason="Standard rule format">Base rule format and structure requirements</reference>
  </references>
</rule> 