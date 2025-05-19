Introducción al Proyecto
Este proyecto constituye el backend de un Producto Mínimo Viable (MVP) para un programa de gestión de clínicas dentales. Desarrollado con Spring Boot y una arquitectura Domain-Driven Design (DDD), proporciona una base sólida para la gestión de datos clínicos en entornos odontológicos.
Características
El sistema está diseñado para gestionar:
Pacientes: Datos personales y de contacto.
Datos Dentales: Odontogramas completos para cada paciente.
Historia Clínica: Registro detallado de tratamientos, diagnósticos y evolución.
Citas: Programación, gestión y seguimiento de citas con pacientes.
Doctores: Información de profesionales, especialidades y disponibilidad.
Estructura del Proyecto
La estructura del proyecto sigue los principios de Domain-Driven Design (DDD), organizando el código en capas claramente definidas:
bash
Copiar
Editar
Odoonto.Back/src/main/java/odoonto/
├── domain/                  # La capa de dominio - el núcleo del negocio
│   ├── model/               # Entidades y agregados de dominio
│   ├── service/             # Servicios de dominio
│   ├── events/              # Eventos de dominio 
│   ├── policy/              # Políticas y reglas de negocio
│   └── exceptions/          # Excepciones específicas del dominio
│
├── application/             # Capa de aplicación - casos de uso
│   ├── dto/                 # Objetos de transferencia de datos
│   ├── port/                # Interfaces de servicios (puertos)
│   ├── mapper/              # Conversiones entre dominio y DTOs
│   ├── service/             # Implementaciones de servicios (adaptadores)
│   └── exceptions/          # Excepciones específicas de la aplicación
│
├── infrastructure/          # Implementaciones técnicas (adaptadores)
│   ├── persistence/         # Implementaciones de repositorios
│   ├── config/              # Configuraciones (Spring, etc.)
│   ├── security/            # Implementaciones de seguridad
│   ├── tools/               # Herramientas y utilidades
│   └── testing/             # Testing de infraestructura
│
└── presentation/            # API y controladores (adaptadores primarios)
    ├── rest/                # API REST
    │   ├── controller/      # Controladores REST
    │   └── advice/          # Manejadores de excepciones para REST
    └── documentation/       # Documentación de API
Arquitectura Domain-Driven Design (DDD)
Capa de Dominio
Es el núcleo del sistema y contiene la lógica de negocio esencial. Aquí se definen:
Entidades y Agregados: Representan los conceptos fundamentales del negocio (Paciente, Doctor, Odontograma, etc.).
Objetos de Valor: Elementos sin identidad propia pero con validaciones específicas (Email, Teléfono, etc.).
Servicios de Dominio: Operaciones que no pertenecen naturalmente a una entidad.
Eventos de Dominio: Notificaciones de cambios significativos en el estado del sistema.
 Ver detalles de la capa de Dominio
Capa de Aplicación
Coordina la ejecución de tareas específicas del sistema. Actúa como intermediario entre la capa de dominio y las capas externas:
Casos de Uso: Definen las operaciones que puede realizar el sistema.
DTOs: Objetos para transferir datos entre capas.
Servicios de Aplicación: Implementan los casos de uso utilizando el dominio.
Puertos: Interfaces que definen cómo interactúa la aplicación con el exterior.
 Ver detalles de la capa de Aplicación
Capa de Infraestructura
Proporciona el soporte técnico para el sistema:
Persistencia: Implementaciones concretas de repositorios (Firestore, etc.).
Configuración: Configuración de Spring, seguridad, etc.
Herramientas: Utilidades para la generación de diagramas y documentación.
 Ver detalles de la capa de Infraestructura
Capa de Presentación (Interfaces)
Gestiona la interacción con el usuario o sistemas externos:
Controladores REST: Exponen la funcionalidad del sistema mediante API REST.
Documentación API: Incluye la configuración de Swagger para probar la API.
 Ver detalles de la capa de Presentación
