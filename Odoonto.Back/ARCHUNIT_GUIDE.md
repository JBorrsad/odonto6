# Guía de ArchUnit - Reglas Arquitectónicas Específicas

## ¿Qué es ArchUnit?

ArchUnit es una biblioteca de testing para Java que permite definir y verificar reglas arquitectónicas mediante pruebas automatizadas. Esto ayuda a mantener la arquitectura limpia y consistente a lo largo del tiempo.

## Configuración Implementada

### Dependencia Agregada
```xml
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>1.2.1</version>
    <scope>test</scope>
</dependency>
```

## Clases de Prueba Creadas Según Especificaciones

### 1. UbicationTest.java
**Propósito**: Verificar estructura de carpetas y ubicación correcta

**Reglas implementadas**:
- Controllers solo en `controller.rest`
- UseCases solo en `application.service`
- DTOs solo en `application.dto`
- Repositorios (interfaces) en `domain.repository`
- Implementaciones de repositorios en `infrastructure.persistence`
- Entidades JPA (@Entity) en `infrastructure.persistence`
- Entidades de dominio en `domain.model`
- ValueObjects en `domain.model.valueobject`
- Adaptadores externos en `infrastructure.external`
- Configuraciones en `configuration`

### 2. AccesTest.java
**Propósito**: Verificar normas de acceso entre capas

**Reglas implementadas**:
- Controllers solo pueden acceder a `application`
- `application.service` solo puede acceder a `domain` y `application.dto`
- `domain` NO puede acceder a ninguna otra capa
- `infrastructure` puede acceder a `domain.repository` pero nunca a controller ni application
- DTOs no pueden depender de clases fuera de application
- Adaptadores no deben acceder directamente a `domain.model`

### 3. DependencyTest.java
**Propósito**: Verificar normas de dependencias y prevenir ciclos

**Reglas implementadas**:
- No ciclos entre paquetes
- Solo `infrastructure.persistence` puede usar @Entity o JpaRepository
- Solo `controller.rest` puede usar @RestController
- Solo `application.service` puede usar @Service
- Solo `infrastructure.persistence` puede usar @Repository
- Domain no puede importar Spring Framework
- Domain no puede tener anotaciones de Spring

### 4. NameTest.java
**Propósito**: Verificar convenciones de nomenclatura

**Reglas implementadas**:
- Controllers terminan en "Controller"
- Use cases terminan en "UseCase"
- Entidades de dominio terminan en "Entity" o "Aggregate"
- Interfaces de repositorio empiezan por "I" o terminan en "Repository"
- DTOs terminan en "Dto"
- Value Objects terminan en "Value" o "VO"
- Mappers terminan en "Mapper"
- Configuraciones terminan en "Config" o "Configuration"

### 5. AcopplementTest.java
**Propósito**: Verificar normas contra acoplamiento

**Reglas implementadas**:
- Domain no puede importar Spring
- Domain no puede tener anotaciones de Spring
- Domain entities solo lanzan excepciones del dominio
- UseCases no dependen de clases HTTP
- Solo Controllers pueden devolver clases HTTP
- No inyección por campo (@Autowired)
- Domain no accede a infrastructure directamente
- Application no accede a infrastructure directamente

### 6. GoodPracticeTest.java
**Propósito**: Verificar buenas prácticas generales

**Reglas implementadas**:
- Value Objects son inmutables
- No excepciones genéricas
- No java.util.logging
- Use Cases son servicios (@Service)
- Entidades no dependen de persistencia
- Validación en capas correctas
- Controllers no contienen lógica de negocio

### 7. SolidTest.java
**Propósito**: Verificar principios SOLID

**Reglas implementadas**:
- SRP: Controllers solo coordinan
- OCP: Use cases no contienen lógica de persistencia
- LSP: Entidades no acceden a bases de datos
- ISP: Use cases dependen de interfaces
- DIP: Infrastructure implementa abstracciones de domain
- Domain no depende de implementaciones concretas
- Application depende solo de interfaces

