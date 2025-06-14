# Guía de jMolecules - Documentación DDD en Código

## ¿Qué es jMolecules?

jMolecules es una biblioteca que permite documentar conceptos de **Domain-Driven Design (DDD)** directamente en el código usando anotaciones. Esto hace que la arquitectura sea **autodocumentada** y **verificable automáticamente**.

## Dependencias Agregadas

```xml
<!-- jMolecules Core - Anotaciones DDD -->
<dependency>
    <groupId>org.jmolecules</groupId>
    <artifactId>jmolecules-ddd</artifactId>
    <version>1.8.0</version>
</dependency>

<!-- jMolecules Events - Domain Events -->
<dependency>
    <groupId>org.jmolecules</groupId>
    <artifactId>jmolecules-events</artifactId>
    <version>1.8.0</version>
</dependency>

<!-- jMolecules ArchUnit Integration -->
<dependency>
    <groupId>org.jmolecules.integrations</groupId>
    <artifactId>jmolecules-archunit</artifactId>
    <version>0.26.0</version>
    <scope>test</scope>
</dependency>

<!-- jMolecules Spring Integration -->
<dependency>
    <groupId>org.jmolecules.integrations</groupId>
    <artifactId>jmolecules-spring</artifactId>
    <version>0.26.0</version>
</dependency>
```

## 🎯 Anotaciones jMolecules vs Convenciones

### **ANTES (Solo convenciones de nomenclatura):**
```java
// Solo por nombre - no hay garantía semántica
public class PatientEntity { }
public class PatientAggregate { }
public class PatientNameValue { }
```

### **AHORA (jMolecules - Semántica explícita):**
```java
@Entity
public class PatientEntity { }

@AggregateRoot
public class PatientAggregate { }

@ValueObject
public class PatientNameValue { }
```

## 📋 Uso de Anotaciones jMolecules por Capa

### 🎯 **Domain Model (`domain.model`)**

#### **Entities:**
```java
package odoonto.domain.model;

import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
public class PatientEntity {
    @Identity
    private final PatientId id;
    private final PatientName name;
    
    // Constructor, métodos de negocio...
}
```

#### **Aggregate Roots:**
```java
package odoonto.domain.model;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.types.AggregateRoot;

@AggregateRoot
public class PatientAggregate implements AggregateRoot<PatientAggregate, PatientId> {
    @Identity
    private final PatientId id;
    
    public void scheduleAppointment(AppointmentId appointmentId) {
        // Lógica de negocio
        // Publicar domain event
    }
}
```

#### **Value Objects:**
```java
package odoonto.domain.model.valueobject;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record PatientName(String firstName, String lastName) {
    public PatientName {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
    }
    
    public String fullName() {
        return firstName + " " + lastName;
    }
}
```

#### **Identities:**
```java
package odoonto.domain.model.valueobject;

import org.jmolecules.ddd.annotation.Identity;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
@Identity
public record PatientId(String value) {
    public PatientId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
    }
}
```

### 🏪 **Domain Repository (`domain.repository`)**

```java
package odoonto.domain.repository;

import org.jmolecules.ddd.annotation.Repository;

@Repository
public interface PatientRepository {
    PatientAggregate findById(PatientId id);
    void save(PatientAggregate patient);
    List<PatientAggregate> findByName(PatientName name);
}
```

### ⚙️ **Domain Services (`domain.service`)**

```java
package odoonto.domain.service;

import org.jmolecules.ddd.annotation.Service;

@Service
public class PatientDomainService {
    
    public boolean canScheduleAppointment(PatientAggregate patient, AppointmentId appointmentId) {
        // Lógica compleja que involucra múltiples aggregates
        return true;
    }
}
```

### 🎭 **Domain Events (`domain.event`)**

```java
package odoonto.domain.event;

import org.jmolecules.event.annotation.DomainEvent;
import org.jmolecules.event.types.DomainEvent;

@DomainEvent
public record PatientRegisteredEvent(
    PatientId patientId,
    PatientName name,
    LocalDateTime occurredAt
) implements DomainEvent {
    
    public PatientRegisteredEvent(PatientId patientId, PatientName name) {
        this(patientId, name, LocalDateTime.now());
    }
}
```