Endpoints de la API REST
La API REST proporciona los siguientes endpoints principales:
Pacientes
GET /api/patients – Obtener todos los pacientes.
GET /api/patients/{id} – Obtener un paciente por ID.
POST /api/patients – Crear nuevo paciente.
PUT /api/patients/{id} – Actualizar paciente existente.
DELETE /api/patients/{id} – Eliminar paciente.
Odontogramas
GET /api/patients/{patientId}/odontogram – Obtener el odontograma de un paciente.
POST /api/patients/{patientId}/odontogram/lesions – Añadir lesión al odontograma.
POST /api/patients/{patientId}/odontogram/treatments – Añadir tratamiento al odontograma.
Historial Médico
GET /api/patients/{patientId}/medical-record – Obtener el historial médico de un paciente.
POST /api/patients/{patientId}/medical-record/entries – Añadir entrada al historial médico.
Doctores
GET /api/doctors – Obtener todos los doctores.
GET /api/doctors/{id} – Obtener doctor por ID.
POST /api/doctors – Crear nuevo doctor.
Citas
GET /api/appointments – Obtener todas las citas.
GET /api/appointments/{id} – Obtener cita por ID.
POST /api/appointments – Crear nueva cita.
PUT /api/appointments/{id}/status – Actualizar estado de una cita (por ejemplo, marcar como cancelada o completada).
Tecnologías Utilizadas
Java 21 – Versión moderna del lenguaje Java.
Spring Boot 3.4.5 – Framework para agilizar el desarrollo de aplicaciones Spring.
Spring WebFlux – Stack reactivo de Spring para manejar operaciones asíncronas con Mono/Flux.
Firestore (Google Cloud) – Base de datos NoSQL de Google Cloud para almacenamiento de datos.
Firebase Admin SDK – Utilizado para autenticación y acceso (por ejemplo, JWT, integración con Firebase Auth).
PlantUML – Herramienta para la generación de diagramas de la arquitectura DDD.
Swagger/OpenAPI – Documentación interactiva de la API REST para pruebas y visualización.
Lombok – Librería para reducir código repetitivo (anotaciones como @Getter, @Setter, etc.).
Configuración del Entorno de Desarrollo
Requisitos Previos
Java 21 (JDK).
Maven 3.8+ instalado en el sistema.
Un IDE compatible con Spring Boot (se recomienda IntelliJ IDEA).
Cuenta de Google Cloud con Firestore habilitado y credenciales de servicio.
Pasos para Configurar el Proyecto Localmente
Clonar el repositorio:
bash
Copiar
Editar
git clone https://github.com/tu-usuario/Odoonto.Back.git
cd Odoonto.Back
Configurar credenciales de Firebase:
Crear un proyecto en la consola de Firebase.
Habilitar Firestore (modo nativo) en el proyecto de Firebase.
Descargar el archivo de credenciales del servicio (por ejemplo, firebase-service-account.json).
Colocar este archivo en la ruta src/main/resources/ del proyecto.
Compilar el proyecto:
bash
Copiar
Editar
mvn clean install
Ejecutar la aplicación:
bash
Copiar
Editar
mvn spring-boot:run
Esto iniciará el servidor embebido en http://localhost:8080.
Acceder a la documentación de la API:
Una vez levantada la aplicación, visitar la URL http://localhost:8080/swagger-ui.html para ver la documentación interactiva de la API (Swagger UI).
Generación de Diagramas DDD
El proyecto incluye herramientas para generar diagramas PlantUML que documentan la arquitectura DDD:
Ejecutar el script de generación de diagramas:
En sistemas Windows, use el archivo por lotes proporcionado:
bash
Copiar
Editar
./generate-diagrams.bat
Este script compila el proyecto y ejecuta el generador de diagramas.
Ubicación de los diagramas generados:
Los diagramas se guardarán en la carpeta de destino del proyecto, por ejemplo:
bash
Copiar
Editar
target/diagrams/
Dentro de esta carpeta encontrará archivos .puml (definiciones PlantUML) y .png/.svg con las imágenes generadas para cada entidad (Paciente, Odontograma, Doctor, etc.).
Visualización:
Puede abrir target/diagrams/index.html en un navegador para ver un índice de los diagramas generados y navegar entre ellos. Alternativamente, abra directamente los archivos PNG para ver los diagramas estáticos.
Los siguientes apartados corresponden a la documentación detallada por capa, que podría ubicarse en archivos README separados para cada capa. En ellos se profundiza en la función, estructura interna y responsabilidades de cada capa de la arquitectura DDD.
Capa de Dominio
Descripción
La capa de dominio es el núcleo de la aplicación Odoonto y representa el corazón del sistema. Aquí se definen todas las entidades, reglas de negocio y conceptos fundamentales del dominio odontológico. Esta capa está aislada de preocupaciones externas como interfaces de usuario, bases de datos o servicios externos.
Estructura
bash
Copiar
Editar
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
Componentes Principales
Agregados (Aggregates)
Los agregados son grupos de entidades y objetos de valor que se tratan como una unidad cohesiva y tienen un límite transaccional claro. Los principales agregados son:
Patient – Representa un paciente con toda su información asociada.
Doctor – Profesional odontológico con sus especialidades.
Odontogram – Representación dental completa de un paciente.
MedicalRecord – Historial médico-dental de un paciente.
Appointment – Cita programada entre paciente y doctor.
Los agregados actúan como raíz para un conjunto de entidades relacionadas, garantizando la consistencia interna.
Entidades (Entities)
Las entidades son objetos del dominio con una identidad única y un ciclo de vida. Ejemplos clave:
Tooth – Cada diente dentro del odontograma de un paciente.
Lesion – Problema o patología en un diente específico.
Treatment – Tratamiento aplicado a un diente.
MedicalEntry – Entrada individual en el historial médico (evolución, diagnóstico, etc.).
Objetos de Valor (Value Objects)
Los objetos de valor son inmutables y se caracterizan por sus atributos, no por una identidad. Incluyen:
EmailAddress – Dirección de correo electrónico validada.
PhoneNumber – Número de teléfono con formato específico.
PersonName – Nombre completo estructurado (por ejemplo, con nombres y apellidos separados).
Address – Dirección postal (calle, ciudad, etc.).
ToothFace – Representación de las caras de un diente (VESTIBULAR, LINGUAL, etc.).
LesionType – Tipos de lesiones dentales (Caries, Fractura, etc.).
TreatmentType – Tipos de tratamientos dentales (Profilaxis, Endodoncia, etc.).
Servicios de Dominio (Domain Services)
Cuando una operación de negocio no pertenece naturalmente a una entidad o agregado específico, se implementa como un servicio de dominio:
DentalDiagnosisService – Lógica para realizar diagnósticos dentales basados en los datos del odontograma.
TreatmentPlanService – Generación de planes de tratamiento dental personalizados para un paciente.
Eventos de Dominio (Domain Events)
Los eventos representan hechos significativos que han ocurrido en el dominio:
PatientRegisteredEvent – Indica que se ha registrado un nuevo paciente en el sistema.
AppointmentScheduledEvent – Indica que se ha programado una nueva cita.
TreatmentCompletedEvent – Indica que se ha completado un tratamiento en un paciente.
Políticas (Policies)
Las políticas encapsulan reglas de negocio complejas que abarcan múltiples entidades:
SchedulingPolicy – Reglas para programar citas (disponibilidad de doctores, evitar solapamientos, duración estándar, etc.).
MedicalRecordPolicy – Reglas para la gestión de historiales médicos (qué datos se requieren para añadir entradas, etc.).
Excepciones (Exceptions)
Las excepciones específicas del dominio aseguran que los errores se manejen adecuadamente y mantienen la integridad de las reglas de negocio:
InvalidPersonDataException – Se lanza cuando los datos personales de una entidad son inválidos (por ejemplo, nombre vacío, fecha de nacimiento futura, etc.).
DuplicateLesionException – Indica un intento de registrar una lesión duplicada en un odontograma.
InvalidToothNumberException – Número de diente fuera de rango o inexistente.
Diagrama UML
El siguiente diagrama muestra las relaciones entre las principales entidades del dominio: 
Ejemplos de Código
Definición de un Agregado (Aggregate Root)
java
Copiar
Editar
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
Definición de un Objeto de Valor (Value Object)
java
Copiar
Editar
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
Principios de Diseño
La capa de dominio sigue varios principios clave:
Encapsulación – Los detalles de implementación están ocultos dentro de las entidades y objetos de valor.
Inmutabilidad – Los objetos de valor son inmutables para evitar efectos secundarios indeseados.
Invariantes – Se mantienen las reglas de negocio y restricciones (invariantes) en todo momento dentro de los agregados.
Tell, Don't Ask – Se prefiere que las entidades ofrezcan comportamientos (métodos) en lugar de exponer datos para que otras capas tomen decisiones.
Relación con Otras Capas
La capa de Dominio no depende de ninguna otra capa; es autónoma.
Define interfaces de repositorio (por ejemplo, en el paquete domain.repository) que son implementadas por la capa de infraestructura.
Expone servicios de dominio y agregados que son utilizados por la capa de aplicación para realizar casos de uso.
Capa de Aplicación
Descripción
La capa de aplicación actúa como coordinadora entre el mundo exterior y el dominio del sistema Odoonto. Se encarga de orquestar la ejecución de los casos de uso, traduciendo las solicitudes externas en operaciones de dominio y proporcionando resultados en formatos adecuados para los consumidores (por ejemplo, DTOs para la capa de presentación).
Estructura
graphql
Copiar
Editar
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
Componentes Principales
Puertos de Entrada (Casos de Uso)
Los puertos de entrada definen las operaciones que la aplicación puede realizar. Siguen el patrón puerto-adaptador, donde el puerto es la interfaz y la implementación es el adaptador. Están organizados por contexto (bounded context) para reflejar las capacidades del sistema:
Patient:
PatientCreateUseCase – Crear nuevo paciente.
PatientUpdateUseCase – Actualizar datos de un paciente existente.
PatientDeleteUseCase – Eliminar paciente.
PatientQueryUseCase – Consultar pacientes (por id o todos).
PatientOdontogramUseCase – Gestionar el odontograma vinculado a un paciente (por ejemplo, obtenerlo).
Odontogram:
OdontogramQueryUseCase – Consultar odontograma de un paciente.
LesionAddUseCase – Añadir una lesión a un diente en el odontograma.
TreatmentAddUseCase – Registrar un tratamiento en un diente.
(LesionRemoveUseCase, TreatmentRemoveUseCase podrían existir para quitar información)
MedicalRecord:
MedicalRecordCreateUseCase – Crear un nuevo historial médico para un paciente.
MedicalEntryAddUseCase – Añadir una entrada al historial médico (ej. un diagnóstico o nota).
(MedicalEntryRemoveUseCase para eliminar entradas, si corresponde)
MedicalRecordQueryUseCase – Consultar el historial médico de un paciente.
Doctor:
DoctorCreateUseCase – Registrar un nuevo doctor.
DoctorUpdateUseCase – Actualizar información de un doctor.
DoctorDeleteUseCase – Eliminar un doctor del sistema.
DoctorQueryUseCase – Consultar doctores (por id o listar todos).
Appointment:
AppointmentCreateUseCase – Programar una nueva cita.
AppointmentCancelUseCase – Cancelar una cita existente.
AppointmentUpdateUseCase – Reprogramar o actualizar detalles de una cita.
AppointmentQueryUseCase – Consultar citas (por id o listar todas).
DTOs (Data Transfer Objects)
Los DTOs facilitan la comunicación entre capas, desacoplando la representación externa de la estructura interna del dominio:
DTOs de Solicitud (Request): encapsulan datos recibidos en peticiones externas para crear o modificar entidades:
PatientCreateDTO – Datos necesarios para crear un paciente (nombre, email, etc.).
MedicalRecordCreateDTO – Datos para crear un historial médico nuevo.
AppointmentCreateDTO – Información para programar una cita (fecha, paciente, doctor, etc.).
(DoctorCreateDTO, LesionCreateDTO, etc., según corresponda).
DTOs de Respuesta (Response): encapsulan datos que se devuelven al cliente como resultado de una operación:
PatientDTO – Representación de un paciente para consumo externo (incluye id, nombre, etc.).
OdontogramDTO – Datos del odontograma listos para mostrar en UI (por ejemplo, lista de dientes, lesiones, tratamientos).
AppointmentDTO – Información de una cita (fecha, estado, paciente, doctor).
(DoctorDTO, MedicalRecordDTO, etc., según corresponda).
Mapeadores (Mappers)
Los mapeadores transforman entre objetos del dominio y DTOs, manteniendo separado el modelo interno de la representación externa:
PatientMapper – Conversión entre Patient (entidad de dominio) y PatientDTO (objeto de salida de API).
OdontogramMapper – Conversión entre Odontogram (dominio) y OdontogramDTO (salida).
AppointmentMapper – Conversión entre Appointment (dominio) y AppointmentDTO (salida).
DoctorMapper, MedicalRecordMapper, etc., para otros contextos.
Servicios de Aplicación
Implementan la lógica de los casos de uso, orquestando la interacción con el dominio y otras capas:
PatientCreateService – Implementa la creación de un paciente utilizando PatientRepository (puerto de salida) y PatientMapper.
PatientUpdateService – Actualiza información de pacientes existentes (busca, modifica atributos, guarda).
PatientDeleteService, PatientQueryService, PatientOdontogramService, etc., para otras operaciones con pacientes.
LesionAddService (Odontogram) – Añade lesiones al odontograma de un paciente, verificando que la lesión no exista ya (usa lógica de dominio).
TreatmentAddService (Odontogram) – Registra un tratamiento en un diente específico.
LesionRemoveService, TreatmentRemoveService para eliminar información si se soporta.
OdontogramQueryService para obtener el odontograma completo.
AppointmentCreateService – Crea nuevas citas verificando disponibilidad (usa SchedulingPolicy del dominio y repositorios para chequear conflictos).
AppointmentCancelService – Gestiona la cancelación de citas (cambia estado, emite evento de dominio si aplica).
AppointmentUpdateService, AppointmentQueryService para otras operaciones sobre citas.
DoctorCreateService, DoctorUpdateService, DoctorDeleteService, DoctorQueryService para operaciones sobre doctores.
Excepciones de Aplicación
Excepciones específicas para errores a nivel de aplicación, típicamente lanzadas en casos de uso cuando algo falla que no es estrictamente una violación de regla de negocio sino una condición de aplicación:
PatientNotFoundException – Se lanza cuando un paciente no existe (al buscar por ID, por ejemplo).
DoctorNotFoundException – Doctor no encontrado.
AppointmentConflictException – Conflicto de horarios al intentar crear o reprogramar una cita (ya existe una cita en el mismo horario).
MedicalRecordNotFoundException, OdontogramNotFoundException, etc., para otros contextos.
Diagrama UML
El siguiente diagrama ilustra la estructura de la capa de aplicación y sus interacciones principales con las demás capas: 
Ejemplos de Código
Definición de un Puerto de Entrada (Caso de Uso)
java
Copiar
Editar
public interface PatientCreateUseCase {
    /**
     * Crea un nuevo paciente en el sistema.
     * @param patientCreateDTO Datos del paciente a crear.
     * @return Mono con el DTO del paciente creado.
     */
    Mono<PatientDTO> createPatient(PatientCreateDTO patientCreateDTO);
}
Implementación de un Servicio de Caso de Uso
java
Copiar
Editar
@Service
@RequiredArgsConstructor
public class PatientCreateService implements PatientCreateUseCase {
    