### 8. CleanCodeTest.java
**Propósito**: Verificar reglas de Clean Code

**Reglas implementadas**:
- Nombres expresivos (no Temp, Utils, Helper)
- Sin abreviaciones (no Ctrl, Svc, Res, Usr)
- No excepciones genéricas
- No java.util.logging
- Controllers sin lógica de negocio
- Validación en capa correcta
- No System.out.println
- Inyección por constructor preferida
- Paquetes con nombres significativos

### 9. JMoleculesTest.java 🆕
**Propósito**: Verificar uso correcto de anotaciones jMolecules DDD

**Reglas implementadas**:
- **@Entity** obligatorio en entities del domain
- **@AggregateRoot** obligatorio en aggregates del domain
- **@ValueObject** obligatorio en value objects
- **@Repository** obligatorio en interfaces de repositorio
- **@Service** obligatorio en domain services
- **@DomainEvent** obligatorio en eventos de dominio
- **@Factory** obligatorio en factories
- **@Identity** obligatorio en identificadores
- **NO uso** de anotaciones jMolecules fuera del domain
- **Implementación** de interfaces jMolecules cuando corresponde

## Cómo Ejecutar las Pruebas

### Opción 1: Script Automatizado
```bash
./run-architecture-tests.bat
```

### Opción 2: Maven Directo
```bash
# Todas las pruebas de arquitectura
mvn test -Dtest="odoonto.architecture.*"

# Solo pruebas específicas
mvn test -Dtest="odoonto.architecture.UbicationTest"
mvn test -Dtest="odoonto.architecture.AccesTest"
mvn test -Dtest="odoonto.architecture.DependencyTest"
mvn test -Dtest="odoonto.architecture.NameTest"
mvn test -Dtest="odoonto.architecture.AcopplementTest"
mvn test -Dtest="odoonto.architecture.GoodPracticeTest"
mvn test -Dtest="odoonto.architecture.SolidTest"
mvn test -Dtest="odoonto.architecture.CleanCodeTest"
mvn test -Dtest="odoonto.architecture.JMoleculesTest"
```

### Opción 3: Desde el IDE
Ejecuta las clases de test como cualquier prueba JUnit 5.

## Interpretación de Resultados

### ✅ Prueba Exitosa
```
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```
Todas las reglas arquitectónicas se cumplen.

### ❌ Prueba Fallida
```
Architecture Violation [Priority: MEDIUM] - Rule 'classes that reside in a package '..domain..' should not depend on classes that reside in any package ['..infrastructure..', '..presentation..', '..application..']' was violated (2 times):
Class <odoonto.domain.model.Patient> depends on class <odoonto.infrastructure.repository.PatientRepository> in (Patient.java:0)
```

**Interpretación**: La clase `Patient` en el dominio está dependiendo de una clase de infraestructura, violando la regla de independencia del dominio.

## Beneficios de ArchUnit

1. **Prevención automática**: Detecta violaciones arquitectónicas antes de que lleguen a producción
2. **Documentación viva**: Las reglas sirven como documentación de la arquitectura
3. **Refactoring seguro**: Te avisa si cambios rompen la arquitectura
4. **Onboarding**: Ayuda a nuevos desarrolladores a entender las reglas
5. **Integración CI/CD**: Se ejecuta automáticamente en el pipeline

## Personalización

Para agregar nuevas reglas, edita las clases de test y agrega métodos con la anotación `@ArchTest`:

```java
@ArchTest
static final ArchRule my_custom_rule = 
        classes()
            .that().resideInAPackage("..domain..")
            .should().notDependOnClassesThat()
            .resideInAPackage("..external..");
```

## Resolución de Problemas Comunes

### Falsos Positivos
Si una regla está fallando por un caso legítimo, puedes:
1. Modificar la regla para ser más específica
2. Agregar excepciones usando `.ignoreDependency()`
3. Comentar temporalmente la regla y crear un ticket para revisarla

