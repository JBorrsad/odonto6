# Capa de Infraestructura

## Descripción

La capa de infraestructura proporciona implementaciones concretas para las abstracciones definidas en las capas de dominio y aplicación. Esta capa se encarga de gestionar la interacción con tecnologías y sistemas externos como bases de datos, servicios en la nube, seguridad y herramientas de utilidad.

En Odoonto, la capa de infraestructura implementa la persistencia de datos utilizando Google Firestore, configuración de Spring Boot, aspectos de seguridad y herramientas de desarrollo como generadores de diagramas.

## Estructura

```
infrastructure/
├── persistence/         # Implementaciones de repositorios
│   ├── reactive/        # Adaptadores reactivos para repositorios
│   └── entity/          # Entidades de persistencia
├── config/              # Configuraciones (Spring, Firestore, etc.)
├── security/            # Implementaciones de seguridad
├── tools/               # Herramientas y utilidades
├── messaging/           # Implementación de mensajería
└── testing/             # Herramientas para pruebas
```

## Componentes Principales

### Persistencia

La persistencia gestiona el almacenamiento y recuperación de datos en la base de datos:

#### Adaptadores Reactivos

Los adaptadores implementan las interfaces de repositorio definidas en la capa de aplicación:

- **ReactivePatientRepositoryAdapter**: Implementación para persistencia de pacientes
- **ReactiveOdontogramRepositoryAdapter**: Implementación para persistencia de odontogramas
- **ReactiveMedicalRecordRepositoryAdapter**: Implementación para persistencia de historiales médicos
- **ReactiveDoctorRepositoryAdapter**: Implementación para persistencia de doctores
- **ReactiveAppointmentRepositoryAdapter**: Implementación para persistencia de citas

Estos adaptadores utilizan la API reactiva de Firestore para operaciones asíncronas.

#### Entidades de Persistencia

Las entidades de persistencia mapean los objetos del dominio a estructuras adecuadas para Firestore:

- **FirestorePatientEntity**: Representación de paciente para almacenamiento
- **FirestoreOdontogramEntity**: Representación de odontograma para almacenamiento
- **FirestoreMedicalRecordEntity**: Representación de historial médico para almacenamiento

### Configuración

La configuración establece los parámetros y beans necesarios para el funcionamiento del sistema:

- **FirestoreConfig**: Configuración de conexión con Firestore
- **DiagramGeneratorConfig**: Configuración para generadores de diagramas
- **StartupMenuConfig**: Configuración del menú interactivo de inicio
- **ServiceInitializer**: Inicialización de servicios y recursos
- **WebConfig**: Configuración de la capa web

### Seguridad

Componentes relacionados con la autenticación y autorización:

- **SecurityConfig**: Configuración de Spring Security
- **FirebaseAuthAdapter**: Integración con Firebase Authentication

### Herramientas

Utilidades para facilitar el desarrollo y mantenimiento:

- **DDDDiagramGenerator**: Genera diagramas PlantUML para visualizar la arquitectura
- **GenerateDDDDiagrams**: Punto de entrada para la generación de diagramas

### Testing

Herramientas y utilidades para pruebas:

- **DataSeeder**: Carga datos iniciales para desarrollo y pruebas

## Diagrama UML

El siguiente diagrama muestra la estructura de la capa de infraestructura:

![Diagrama de Infraestructura](../documentation/plantuml/infrastructure_layer.png)

## Ejemplos de Código

### Adaptador de Repositorio Reactivo

```java
@Component
@RequiredArgsConstructor
public class ReactivePatientRepositoryAdapter implements ReactivePatientRepository {

    private final FirestoreTemplate firestoreTemplate;
    
    @Override
    public Mono<Patient> save(Patient patient) {
        // Convertir de entidad de dominio a entidad de Firestore
        FirestorePatientEntity entity = mapToEntity(patient);
        
        // Guardar en Firestore utilizando la plantilla reactiva
        return firestoreTemplate.save(entity)
            // Convertir resultado de vuelta a entidad de dominio
            .map(this::mapToDomain);
    }
    
    @Override
    public Mono<Patient> findById(PatientId id) {
        return firestoreTemplate.findById(id.getValue(), FirestorePatientEntity.class)
            .map(this::mapToDomain);
    }
    
    @Override
    public Flux<Patient> findAll() {
        return firestoreTemplate.findAll(FirestorePatientEntity.class)
            .map(this::mapToDomain);
    }
    
    // Métodos de mapeo entre dominio y persistencia
    private FirestorePatientEntity mapToEntity(Patient patient) {
        // Implementación del mapeo
        // ...
    }
    
    private Patient mapToDomain(FirestorePatientEntity entity) {
        // Implementación del mapeo inverso
        // ...
    }
}
```

### Configuración de Firestore

```java
@Configuration
@EnableReactiveFeignClients
public class FirestoreConfig {

    @Value("${gcp.project.id}")
    private String projectId;
    
    @Bean
    public Firestore firestore() throws IOException {
        FirestoreOptions options = FirestoreOptions.getDefaultInstance().toBuilder()
            .setProjectId(projectId)
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .build();
        
        return options.getService();
    }
    
    @Bean
    public FirestoreTemplate firestoreTemplate(Firestore firestore) {
        return new FirestoreTemplate(firestore);
    }
    
    @Bean
    public FirestoreReactiveOperations firestoreReactiveOperations(FirestoreTemplate firestoreTemplate) {
        return new FirestoreReactiveOperations(firestoreTemplate);
    }
}
```

### Generación de Diagramas DDD

```java
@Component
public class DDDDiagramGenerator {

    private final List<Class<?>> domainClasses;
    private final List<Class<?>> applicationClasses;
    
    // Constructor y configuración
    
    /**
     * Genera diagramas PlantUML para visualizar la arquitectura DDD
     */
    public void generateDDDDiagrams() {
        generateDomainLayerDiagram();
        generateApplicationLayerDiagram();
        generateInfrastructureLayerDiagram();
        generatePresentationLayerDiagram();
    }
    
    private void generateDomainLayerDiagram() {
        // Implementación de generación de diagrama para la capa de dominio
    }
    
    // Más métodos para generar diagramas de otras capas
}
```

## Principios de Diseño

La capa de infraestructura se basa en los siguientes principios:

1. **Adaptadores de Puerto**: Implementa las interfaces (puertos) definidas en las capas internas
2. **Desacoplamiento**: Aísla las tecnologías específicas del núcleo de la aplicación
3. **Programación Reactiva**: Utiliza programación reactiva para operaciones asíncronas con la base de datos
4. **Inyección de Dependencias**: Utiliza Spring para la inyección de dependencias y configuración

## Relación con Otras Capas

- **Hacia el Dominio**: No afecta directamente la capa de dominio, pero proporciona implementaciones para sus repositorios
- **Hacia la Aplicación**: Implementa los puertos de salida definidos en la capa de aplicación
- **Hacia la Presentación**: Proporciona configuración para controladores REST y seguridad 