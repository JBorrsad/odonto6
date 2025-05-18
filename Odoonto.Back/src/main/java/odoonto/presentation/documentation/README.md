# Documentación DDD - Odoonto

Esta carpeta contiene la documentación relacionada con la arquitectura DDD del proyecto Odoonto.

## Generación de diagramas

Los diagramas se pueden generar de dos formas:

### 1. Mediante el menú interactivo de inicio

Al iniciar la aplicación, se muestra un menú interactivo que permite:
- Generar diagramas de clases para diferentes entidades (Paciente, Odontograma, Doctor, etc.)
- Iniciar la aplicación normalmente

El menú es implementado por la clase `StartupMenuConfig` ubicada en el paquete `odoonto.infrastructure.config`.

### 2. Automáticamente al iniciar la aplicación

Si lo prefieres, los diagramas también se pueden generar automáticamente al iniciar la aplicación mediante la clase `DiagramGeneratorConfig` ubicada en el paquete `odoonto.infrastructure.config`. Para activar esta funcionalidad, inicia la aplicación con el parámetro `--auto-generate-diagrams`.

## Archivos generados

Para cada entidad, se generan los siguientes archivos:
- `[entidad]_ddd_diagram.puml`: Código PlantUML para el diagrama de la entidad
- `[entidad]_ddd_diagram.png`: Imagen del diagrama generada automáticamente
- `index.html`: Página web con visualización del diagrama e instrucciones

Cada vez que se genera un diagrama para una entidad, se borran los diagramas anteriores de esa misma entidad y se crean nuevos, asegurando que siempre estén actualizados con el código más reciente.

## Entidades disponibles

Puedes generar diagramas para las siguientes entidades:
- Patient (Paciente)
- Odontogram (Odontograma)
- Doctor
- Appointment (Cita)
- MedicalRecord (Historial Médico)

## Acceso a los diagramas desde la aplicación

Una vez iniciada la aplicación, puedes acceder a los diagramas a través de las siguientes URL:

- **Documentación general**: `/documentation/ddd`
- **Código PlantUML del diagrama de Paciente**: `/documentation/ddd/patient.puml`
- **Imagen del diagrama de Paciente**: `/documentation/ddd/patient_ddd_diagram.png`
- **Estado de generación**: `/documentation/ddd/status`

## Visualización de los diagramas

Las imágenes de los diagramas se generan automáticamente en formato PNG y se pueden visualizar directamente abriendo los archivos o a través de la página HTML generada.

Si deseas editar los diagramas, puedes:

1. **Modificar los archivos .puml**:
   - Editar el archivo .puml correspondiente a la entidad deseada
   - Regenerar la imagen utilizando PlantUML

2. **Usar herramientas de PlantUML**:
   - En VS Code, instala la extensión PlantUML
   - En IntelliJ IDEA, instala el plugin PlantUML Integration
   - O utiliza servicios online como [PlantText](https://www.planttext.com/) o [PlantUML Online Server](https://plantuml.com/plantuml)

## Personalización del generador

Si deseas personalizar o agregar nuevos diagramas, puedes modificar la clase `DDDDiagramGenerator` en el paquete `odoonto.infrastructure.tools`. Esta clase analiza el código fuente del proyecto y genera los diagramas con sus imágenes correspondientes. 