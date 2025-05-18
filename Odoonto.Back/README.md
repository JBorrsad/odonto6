# Odoonto.Back - Backend para Gestión de Clínicas Dentales

## Descripción

Este proyecto constituye el backend de un Producto Mínimo Viable (MVP) para un programa de gestión de clínicas dentales. Desarrollado con Spring Boot y una arquitectura Domain-Driven Design (DDD), proporciona una base sólida para la gestión de datos clínicos en entornos odontológicos.

## Características

El sistema está diseñado para gestionar:

- **Pacientes**: Datos personales y de contacto.
- **Datos Dentales**: Odontogramas completos para cada paciente.
- **Historia Clínica**: Registro detallado de tratamientos, diagnósticos y evolución.
- **Citas**: Programación, gestión y seguimiento de citas con pacientes.
- **Doctores**: Información de profesionales, especialidades y disponibilidad.

## Estructura del Proyecto

La estructura del proyecto sigue los principios de Domain-Driven Design (DDD), organizando el código en capas claramente definidas:

```
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
```

## Arquitectura Domain-Driven Design (DDD)

### Capa de Dominio

Es el núcleo del sistema y contiene la lógica de negocio esencial. Aquí se definen:

- **Entidades y Agregados**: Representan los conceptos fundamentales del negocio (Paciente, Doctor, Odontograma, etc.)
- **Objetos de Valor**: Elementos sin identidad propia pero con validaciones específicas (Email, Teléfono, etc.)
- **Servicios de Dominio**: Operaciones que no pertenecen naturalmente a una entidad
- **Eventos de Dominio**: Notificaciones de cambios significativos en el estado del sistema

![Capa de Dominio](src/main/java/odoonto/documentation/plantuml/domain_layer.png)

[Ver detalles de la capa de Dominio](src/main/java/odoonto/domain/README.md)

### Capa de Aplicación

Coordina la ejecución de tareas específicas del sistema. Actúa como intermediario entre la capa de dominio y las capas externas:

- **Casos de Uso**: Definen las operaciones que puede realizar el sistema
- **DTOs**: Objetos para transferir datos entre capas
- **Servicios de Aplicación**: Implementan los casos de uso utilizando el dominio
- **Puertos**: Interfaces que definen cómo interactúa la aplicación con el exterior

![Capa de Aplicación](src/main/java/odoonto/documentation/plantuml/application_layer.png)

[Ver detalles de la capa de Aplicación](src/main/java/odoonto/application/README.md)

### Capa de Infraestructura

Proporciona el soporte técnico para el sistema:

- **Persistencia**: Implementaciones concretas de repositorios (Firestore)
- **Configuración**: Configuración de Spring, seguridad, etc.
- **Herramientas**: Utilidades para la generación de diagramas y documentación

![Capa de Infraestructura](src/main/java/odoonto/documentation/plantuml/infrastructure_layer.png)

[Ver detalles de la capa de Infraestructura](src/main/java/odoonto/infrastructure/README.md)

### Capa de Presentación

Gestiona la interacción con el usuario o sistemas externos:

- **Controladores REST**: Exponen la funcionalidad del sistema mediante API REST
- **Documentación API**: Incluye configuración de Swagger para probar la API

![Capa de Presentación](src/main/java/odoonto/documentation/plantuml/presentation_layer.png)

[Ver detalles de la capa de Presentación](src/main/java/odoonto/presentation/README.md)

## Endpoints de la API REST

La API REST proporciona los siguientes endpoints principales:

### Pacientes
- `GET /api/patients` - Obtener todos los pacientes
- `GET /api/patients/{id}` - Obtener paciente por ID
- `POST /api/patients` - Crear nuevo paciente
- `PUT /api/patients/{id}` - Actualizar paciente
- `DELETE /api/patients/{id}` - Eliminar paciente

### Odontogramas
- `GET /api/patients/{patientId}/odontogram` - Obtener odontograma de un paciente
- `POST /api/patients/{patientId}/odontogram/lesions` - Añadir lesión al odontograma
- `POST /api/patients/{patientId}/odontogram/treatments` - Añadir tratamiento al odontograma

### Historial Médico
- `GET /api/patients/{patientId}/medical-record` - Obtener historial médico
- `POST /api/patients/{patientId}/medical-record/entries` - Añadir entrada al historial

### Doctores
- `GET /api/doctors` - Obtener todos los doctores
- `GET /api/doctors/{id}` - Obtener doctor por ID
- `POST /api/doctors` - Crear nuevo doctor

### Citas
- `GET /api/appointments` - Obtener todas las citas
- `GET /api/appointments/{id}` - Obtener cita por ID
- `POST /api/appointments` - Crear nueva cita
- `PUT /api/appointments/{id}/status` - Actualizar estado de cita

## Tecnologías Utilizadas

- **Java 21**: Versión moderna del lenguaje
- **Spring Boot 3.4.5**: Framework para el desarrollo de aplicaciones
- **Spring WebFlux**: API reactiva para operaciones asíncronas
- **Firestore**: Base de datos NoSQL de Google Cloud para almacenamiento
- **Firebase Admin**: Gestión de autenticación y acceso
- **PlantUML**: Generación de diagramas de la arquitectura DDD
- **Swagger/OpenAPI**: Documentación interactiva de la API REST
- **Lombok**: Reducción de código repetitivo

## Configuración del Entorno de Desarrollo

### Requisitos Previos
- Java 21 JDK
- Maven 3.8+
- IDE compatible con Spring Boot (IntelliJ IDEA recomendado)
- Cuenta de Google Cloud con Firestore habilitado

### Pasos para Configurar el Proyecto
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/Odoonto.Back.git
   cd Odoonto.Back
   ```

2. Configurar credenciales de Firebase:
   - Crear un proyecto en Firebase Console
   - Descargar el archivo de credenciales `firebase-service-account.json`
   - Colocarlo en `src/main/resources/`

3. Compilar el proyecto:
   ```bash
   mvn clean install
   ```

4. Ejecutar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

5. Acceder a la documentación de la API:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## Generación de Diagramas DDD

El proyecto incluye herramientas para generar diagramas PlantUML que documentan la arquitectura DDD:

1. Ejecutar el script de generación de diagramas:
   ```bash
   ./generate-diagrams.bat
   ```

2. Los diagramas generados se guardan en:
   ```
   src/main/java/odoonto/documentation/plantuml/
   ``` 