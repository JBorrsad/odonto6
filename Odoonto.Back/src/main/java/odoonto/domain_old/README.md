# Capa de Dominio

## Descripción

La capa de dominio es el núcleo de la aplicación Odoonto y representa el corazón del sistema. Aquí se definen todas las entidades, reglas de negocio y conceptos fundamentales del dominio odontológico. Esta capa está aislada de preocupaciones externas como interfaces de usuario, bases de datos o servicios externos.

## Estructura

```
domain/
├── model/               # Entidades y agregados de dominio
│   ├── aggregates/      # Agregados (raíces de entidades relacionadas)
│   ├── entities/        # Entidades de dominio (con identidad)
│   └── valueobjects/    # Objetos de valor inmutables
├── service/             # Servicios de dominio 
├── events/              # Eventos de dominio 
├── policy/              # Políticas y reglas de negocio
├── exceptions/          # Excepciones específicas del dominio
├── repository/          # Interfaces de repositorio
└── specifications/      # Especificaciones para reglas complejas
```

## Componentes Principales

### Agregados (Aggregates)

Los agregados son grupos de entidades y objetos de valor que se tratan como una unidad cohesiva y tienen un límite transaccional claro. Los principales agregados son:

- **Patient**: Representa un paciente con toda su información asociada
- **Doctor**: Profesional odontológico con sus especialidades
- **Odontogram**: Representación dental completa de un paciente
- **MedicalRecord**: Historial médico-dental de un paciente
- **Appointment**: Cita programada entre paciente y doctor

Los agregados actúan como raíz para un conjunto de entidades relacionadas, garantizando la consistencia interna.

### Entidades (Entities)

Las entidades son objetos del dominio con una identidad única y un ciclo de vida. Ejemplos clave:

- **Tooth**: Cada diente dentro del odontograma
- **Lesion**: Problema o patología en un diente
- **Treatment**: Tratamiento aplicado a un diente
- **MedicalEntry**: Entrada individual en el historial médico

### Objetos de Valor (Value Objects)

Los objetos de valor son inmutables y se caracterizan por sus atributos, no por una identidad. Incluyen:

- **EmailAddress**: Dirección de correo electrónico validada
- **PhoneNumber**: Número de teléfono con formato específico
- **PersonName**: Nombre completo estructurado
- **Address**: Dirección postal
- **ToothFace**: Representación de caras dentales (VESTIBULAR, LINGUAL, etc.)
- **LesionType**: Tipos de lesiones dentales
- **TreatmentType**: Tipos de tratamientos dentales

### Servicios de Dominio (Domain Services)

Cuando una operación no pertenece naturalmente a una entidad o agregado específico, se implementa como un servicio de dominio:

- **DentalDiagnosisService**: Lógica para diagnósticos dentales basados en los datos del odontograma
- **TreatmentPlanService**: Generación de planes de tratamiento dental

### Eventos de Dominio (Domain Events)

Los eventos representan hechos significativos que han ocurrido en el dominio:

- **PatientRegisteredEvent**: Se ha registrado un nuevo paciente
- **AppointmentScheduledEvent**: Se ha programado una nueva cita
- **TreatmentCompletedEvent**: Se ha completado un tratamiento

### Políticas (Policies)

Las políticas encapsulan reglas de negocio complejas:

- **SchedulingPolicy**: Reglas para programar citas (disponibilidad, duración, etc.)
- **MedicalRecordPolicy**: Reglas para la gestión de historiales médicos

### Excepciones (Exceptions)

Las excepciones específicas del dominio aseguran que los errores se manejen adecuadamente:

- **InvalidPersonDataException**: Datos personales inválidos
- **DuplicateLesionException**: Intento de registrar una lesión duplicada
- **InvalidToothNumberException**: Número de diente fuera de rango

## Diagrama UML 

El siguiente diagrama muestra las relaciones entre las principales entidades del dominio:

![Diagrama de Dominio](../documentation/plantuml/domain_model.png)

## Ejemplos de Código

### Definición de un Agregado (Aggregate Root)

```java
@Getter
public class Patient {
    private final PatientId id;
    private PersonName name;
    private EmailAddress email;
    private PhoneNumber phoneNumber;
    private Address address;
    private LocalDate birthDate;
    private Sexo sexo;
    
    // Constructor, métodos de dominio y validaciones
    
    public void updateContactInfo(EmailAddress newEmail, PhoneNumber newPhone) {
        this.email = newEmail;
        this.phoneNumber = newPhone;
    }
    
    // Más comportamiento de dominio...
}
```

### Definición de un Objeto de Valor (Value Object)

```java
@Value
public class EmailAddress {
    String value;
    
    public EmailAddress(String email) {
        if (!isValidEmail(email)) {
            throw new InvalidEmailException(email);
        }
        this.value = email;
    }
    
    private boolean isValidEmail(String email) {
        // Validación de formato de email
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}
```

## Principios de Diseño

La capa de dominio sigue varios principios clave:

1. **Encapsulación**: Los detalles de implementación están ocultos
2. **Inmutabilidad**: Los objetos de valor son inmutables para evitar efectos secundarios
3. **Invariantes**: Se mantienen reglas de negocio y restricciones en todo momento
4. **Tell, Don't Ask**: Se favorece el comportamiento sobre la exposición de datos

## Relación con Otras Capas

- La capa de dominio no depende de ninguna otra capa
- Define interfaces de repositorio que son implementadas por la capa de infraestructura
- Expone servicios y agregados que son utilizados por la capa de aplicación 