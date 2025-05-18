# Capa de Presentación

## Descripción

La capa de presentación en Odoonto actúa como el punto de entrada para que los clientes externos interactúen con el sistema. Implementa la API REST que expone las funcionalidades del sistema a consumidores externos como aplicaciones frontend, servicios móviles u otros sistemas.

Esta capa traduce las solicitudes HTTP en llamadas a los casos de uso de la aplicación y transforma los resultados en respuestas HTTP adecuadas. También maneja la validación de entrada, la gestión de errores y la seguridad a nivel de API.

## Estructura

```
presentation/
├── rest/                # API REST
│   ├── controller/      # Controladores REST (adaptadores primarios)
│   └── advice/          # Manejadores de excepciones para REST
└── documentation/       # Documentación de API
```

## Componentes Principales

### Controladores REST

Los controladores actúan como adaptadores que conectan las peticiones HTTP con los casos de uso de la aplicación:

- **PatientController**: Endpoints para gestión de pacientes
  - `GET /api/patients` - Listar pacientes
  - `GET /api/patients/{id}` - Obtener paciente por ID
  - `POST /api/patients` - Crear nuevo paciente
  - `PUT /api/patients/{id}` - Actualizar paciente
  - `DELETE /api/patients/{id}` - Eliminar paciente

- **OdontogramController**: Endpoints para gestión de odontogramas
  - `GET /api/patients/{patientId}/odontogram` - Obtener odontograma
  - `POST /api/patients/{patientId}/odontogram/lesions` - Añadir lesión
  - `POST /api/patients/{patientId}/odontogram/treatments` - Añadir tratamiento

- **MedicalRecordController**: Endpoints para gestión de historiales médicos
  - `GET /api/patients/{patientId}/medical-record` - Obtener historial
  - `POST /api/patients/{patientId}/medical-record/entries` - Añadir entrada

- **DoctorController**: Endpoints para gestión de doctores
  - `GET /api/doctors` - Listar doctores
  - `GET /api/doctors/{id}` - Obtener doctor por ID
  - `POST /api/doctors` - Crear doctor

- **AppointmentController**: Endpoints para gestión de citas
  - `GET /api/appointments` - Listar citas
  - `GET /api/appointments/{id}` - Obtener cita por ID
  - `POST /api/appointments` - Crear cita
  - `PUT /api/appointments/{id}/status` - Actualizar estado

### Manejadores de Excepciones

El componente `GlobalExceptionHandler` proporciona un manejo centralizado de errores:

- Captura excepciones de dominio y aplicación
- Las transforma en respuestas HTTP adecuadas
- Estandariza el formato de los mensajes de error

### Documentación de API

La documentación facilita el entendimiento y consumo de la API:

- **SwaggerConfig**: Configuración de Swagger/OpenAPI
- **DDDDocumentation**: Controlador para acceder a documentación DDD

## Diagrama UML

El siguiente diagrama ilustra la estructura de la capa de presentación:

![Diagrama de Presentación](../documentation/plantuml/presentation_layer.png)

## Ejemplos de Código

### Controlador REST

```java
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientCreateUseCase patientCreateUseCase;
    private final PatientQueryUseCase patientQueryUseCase;
    private final PatientUpdateUseCase patientUpdateUseCase;
    private final PatientDeleteUseCase patientDeleteUseCase;
    
    @GetMapping
    public Flux<PatientDTO> getAllPatients() {
        return patientQueryUseCase.findAllPatients();
    }
    
    @GetMapping("/{id}")
    public Mono<PatientDTO> getPatientById(@PathVariable String id) {
        return patientQueryUseCase.findPatientById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Paciente no encontrado")));
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PatientDTO> createPatient(@Valid @RequestBody PatientCreateDTO patientDTO) {
        return patientCreateUseCase.createPatient(patientDTO);
    }
    
    @PutMapping("/{id}")
    public Mono<PatientDTO> updatePatient(
            @PathVariable String id, 
            @Valid @RequestBody PatientUpdateDTO patientDTO) {
        return patientUpdateUseCase.updatePatient(id, patientDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePatient(@PathVariable String id) {
        return patientDeleteUseCase.deletePatient(id);
    }
}
```

### Manejador Global de Excepciones

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFoundException(PatientNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
            
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Error de validación",
            errors,
            LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
```

### Configuración de Swagger

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI odoontoOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Odoonto - Sistema de Gestión Dental")
                .description("API REST para gestión de clínicas odontológicas")
                .version("v1")
                .contact(new Contact()
                    .name("Equipo Odoonto")
                    .email("contacto@odoonto.com"))
                .license(new License()
                    .name("Licencia Propietaria")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token de acceso JWT")));
    }
}
```

## Principios de Diseño

La capa de presentación sigue varios principios clave:

1. **Adaptadores de Entrada**: Los controladores actúan como adaptadores que traducen solicitudes externas a llamadas a casos de uso internos
2. **Separación de Responsabilidades**: Los controladores solo se encargan de la conversión protocolar, no contienen lógica de negocio
3. **Validación de Entrada**: Valida los datos de entrada antes de enviarlos a la capa de aplicación
4. **Manejo Centralizado de Errores**: Captura excepciones y las transforma en respuestas HTTP adecuadas
5. **Programación Reactiva**: Utiliza tipos reactivos (Mono/Flux) para manejar respuestas asíncronas

## Relación con Otras Capas

- **Hacia la Aplicación**: Consume los casos de uso definidos en la capa de aplicación
- **Independencia del Dominio**: No tiene dependencias directas con la capa de dominio
- **Configuración de Infraestructura**: Utiliza la configuración proporcionada por la capa de infraestructura para aspectos como seguridad y documentación

## Documentación REST API

La API REST está documentada utilizando OpenAPI 3.0 (Swagger). Puedes acceder a la documentación interactiva en:

```
http://localhost:8080/swagger-ui.html
```

Esta interfaz te permite:

- Explorar todos los endpoints disponibles
- Ver los formatos de solicitud y respuesta
- Probar las API directamente desde el navegador
- Entender los códigos de estado y posibles errores 