    private final ReactivePatientRepository patientRepository;
    private final PatientMapper patientMapper;
    
    @Override
    public Mono<PatientDTO> createPatient(PatientCreateDTO patientCreateDTO) {
        // 1. Mapear DTO a entidad de dominio
        Patient patient = patientMapper.toDomain(patientCreateDTO);
        
        // 2. Persistir en el repositorio (capa de infraestructura)
        return patientRepository.save(patient)
            // 3. Mapear resultado a DTO de respuesta y retornarlo
            .map(patientMapper::toDTO);
    }
}
Ejemplo de DTO de Solicitud
java
Copiar
Editar
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
Principios de Diseño
La capa de aplicación sigue varios principios clave:
Separación de Responsabilidades – Cada caso de uso (servicio) tiene una responsabilidad clara y única.
Inversión de Dependencias – La capa de aplicación depende de abstracciones (interfaces de puertos), no de implementaciones concretas, permitiendo aislar detalles de infraestructura.
Casos de Uso Aislados – Cada caso de uso está encapsulado en su propia interfaz e implementación, facilitando pruebas unitarias y cambios independientes.
Programación Reactiva – Dado que se utiliza Spring WebFlux, muchos casos de uso retornan tipos reactivos (Mono, Flux) permitiendo un manejo eficiente de operaciones asíncronas.
Relación con Otras Capas
Con la Capa de Dominio: Invoca métodos de entidades y servicios de dominio para ejecutar la lógica de negocio. La capa de aplicación traduce resultados de dominio a DTOs para salida.
Con la Capa de Infraestructura: Define los puertos de salida (interfaces de repositorios) que la infraestructura implementa; utiliza estas interfaces para persistir y recuperar datos.
Con la Capa de Presentación: Ofrece puertos de entrada (casos de uso) que son invocados por los controladores REST en la capa de presentación. La aplicación no depende de la presentación, sino al revés.
Capa de Infraestructura
Descripción
La capa de infraestructura proporciona implementaciones concretas para las abstracciones definidas en las capas de dominio y aplicación. Se encarga de gestionar la interacción con tecnologías y sistemas externos como bases de datos, servicios en la nube, seguridad y herramientas de soporte. En Odoonto, la infraestructura implementa la persistencia de datos utilizando Google Firestore, la configuración de Spring Boot, aspectos de seguridad (por ejemplo, autenticación con Firebase) y herramientas de desarrollo como generadores de diagramas.
Estructura
bash
Copiar
Editar
infrastructure/
├── persistence/         # Implementaciones de repositorios
│   ├── reactive/        # Adaptadores reactivos para repositorios (Firestore)
│   └── entity/          # Entidades de persistencia (mapeos a Firestore)
├── config/              # Configuraciones (Spring, Firestore, etc.)
├── security/            # Configuraciones/Adaptadores de seguridad (Auth)
├── tools/               # Herramientas y utilidades (generación de diagramas, etc.)
├── messaging/           # Implementación de mensajería (si aplica)
└── testing/             # Utilidades para pruebas (carga de datos, etc.)
Componentes Principales
Persistencia
La persistencia gestiona el almacenamiento y recuperación de datos en la base de datos:
Entidades de Persistencia – Clases que representan cómo se almacenan las entidades de dominio en Firestore (u otra base de datos). Por ejemplo:
FirestorePatientEntity – Representación de un paciente para Firestore.
FirestoreOdontogramEntity – Representación de un odontograma para Firestore.
FirestoreMedicalRecordEntity, FirestoreDoctorEntity, etc.
Estas suelen corresponder a las entidades/agregados de la capa de dominio, adaptadas al esquema de la base de datos.
Adaptadores Reactivos de Repositorio – Clases que implementan las interfaces de repositorio definidas en la capa de aplicación (puertos de salida). Por ejemplo:
ReactivePatientRepositoryAdapter – Implementación del repositorio de pacientes usando Firestore.
ReactiveOdontogramRepositoryAdapter, ReactiveMedicalRecordRepositoryAdapter, etc.
Estas clases utilizan la API reactiva de Spring Cloud GCP Firestore (como FirestoreTemplate y operaciones reactivas) para realizar operaciones de base de datos de forma asíncrona. Cada adaptador mapea entre las entidades de dominio y las entidades de persistencia.
Configuración
La configuración establece los beans y parámetros necesarios para que el sistema funcione correctamente:
FirestoreConfig – Configuración de la conexión a Firestore (por ejemplo, credenciales, identificador de proyecto). Define beans como Firestore y FirestoreTemplate para acceder a la DB.
DiagramGeneratorConfig – Configuración para la generación automática de diagramas PlantUML al iniciar la aplicación (por medio de DDDDiagramGenerator).
StartupMenuConfig – Configura un menú interactivo en la consola al iniciar la app, permitiendo opciones como generar diagramas o inicializar datos.
ServiceInitializer – Inicialización de servicios o datos al arranque (por ejemplo, cargar datos de prueba si es entorno dev).
WebConfig – Configuraciones generales de la capa web (CORS, formateadores JSON, etc.).
Seguridad
Componentes relacionados con la autenticación y autorización:
SecurityConfig – Configuración de Spring Security para asegurar los endpoints (por ejemplo, JWT requerido, roles, etc.).
FirebaseAuthAdapter (si existe) – Integración con Firebase Authentication para validar tokens JWT en las peticiones entrantes. Esto podría incluir filtros de seguridad personalizados.
Herramientas
Utilidades y herramientas de soporte al desarrollo:
DDDDiagramGenerator – Componente que analiza el código y genera automáticamente diagramas PlantUML de la arquitectura DDD (usado por el menú interactivo o al inicio automático).
GenerateDDDDiagrams – Clase con método main para ejecutar la generación de diagramas desde la línea de comando (usada en el script .bat).
FixPumlDiagrams – Utilidad para ajustar o corregir diagramas PlantUML generados (por ejemplo, formateo).
Testing
Herramientas y utilidades para pruebas:
DataSeeder – Clase que carga datos iniciales (pacientes, doctores, etc.) en la base de datos para propósitos de desarrollo o pruebas de integración. Se puede invocar condicionalmente para poblar Firestore con datos de ejemplo.
Diagrama UML
El siguiente diagrama muestra la estructura de la capa de infraestructura y cómo se relacionan sus componentes principales: 
Ejemplos de Código
Adaptador de Repositorio Reactivo
java
Copiar
Editar
@Component
@RequiredArgsConstructor
public class ReactivePatientRepositoryAdapter implements ReactivePatientRepository {