### Rendimiento
Si las pruebas son lentas:
1. Usa `ImportOption.DoNotIncludeTests.class` para excluir clases de test
2. Limita el escaneo a paquetes específicos
3. Ejecuta pruebas de arquitectura en un perfil separado

## Integración con CI/CD

Agrega esto a tu pipeline para ejecutar automáticamente:

```yaml
- name: Run Architecture Tests
  run: mvn test -Dtest="odoonto.architecture.*"
```

La integración fallará si alguna regla arquitectónica es violada, previniendo deployments con problemas arquitecturales.

## Resumen de Reglas Implementadas

### 📁 Normas de estructura de carpetas (UbicationTest)
✅ Controllers en `controller.rest`
✅ UseCases en `application.service`  
✅ DTOs en `application.dto`
✅ Repositorios (interfaces) en `domain.repository`
✅ Implementaciones en `infrastructure.persistence`
✅ Entidades JPA en `infrastructure.persistence`
✅ Entidades dominio en `domain.model`
✅ ValueObjects en `domain.model.valueobject`
✅ Adaptadores en `infrastructure.external`
✅ Configuraciones en `configuration`

### 🔐 Normas de acceso entre capas (AccesTest)
✅ Controllers → solo application
✅ application.service → solo domain + application.dto
✅ domain → NO accede a ninguna otra capa
✅ infrastructure → domain.repository (NO controller/application)
✅ DTOs → NO dependen de nada fuera de application
✅ Adaptadores → NO acceso directo a domain.model

### 🧩 Normas de dependencias (DependencyTest)
✅ Sin ciclos entre paquetes
✅ @Entity solo en infrastructure.persistence
✅ @RestController solo en controller.rest
✅ @Service solo en application.service
✅ @Repository solo en infrastructure.persistence

### 📛 Normas de nomenclatura (NameTest)
✅ Controllers terminan en "Controller"
✅ UseCases terminan en "UseCase"
✅ Entidades terminan en "Entity/Aggregate"
✅ Repositorios empiezan por "I" o terminan en "Repository"
✅ DTOs terminan en "Dto"
✅ ValueObjects terminan en "Value/VO"
✅ Mappers terminan en "Mapper"

### 🔌 Normas contra acoplamiento + DDD Críticas (AcopplementTest)
✅ Domain sin dependencias Spring
✅ Domain sin anotaciones Spring
✅ UseCases sin clases HTTP
✅ Solo Controllers devuelven ResponseEntity
✅ Sin inyección por campo (@Autowired)
✅ **UseCases como boundaries transaccionales** (@Transactional)
✅ **Value Objects inmutables** (campos final)
✅ **Entities con identidad** (verificación de estructura)
✅ **Repositories solo Aggregates** (no entidades individuales)
✅ **Command/Query Separation** (transaccional vs no-transaccional)
✅ **Aggregates independientes** (no referencia directa entre aggregates)
✅ **Domain Services puros** (sin dependencias de infrastructure)

### ⚡ Principios SOLID (SolidTest)
✅ SRP: Controllers solo coordinan
✅ OCP: UseCases sin lógica persistencia
✅ LSP: Entidades sin acceso BD
✅ ISP: UseCases dependen de interfaces
✅ DIP: Infrastructure implementa abstracciones

### 🧼 Clean Code (CleanCodeTest)
✅ Nombres expresivos (no Temp/Utils/Helper)
✅ Sin abreviaciones (no Ctrl/Svc/Res/Usr)
✅ Sin excepciones genéricas
✅ Sin System.out.println
✅ Inyección por constructor

## Estado Actual del Proyecto

Para verificar el cumplimiento actual de estas reglas en tu proyecto, ejecuta:

```bash
./run-architecture-tests.bat
```

Esto te mostrará qué reglas ya se cumplen y cuáles necesitan ajustes en el código actual.

## 🎯 Reglas DDD Críticas Implementadas

### 🔥 **NUEVAS REGLAS CRÍTICAS AGREGADAS**:

#### 1. **Agregados como Transaccional Boundaries**
```java
@Transactional  // ✅ OBLIGATORIO en UseCases
public class CreatePatientUseCase {
    // Un UseCase = Una transacción = Un Aggregate modificado
}
```

#### 2. **Value Objects Completamente Inmutables**
```java
// ✅ TODOS los campos deben ser final
public class PatientName {
    private final String firstName;  // ✅ final
    private final String lastName;   // ✅ final
    // + equals() y hashCode() override
}
```

#### 3. **Entities con Identidad Clara**
```java
// ✅ Verificación automática de estructura correcta
public class PatientEntity {
    private final PatientId id;  // ✅ Campo de identidad requerido
}
```

#### 4. **Repositories Solo Trabajan con Aggregates**
```java
// ✅ Métodos solo retornan Aggregate roots
public interface PatientRepository {
    PatientAggregate findById(PatientId id);      // ✅ Correcto
    // NO: PatientEntity findEntity(String id);   // ❌ Incorrecto
}
```

#### 5. **Command/Query Separation Estricto**
```java
// Commands (modifican estado)
@Transactional  // ✅ OBLIGATORIO
public class CreatePatientCommandHandler { }

// Queries (solo leen)
// SIN @Transactional  // ✅ OBLIGATORIO
public class GetPatientQueryHandler { }
```

#### 6. **Aggregates Completamente Independientes**
```java
public class PatientAggregate {
    // ✅ Solo referencia ID de otros aggregates
    private AppointmentId appointmentId;  // ✅ Solo ID
    
    // ❌ NO referencias directas
    // private AppointmentAggregate appointment;  // ❌ Prohibido
}
```

#### 7. **Domain Services Puros**
```java
// ✅ Solo coordina aggregates del dominio
public class PatientDomainService {
    // SIN dependencias de infrastructure
    // Solo lógica de dominio compleja que spans múltiples aggregates
}
```

### 📊 **IMPACTO EN TU CÓDIGO**:

Con estas reglas, tu código **automáticamente** seguirá:
- ✅ **Transactional Consistency** por aggregate
- ✅ **Inmutabilidad** en Value Objects
- ✅ **Boundaries claros** entre aggregates
- ✅ **CQRS** pattern enforcement
- ✅ **Domain purity** garantizada

### 🔍 **VERIFICACIÓN AUTOMÁTICA**:

Cada vez que ejecutes:
```bash
./run-architecture-tests.bat
```

El sistema verificará que **TODAS** estas reglas DDD críticas se cumplan en tu código. Si alguna se viola, el build fallará con un mensaje claro explicando qué corregir.

**¡Ahora tienes una implementación DDD enterprise-grade garantizada por ArchUnit + jMolecules!** 🎉

## 🆕 jMolecules Integration

### 📚 **Documentación DDD Autodocumentada**

Con jMolecules, tu código DDD ahora se autodocumenta:

```java
@AggregateRoot  // ✅ Explícitamente un Aggregate Root
public class PatientAggregate {
    @Identity   // ✅ Explícitamente la identidad
    private final PatientId id;
}

@ValueObject    // ✅ Explícitamente un Value Object
public record PatientName(String firstName, String lastName) { }

@Repository     // ✅ Explícitamente un Repository del Domain
public interface PatientRepository { }
```

### 🔍 **Verificación Automática Mejorada**

Las nuevas reglas de `JMoleculesTest` garantizan:
- ✅ **Conceptos DDD explícitos** con anotaciones jMolecules
- ✅ **Separación clara** entre domain y otras capas
- ✅ **Integración Spring automática** via jMolecules
- ✅ **Documentación viva** en el código

### 📖 **Guía Completa de jMolecules**

Para ejemplos detallados y mejores prácticas, consulta:
**[JMOLECULES_GUIDE.md](./JMOLECULES_GUIDE.md)**

**¡Tu DDD ahora es completamente autodocumentado y autoverificado!** 🚀 