### 🏭 **Factories (`domain.factory`)**

```java
package odoonto.domain.factory;

import org.jmolecules.ddd.annotation.Factory;

@Factory
public class PatientFactory {
    
    public PatientAggregate createPatient(PatientName name, PatientEmail email) {
        PatientId id = PatientId.generate();
        return new PatientAggregate(id, name, email);
    }
}
```

### 🎪 **Domain Exceptions (`domain.exception`)**

```java
package odoonto.domain.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(PatientId patientId) {
        super("Patient not found: " + patientId.value());
    }
}
```

## 🚫 **LO QUE NO DEBES HACER:**

### ❌ **NO usar anotaciones jMolecules fuera del dominio:**
```java
// ❌ INCORRECTO - Application Service
@Entity  // ¡NO! Esto es solo para domain
public class CreatePatientUseCase { }

// ❌ INCORRECTO - Infrastructure
@AggregateRoot  // ¡NO! Esto es solo para domain
public class PatientJpaEntity { }
```

### ❌ **NO mezclar anotaciones de Spring/JPA en domain:**
```java
// ❌ INCORRECTO - Domain Entity
@Entity  // jMolecules ✅
@javax.persistence.Entity  // JPA ❌ ¡NO en domain!
public class PatientEntity { }
```

## ✅ **Integración con Spring:**

La integración con Spring permite que las anotaciones jMolecules se mapeen automáticamente:

```java
// Domain Service se vuelve Spring @Service automáticamente
@org.jmolecules.ddd.annotation.Service
public class PatientDomainService {
    // Se registra automáticamente como Spring Bean
}

// Repository interface se vuelve Spring @Repository automáticamente
@org.jmolecules.ddd.annotation.Repository  
public interface PatientRepository {
    // Se detecta automáticamente por Spring Data
}
```

## 🔍 **Verificación Automática con ArchUnit:**

Las reglas en `JMoleculesTest.java` verifican automáticamente:

✅ **Entities** deben estar en `domain.model` y tener `@Entity`
✅ **Aggregates** deben estar en `domain.model` y tener `@AggregateRoot`
✅ **Value Objects** deben estar en `domain.model.valueobject` y tener `@ValueObject`
✅ **Repositories** deben estar en `domain.repository` y tener `@Repository`
✅ **Domain Services** deben estar en `domain.service` y tener `@Service`
✅ **Domain Events** deben estar en `domain.event` y tener `@DomainEvent`
✅ **Identities** deben estar en `domain.model` y tener `@Identity`
✅ **Application/Infrastructure** NO deben usar anotaciones de domain

## 🎯 **Beneficios de jMolecules:**

1. **📚 Autodocumentación**: El código se documenta a sí mismo
2. **✅ Verificación automática**: ArchUnit valida la arquitectura
3. **🔧 Integración Spring**: Mapeo automático a anotaciones Spring
4. **🎨 Claridad conceptual**: Conceptos DDD explícitos en código
5. **👥 Onboarding**: Nuevos desarrolladores entienden la arquitectura inmediatamente
6. **🚀 Refactoring seguro**: Cambios arquitectónicos son detectados automáticamente

## 🚀 **Ejecutar Verificaciones:**

```bash
# Verificar todas las reglas DDD + jMolecules
./run-architecture-tests.bat

# Solo jMolecules
mvn test -Dtest="odoonto.architecture.JMoleculesTest"
```

## 📊 **Estado de Implementación:**

Para verificar qué clases necesitan anotaciones jMolecules:

```bash
mvn test -Dtest="odoonto.architecture.*"
```

Las pruebas te dirán exactamente qué clases necesitan qué anotaciones para cumplir con DDD.

**¡Con jMolecules tu código DDD es autodocumentado y autoverificado!** 🎉 