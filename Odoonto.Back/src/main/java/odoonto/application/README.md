# Capa de Aplicación

## Descripción

La capa de aplicación actúa como coordinadora entre el mundo exterior y el dominio del sistema Odoonto. Se encarga de orquestar la ejecución de los casos de uso del sistema, traduciendo las solicitudes externas en operaciones de dominio y proporcionando resultados en formatos adecuados para los consumidores.

## Estructura

```
application/
├── dto/                 # Objetos de transferencia de datos
│   ├── request/         # DTOs para solicitudes entrantes
│   └── response/        # DTOs para respuestas salientes
├── port/                # Interfaces de servicios (puertos)
│   ├── in/              # Puertos de entrada (casos de uso)
│   └── out/             # Puertos de salida (interfaces de repositorio)
├── mapper/              # Conversiones entre objetos de dominio y DTOs
├── service/             # Implementaciones de casos de uso
└── exceptions/          # Excepciones específicas de la aplicación
```

## Componentes Principales

### Puertos de Entrada (Casos de Uso)

Los puertos de entrada definen las operaciones que la aplicación puede realizar. Siguen el patrón "puerto-adaptador" donde el puerto es la interfaz y la implementación es el adaptador. Están organizados por contexto:

#### Patient
- **PatientCreateUseCase**: Crear nuevo paciente
- **PatientUpdateUseCase**: Actualizar datos de paciente existente
- **PatientDeleteUseCase**: Eliminar paciente
- **PatientQueryUseCase**: Buscar y consultar pacientes
- **PatientOdontogramUseCase**: Gestionar odontograma vinculado a paciente

#### Odontogram
- **OdontogramQueryUseCase**: Consultar odontograma
- **LesionAddUseCase**: Añadir lesión a un diente
- **TreatmentAddUseCase**: Registrar tratamiento en un diente

#### MedicalRecord
- **MedicalRecordCreateUseCase**: Crear historial médico
- **MedicalEntryAddUseCase**: Añadir entrada al historial

#### Doctor
- **DoctorCreateUseCase**: Registrar nuevo doctor
- **DoctorQueryUseCase**: Buscar y consultar doctores

#### Appointment
- **AppointmentCreateUseCase**: Programar nueva cita
- **AppointmentCancelUseCase**: Cancelar cita existente
- **AppointmentQueryUseCase**: Buscar y consultar citas

### DTOs (Data Transfer Objects)

Los DTOs facilitan la comunicación entre capas, desacoplando la representación externa de la estructura interna:

#### DTOs de Solicitud (Request)
- **PatientCreateDTO**: Datos para crear paciente
- **MedicalRecordCreateDTO**: Datos para crear historial médico
- **AppointmentCreateDTO**: Datos para programar cita

#### DTOs de Respuesta (Response)
- **PatientDTO**: Representación de paciente para consumo externo
- **OdontogramDTO**: Datos del odontograma para UI
- **AppointmentDTO**: Información de cita

### Mapeadores (Mappers)

Los mapeadores transforman entre objetos del dominio y DTOs:

- **PatientMapper**: Conversión entre Patient (dominio) y PatientDTO (API)
- **OdontogramMapper**: Conversión entre Odontogram (dominio) y OdontogramDTO (API)
- **AppointmentMapper**: Conversión entre Appointment (dominio) y AppointmentDTO (API)

### Servicios de Aplicación

Implementan la lógica de los casos de uso, orquestando la interacción con el dominio:

#### Patient
- **PatientCreateService**: Implementa la creación de pacientes
- **PatientUpdateService**: Actualiza información de pacientes existentes

#### Odontogram
- **LesionAddService**: Añade lesiones al odontograma
- **TreatmentAddService**: Registra tratamientos en dientes

#### Appointment
- **AppointmentCreateService**: Crea nuevas citas verificando disponibilidad
- **AppointmentCancelService**: Gestiona cancelación de citas

### Excepciones de Aplicación

Excepciones específicas para errores a nivel de aplicación:

- **PatientNotFoundException**: Paciente no encontrado
- **DoctorNotFoundException**: Doctor no encontrado
- **AppointmentConflictException**: Conflicto de horarios en citas

## Diagrama UML

El siguiente diagrama ilustra la estructura de la capa de aplicación y sus interacciones:

![Diagrama de Aplicación](../documentation/plantuml/application_layer.png)

## Ejemplos de Código

### Puerto de Entrada (Caso de Uso)

```java
public interface PatientCreateUseCase {
    /**
     * Crea un nuevo paciente en el sistema
     * @param patientCreateDTO Datos del paciente a crear
     * @return DTO con la información del paciente creado
     */
    Mono<PatientDTO> createPatient(PatientCreateDTO patientCreateDTO);
}
```

### Implementación de Servicio

```java
@Service
@RequiredArgsConstructor
public class PatientCreateService implements PatientCreateUseCase {
    
    private final ReactivePatientRepository patientRepository;
    private final PatientMapper patientMapper;
    
    @Override
    public Mono<PatientDTO> createPatient(PatientCreateDTO patientCreateDTO) {
        // 1. Mapear DTO a entidad de dominio
        Patient patient = patientMapper.toDomain(patientCreateDTO);
        
        // 2. Persistir en el repositorio
        return patientRepository.save(patient)
            // 3. Mapear resultado a DTO de respuesta
            .map(patientMapper::toDTO);
    }
}
```

### Data Transfer Object (DTO)

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;
    
    private String phoneNumber;
    private String address;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate birthDate;
    
    private String sexo;
}
```

## Principios de Diseño

La capa de aplicación sigue varios principios clave:

1. **Separación de Responsabilidades**: Cada clase tiene una responsabilidad única
2. **Inversión de Dependencias**: Depende de abstracciones (puertos) no de implementaciones concretas
3. **Casos de Uso Aislados**: Cada caso de uso está encapsulado en su propia interfaz
4. **Programación Reactiva**: Utiliza tipos reactivos (Mono/Flux) para operaciones asíncronas

## Relación con Otras Capas

- **Hacia el Dominio**: Utiliza entidades y servicios de la capa de dominio
- **Hacia la Infraestructura**: Define puertos que son implementados por adaptadores en la capa de infraestructura
- **Hacia la Presentación**: Expone casos de uso que son consumidos por controladores en la capa de presentación 