    private final FirestoreTemplate firestoreTemplate;
    
    @Override
    public Mono<Patient> save(Patient patient) {
        // Convertir de entidad de dominio a entidad de Firestore
        FirestorePatientEntity entity = mapToEntity(patient);
        
        // Guardar en Firestore de forma reactiva
        return firestoreTemplate.save(entity)
            // Convertir la entidad de Firestore guardada de vuelta a entidad de dominio
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
    
    // Métodos de mapeo entre dominio y persistencia...
    private FirestorePatientEntity mapToEntity(Patient patient) {
        // Implementación del mapeo de dominio a persistencia
        // ...
    }
    
    private Patient mapToDomain(FirestorePatientEntity entity) {
        // Implementación del mapeo de persistencia a dominio
        // ...
    }
}
Configuración de Firestore
java
Copiar
Editar
@Configuration
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
Generación de Diagramas DDD (Herramienta)
java
Copiar
Editar
@Component
public class DDDDiagramGenerator {

    private final List<Class<?>> domainClasses;
    private final List<Class<?>> applicationClasses;
    
    // Constructor y configuración omitidos por brevedad
    
    /**
     * Genera diagramas PlantUML para visualizar la arquitectura DDD.
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
    
    // ... Métodos similares para las otras capas
}
Principios de Diseño
La capa de infraestructura se basa en los siguientes principios:
Adaptadores de Puerto – Implementa las interfaces (puertos) definidas en las capas internas (dominio/aplicación), siguiendo el patrón hexagonal (puerto-adaptador).
Desacoplamiento – Aísla las tecnologías y detalles externos del núcleo de la aplicación; un cambio de base de datos o proveedor de autenticación no afecta al dominio.
Programación Reactiva – Utiliza la programación reactiva (Reactive Streams) para el acceso a datos y otras operaciones I/O, asegurando escalabilidad en las operaciones asíncronas.
Inyección de Dependencias – Aprovecha Spring para configurar e inyectar dependencias (Beans), facilitando la configuración de componentes externos y su uso en adaptadores.
Relación con Otras Capas
Con la Capa de Dominio: La infraestructura no debe contener lógica de negocio, pero proporciona las implementaciones de repositorios que el dominio (o la aplicación) necesita. Por ejemplo, el dominio define una interfaz PatientRepository, la capa de infraestructura provee ReactivePatientRepositoryAdapter.
Con la Capa de Aplicación: Implementa los puertos de salida que la capa de aplicación declara. La aplicación invoca a la infraestructura a través de estas interfaces, sin conocer los detalles (por ejemplo, tipo de base de datos).
Con la Capa de Presentación: Proporciona configuraciones y aspectos técnicos que afectan a la presentación (por ejemplo, configuración CORS en WebConfig, seguridad en SecurityConfig, integración de Swagger en SwaggerConfig). Sin embargo, la lógica de presentación (controladores) sigue estando en su propia capa.
Capa de Presentación
Descripción
La capa de presentación en Odoonto actúa como el punto de entrada para que los clientes externos (por ejemplo, la interfaz web de la clínica dental) interactúen con el sistema. Implementa la API REST que expone las funcionalidades a consumidores externos como aplicaciones frontend, apps móviles u otros servicios. Esta capa traduce las solicitudes HTTP en llamadas a los casos de uso de la capa de aplicación y transforma los resultados (entidades o DTOs) en respuestas HTTP (generalmente JSON). También maneja la validación de la entrada, la gestión de errores a nivel de API y aspectos de seguridad relacionados con las solicitudes.
Estructura
bash
Copiar
Editar
presentation/
├── rest/                # API REST
│   ├── controller/      # Controladores REST (adaptadores primarios de entrada)
│   └── advice/          # Manejadores de excepciones para REST
└── documentation/       # Documentación de API (Swagger, etc.)
Componentes Principales
Controladores REST
Los controladores actúan como adaptadores de entrada que conectan las peticiones HTTP con los casos de uso de la capa de aplicación. Por cada agregado o concepto principal del dominio suele haber un controlador correspondiente que maneja las rutas relacionadas:
PatientController – Endpoints para gestión de pacientes:
GET /api/patients – Listar todos los pacientes.
GET /api/patients/{id} – Obtener un paciente por ID.
POST /api/patients – Crear un nuevo paciente.
PUT /api/patients/{id} – Actualizar un paciente existente.
DELETE /api/patients/{id} – Eliminar un paciente.
OdontogramController – Endpoints para gestión de odontogramas (asociados a un paciente):
GET /api/patients/{patientId}/odontogram – Obtener el odontograma del paciente.
POST /api/patients/{patientId}/odontogram/lesions – Añadir una lesión al odontograma.
POST /api/patients/{patientId}/odontogram/treatments – Añadir un tratamiento al odontograma.
MedicalRecordController – Endpoints para gestión de historiales médicos:
GET /api/patients/{patientId}/medical-record – Obtener el historial médico de un paciente.
POST /api/patients/{patientId}/medical-record/entries – Añadir una entrada al historial médico.
DoctorController – Endpoints para gestión de doctores:
GET /api/doctors – Listar todos los doctores.
GET /api/doctors/{id} – Obtener detalle de un doctor por ID.
POST /api/doctors – Registrar un nuevo doctor.
AppointmentController – Endpoints para gestión de citas:
GET /api/appointments – Listar todas las citas.
GET /api/appointments/{id} – Obtener una cita por ID.
POST /api/appointments – Crear una nueva cita.
PUT /api/appointments/{id}/status – Actualizar el estado de una cita (confirmar, cancelar, completar, etc.).
Cada controlador recibe las peticiones, valida los datos de entrada (por ejemplo, usando @Valid en los DTOs), y utiliza los casos de uso de la capa de aplicación para ejecutar la lógica. Las respuestas de los casos de uso (Mono/Flux de DTOs) son retornadas directamente al cliente en formato JSON.
Manejadores de Excepciones (Controller Advice)
El componente GlobalExceptionHandler (anotado con @RestControllerAdvice) proporciona un manejo centralizado de errores para la capa de presentación:
Captura excepciones lanzadas desde las capas inferiores (por ejemplo, DomainException, PatientNotFoundException, MethodArgumentNotValidException, etc.).
Transforma cada excepción en una respuesta HTTP adecuada, estableciendo el código de estado (por ejemplo, 400 Bad Request, 404 Not Found) y un cuerpo consistente (por ejemplo, un objeto JSON con mensaje de error).
Estandariza el formato de los errores para que los clientes de la API puedan manejar las respuestas de error de forma uniforme.
Por ejemplo, si una validación de datos falla, MethodArgumentNotValidException es manejada para devolver un 400 con detalles de los campos inválidos.
Documentación de API
La documentación facilita el entendimiento y consumo de la API por terceros:
SwaggerConfig – Configuración de Swagger/OpenAPI para generar la documentación interactiva. Define información de la API (título, descripción, versión, contacto) y el esquema de seguridad (por ejemplo, JWT Bearer). Esto permite que en swagger-ui.html se muestren todos los endpoints documentados.
DDDDocumentation – (si existe) Podría ser un controlador adicional para servir la documentación de los diagramas DDD (por ejemplo, los endpoints bajo /documentation/ddd mencionados en el README de PlantUML).
Diagrama UML
El siguiente diagrama ilustra la estructura de la capa de presentación, mostrando controladores y su interacción con otras capas: 
Ejemplos de Código
Controlador REST (ejemplo simplificado de PatientController)
java
Copiar
Editar
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
    public Mono<PatientDTO> updatePatient(@PathVariable String id,
                                          @Valid @RequestBody PatientUpdateDTO patientDTO) {
        return patientUpdateUseCase.updatePatient(id, patientDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePatient(@PathVariable String id) {
        return patientDeleteUseCase.deletePatient(id);
    }
}
Manejador Global de Excepciones
java
Copiar
Editar
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
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
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
Configuración de Swagger (OpenAPI 3)
java
Copiar
Editar
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
                .license(new License().name("Licencia Propietaria")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components().addSecuritySchemes("bearerAuth", 
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token de acceso JWT")));
    }
}
Principios de Diseño
La capa de presentación sigue varios principios clave:
Adaptadores de Entrada – Los controladores son adaptadores que traducen las solicitudes HTTP (protocolos externos) a llamadas a los casos de uso internos, decodificando la información de la petición.
Separación de Responsabilidades – Los controladores se limitan a manejar la comunicación (HTTP/JSON); no contienen lógica de negocio, que es delegada a la capa de aplicación.
Validación de Entrada – Antes de invocar la lógica de negocio, validan los datos de entrada (usando anotaciones de Bean Validation y manejadores de excepción para errores de validación).
Manejo Centralizado de Errores – Utiliza un controller advice global para capturar excepciones y convertirlas en respuestas coherentes, evitando duplicación de manejo de errores en cada controlador.
Programación Reactiva – Dado que se usa WebFlux, los controladores manejan Mono y Flux como tipos de retorno, respondiendo de manera no bloqueante a las solicitudes concurrentes.
Relación con Otras Capas
Hacia la Capa de Aplicación: La capa de presentación invoca métodos de los casos de uso (interfaces de puertos de entrada) que ofrece la capa de aplicación. No contiene lógica de negocio propia; simplemente formatea la entrada, llama al servicio apropiado y formatea la salida.
Independencia del Dominio: La capa de presentación no depende directamente del dominio; interactúa a través de la capa de aplicación. Los controladores manejan DTOs, no las entidades de dominio, lo cual mantiene desacopladas las preocupaciones de presentación y negocio.
Uso de Infraestructura: Aprovecha la configuración proporcionada por infraestructura (por ejemplo, seguridad con filtros JWT definidos en SecurityConfig, documentación generada por SwaggerConfig), pero no conoce los detalles internos de cómo están implementadas esas configuraciones.
Documentación REST API
La API REST está documentada utilizando OpenAPI 3.0 (Swagger). Una vez que la aplicación está en ejecución, se puede acceder a la documentación interactiva en:
bash
Copiar
Editar
http://localhost:8080/swagger-ui.html
Esta interfaz permite:
Explorar todos los endpoints disponibles y ver sus detalles (métodos HTTP, URL, parámetros, etc.).
Visualizar los modelos de datos (esquemas de los DTOs de entrada y salida).
Probar las llamadas a la API directamente desde el navegador, incluyendo enviar peticiones con parámetros y bodies JSON.
Entender los códigos de estado de respuesta y posibles mensajes de error que la API puede retornar.