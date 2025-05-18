package odoonto.documentation.plantuml.tools;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.StandardCopyOption;

/**
 * Generador de diagramas PlantUML para arquitectura DDD
 * Analiza el código fuente del proyecto y genera diagramas de clases
 * específicos para cada módulo de la arquitectura DDD
 */
public class DDDDiagramGenerator {

    private static final String SOURCE_DIR = "src/main/java";
    private static final String OUTPUT_DIR = "src/main/java/odoonto/documentation/plantuml";
    
    // Mapeo de capas DDD a colores para el diagrama
    private static final Map<String, String> LAYER_COLORS = Map.of(
        "domain", "#e1f5fe",
        "application", "#e0f7fa",
        "infrastructure", "#f1f8e9",
        "presentation", "#fff8e1"
    );
    
    // Prefijos de paquetes para cada capa DDD
    private static final Map<String, String> LAYER_PACKAGES = Map.of(
        "domain", "odoonto.domain",
        "application", "odoonto.application",
        "infrastructure", "odoonto.infrastructure",
        "presentation", "odoonto.presentation"
    );
    
    // Variable para almacenar la entidad filtrada actual
    private String entityFilter = "Patient";

    // Configuración del parser de Java
    private final JavaParser javaParser;
    
    // Almacenamiento para las clases encontradas organizadas por capa
    private final Map<String, List<ClassInfo>> classesByLayer = new HashMap<>();
    
    // Relaciones entre clases
    private final List<Relationship> relationships = new ArrayList<>();
    
    // Lista para almacenar todas las entidades de dominio detectadas
    private static Set<String> detectedEntities = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Iniciando generador de diagramas DDD...");
        
        try {
            // Verificar y crear directorio de salida
            Path outputPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            // Iniciar generador
            DDDDiagramGenerator generator = new DDDDiagramGenerator();
            
            // Analizar código y generar diagramas
            generator.analyzeCode();
            generator.generatePatientDiagram();
            
            // Corregir todos los archivos PUML existentes
            System.out.println("Corrigiendo archivos PUML existentes...");
            fixAllPumlFiles();
            
            System.out.println("Diagrama generado exitosamente en: " + OUTPUT_DIR);
        } catch (Exception e) {
            System.err.println("Error al generar diagrama: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método estático para generar diagramas para una entidad específica
     */
    public static void generateDiagramForEntity(String entityName) {
        System.out.println("Iniciando generador de diagramas DDD para entidad: " + entityName);
        
        try {
            // Verificar y crear directorio de salida
            Path outputPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            // Verificar si el directorio target/diagrams existe
            Path targetDiagramsPath = Paths.get("target/diagrams");
            if (!Files.exists(targetDiagramsPath)) {
                try {
                    Files.createDirectories(targetDiagramsPath);
                    System.out.println("Creado directorio target/diagrams para los diagramas generados");
                } catch (Exception e) {
                    System.out.println("No se pudo crear el directorio target/diagrams: " + e.getMessage());
                    // Continuar sin crear el directorio
                }
            }
            
            // Eliminar archivos anteriores para esta entidad
            String entityDirName = entityName.toLowerCase();
            deleteExistingDiagrams(entityDirName);
            
            // Iniciar generador con la entidad específica
            DDDDiagramGenerator generator = new DDDDiagramGenerator(entityName);
            
            // Analizar código y generar diagramas
            generator.analyzeCode();
            generator.generateEntityDiagram();
            
            // Corregir todos los archivos PUML existentes
            System.out.println("Corrigiendo archivos PUML existentes...");
            fixAllPumlFiles();
            
            // Copiar el archivo PUML al directorio target/diagrams para facilitar el acceso
            try {
                Path sourcePath = Paths.get(OUTPUT_DIR, entityDirName, entityDirName + "_ddd_diagram.puml");
                Path targetPath = Paths.get("target/diagrams", entityDirName + "_ddd_diagram.puml");
                
                if (Files.exists(sourcePath)) {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Copiado diagrama PUML a: target/diagrams/" + entityDirName + "_ddd_diagram.puml");
                }
            } catch (Exception e) {
                System.out.println("No se pudo copiar el archivo al directorio target: " + e.getMessage());
                // Continuar sin copiar el archivo
            }
            
            System.out.println("Diagrama generado exitosamente en: " + Paths.get(OUTPUT_DIR, entityDirName).toString());
            System.out.println("NOTA: Para visualizar el diagrama, puedes usar una herramienta online como https://www.plantuml.com/plantuml/");
        } catch (Exception e) {
            System.err.println("Error al generar diagrama: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método para generar diagramas para todas las entidades detectadas
     */
    public static void generateDiagramsForAllEntities() {
        System.out.println("Detectando entidades en el proyecto...");
        
        // Detectar entidades en el proyecto
        Set<String> entities = detectAllEntities();
        
        if (entities.isEmpty()) {
            System.out.println("No se detectaron entidades en el proyecto.");
            return;
        }
        
        System.out.println("Se detectaron " + entities.size() + " entidades: " + String.join(", ", entities));
        System.out.println("Generando diagramas para todas las entidades...");
        
        // Generar diagrama para cada entidad detectada
        for (String entity : entities) {
            generateDiagramForEntity(entity);
        }
        
        System.out.println("Todos los diagramas fueron generados exitosamente.");
    }
    
    /**
     * Método utilitario para corregir archivos PUML existentes sin generar nuevos diagramas
     */
    public static void fixExistingPumlFiles() {
        System.out.println("Corrigiendo archivos PUML existentes...");
        try {
            Path outputPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                System.out.println("No existe el directorio de documentación: " + OUTPUT_DIR);
                return;
            }
            
            fixAllPumlFiles();
            System.out.println("Archivos PUML corregidos exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al corregir archivos PUML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Analiza el código fuente y recopila información sobre clases y relaciones
     */
    public void analyzeCode() throws IOException {
        System.out.println("Analizando código fuente...");
        
        // Recorrer estructura de directorios de código fuente
        for (String layer : LAYER_PACKAGES.keySet()) {
            String packagePath = LAYER_PACKAGES.get(layer).replace('.', '/');
            Path dirPath = Paths.get(SOURCE_DIR, packagePath);
            
            if (Files.exists(dirPath)) {
                // Analizar capas de forma recursiva
                processDirectory(dirPath.toFile(), layer);
                
                // Añadir búsqueda adicional para detectar más clases relacionadas
                if (layer.equals("domain")) {
                    // Buscar archivos específicos del tipo actual en toda la capa de dominio
                    searchRecursively(dirPath.toFile(), layer);
                }
            }
        }
        
        // Buscar clases relacionadas que podrían estar en paquetes más específicos
        String entityLowerCase = entityFilter.toLowerCase();
        for (String layer : LAYER_PACKAGES.keySet()) {
            String packagePath = LAYER_PACKAGES.get(layer).replace('.', '/');
            Path dirPath = Paths.get(SOURCE_DIR, packagePath, entityLowerCase);
            
            if (Files.exists(dirPath)) {
                processDirectory(dirPath.toFile(), layer);
            }
        }
        
        // Detectar relaciones entre clases
        detectRelationships();
    }
    
    /**
     * Busca de forma más exhaustiva en todo el directorio
     */
    private void searchRecursively(File dir, String layer) {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                searchRecursively(file, layer);
            } else if (file.getName().endsWith(".java")) {
                try {
                    // Leer el contenido del archivo para buscar referencias a la entidad
                    String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    if (content.contains(entityFilter)) {
                        processJavaFile(file, layer);
                    }
                } catch (Exception e) {
                    System.err.println("Error al leer archivo para búsqueda adicional: " + file.getPath() + " - " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Detecta todas las entidades en el proyecto analizando el código fuente
     * @return Conjunto de nombres de entidades detectadas
     */
    public static Set<String> detectAllEntities() {
        // Si ya tenemos entidades detectadas, devolverlas
        if (!detectedEntities.isEmpty()) {
            return detectedEntities;
        }
        
        Set<String> entities = new HashSet<>();
        
        try {
            // Buscar en el paquete de dominio
            String domainPackagePath = LAYER_PACKAGES.get("domain").replace('.', '/');
            Path domainDir = Paths.get(SOURCE_DIR, domainPackagePath);
            Path modelDir = Paths.get(domainDir.toString(), "model");
            Path aggregatesDir = Paths.get(modelDir.toString(), "aggregates");
            
            // Verificar si existe el directorio de agregados
            if (Files.exists(aggregatesDir) && Files.isDirectory(aggregatesDir)) {
                try (Stream<Path> paths = Files.list(aggregatesDir)) {
                    paths.filter(Files::isRegularFile)
                         .filter(path -> path.toString().endsWith(".java"))
                         .forEach(path -> {
                             String fileName = path.getFileName().toString();
                             // Extraer nombre de entidad (eliminar .java)
                             String entityName = fileName.substring(0, fileName.length() - 5);
                             entities.add(entityName);
                         });
                }
            } else {
                // Si no hay directorio de agregados, buscar en todo el paquete de dominio
                searchEntitiesInDirectory(domainDir.toFile(), entities);
                
                // También buscar en otros paquetes importantes que puedan contener entidades
                Path entityDir = Paths.get(SOURCE_DIR, domainPackagePath, "entity");
                if (Files.exists(entityDir)) {
                    searchEntitiesInDirectory(entityDir.toFile(), entities);
                }
                
                Path valueObjectDir = Paths.get(SOURCE_DIR, domainPackagePath, "vo");
                if (Files.exists(valueObjectDir)) {
                    searchEntitiesInDirectory(valueObjectDir.toFile(), entities);
                }
                
                // También buscar en la capa de aplicación por nombres comunes de entidades
                String appPackagePath = LAYER_PACKAGES.get("application").replace('.', '/');
                Path appDir = Paths.get(SOURCE_DIR, appPackagePath);
                searchEntitiesInApplicationLayer(appDir.toFile(), entities);
            }
            
            // Si no se encontraron entidades, añadir algunas predeterminadas
            if (entities.isEmpty()) {
                entities.add("Patient");
                entities.add("Doctor");
                entities.add("Appointment");
                entities.add("Odontogram");
            }
        } catch (IOException e) {
            System.err.println("Error al detectar entidades: " + e.getMessage());
        }
        
        // Guardar entidades detectadas
        detectedEntities = entities;
        return entities;
    }
    
    /**
     * Busca entidades a partir de patrones comunes en la capa de aplicación
     */
    private static void searchEntitiesInApplicationLayer(File dir, Set<String> entities) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                searchEntitiesInApplicationLayer(file, entities);
            } else if (file.getName().endsWith("UseCase.java")) {
                String fileName = file.getName();
                // Para extraer el nombre de la entidad de casos de uso como PatientCreateUseCase.java
                for (String prefix : new String[] {"Create", "Get", "Update", "Delete", "Find"}) {
                    if (fileName.contains(prefix)) {
                        int endIndex = fileName.indexOf(prefix);
                        if (endIndex > 0) {
                            String entityName = fileName.substring(0, endIndex);
                            if (entityName.length() > 1 && Character.isUpperCase(entityName.charAt(0))) {
                                entities.add(entityName);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Busca entidades recursivamente en un directorio
     */
    private static void searchEntitiesInDirectory(File dir, Set<String> entities) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                searchEntitiesInDirectory(file, entities);
            } else if (file.getName().endsWith(".java")) {
                // Heurística simple: archivos que parecen ser entidades agregadas
                String fileName = file.getName();
                String entityName = fileName.substring(0, fileName.length() - 5); // Eliminar .java
                
                // Detectar archivos que parecen ser agregados o entidades principales
                if (entityName.matches("^[A-Z][a-zA-Z0-9]*$") &&        // Nombre en CamelCase
                    !entityName.endsWith("DTO") &&                     // No es un DTO
                    !entityName.endsWith("Service") &&                 // No es un servicio
                    !entityName.endsWith("Repository") &&              // No es un repositorio
                    !entityName.endsWith("Controller") &&              // No es un controlador
                    !entityName.endsWith("Config") &&                  // No es una configuración
                    !entityName.endsWith("Exception") &&               // No es una excepción
                    !entityName.endsWith("Factory") &&                 // No es una fábrica
                    !entityName.endsWith("Adapter") &&                 // No es un adaptador
                    !entityName.endsWith("Mapper") &&                  // No es un mapeador
                    !entityName.contains("Abstract") &&                // No es una clase abstracta
                    !entityName.contains("Base")) {                    // No es una clase base
                    
                    try {
                        // Leer el archivo para verificar si tiene características de entidad
                        if (isLikelyEntity(file)) {
                            entities.add(entityName);
                        }
                    } catch (IOException e) {
                        System.err.println("Error al leer archivo: " + file.getPath());
                    }
                }
            }
        }
    }
    
    /**
     * Verifica si un archivo parece ser una entidad analizando su contenido
     */
    private static boolean isLikelyEntity(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        
        // Buscar patrones comunes en entidades de dominio
        boolean hasEntityCharacteristics = 
            // Tiene Id o identificador
            content.contains("Id ") || content.contains("ID ") || 
            content.contains("Id;") || content.contains("Identifier") ||
            // Tiene anotaciones JPA o de persistencia
            content.contains("@Entity") || content.contains("@Table") || 
            content.contains("@Id") || content.contains("@Column") ||
            // Podría ser un agregado
            content.contains("Aggregate") || content.contains("Root") ||
            // Contiene colecciones de otras entidades
            (content.contains("List<") && content.contains("private")) ||
            (content.contains("Set<") && content.contains("private"));
        
        return hasEntityCharacteristics;
    }
    
    /**
     * Elimina los diagramas existentes para una entidad específica
     */
    private static void deleteExistingDiagrams(String entityName) throws IOException {
        // Comprobar el directorio principal
        Path dir = Paths.get(OUTPUT_DIR);
        if (!Files.exists(dir)) {
            return;
        }
        
        // Comprobar el directorio específico de la entidad
        Path entityDir = Paths.get(OUTPUT_DIR, entityName);
        if (Files.exists(entityDir) && Files.isDirectory(entityDir)) {
            // Buscar archivos en el directorio de la entidad
            try (Stream<Path> paths = Files.list(entityDir)) {
                paths.filter(path -> {
                    String fileName = path.getFileName().toString();
                    return fileName.endsWith(".puml") || fileName.endsWith(".png") || 
                           fileName.endsWith(".svg") || fileName.endsWith(".pdf");
                }).forEach(path -> {
                    try {
                        Files.delete(path);
                        System.out.println("Eliminado diagrama anterior: " + path.getFileName());
                    } catch (IOException e) {
                        System.err.println("No se pudo eliminar el archivo: " + path);
                    }
                });
            }
        } else {
            // Si no existe la carpeta de la entidad, crearla
            try {
                Files.createDirectories(entityDir);
                System.out.println("Creado directorio para entidad: " + entityDir);
            } catch (IOException e) {
                System.err.println("Error al crear directorio para entidad: " + entityName + " - " + e.getMessage());
            }
        }
        
        // También buscar archivos antiguos en el directorio raíz que pudieran haberse generado anteriormente
        try (Stream<Path> paths = Files.list(dir)) {
            paths.filter(path -> {
                String fileName = path.getFileName().toString();
                return fileName.startsWith(entityName + "_ddd_diagram") && 
                       (fileName.endsWith(".puml") || fileName.endsWith(".png") || 
                        fileName.endsWith(".svg") || fileName.endsWith(".pdf"));
            }).forEach(path -> {
                try {
                    Files.delete(path);
                    System.out.println("Eliminado diagrama anterior del directorio raíz: " + path.getFileName());
                } catch (IOException e) {
                    System.err.println("No se pudo eliminar el archivo: " + path);
                }
            });
        }
    }

    public DDDDiagramGenerator() {
        // Configurar el parser de Java con resolución de símbolos
        ParserConfiguration config = new ParserConfiguration();
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        typeSolver.add(new JavaParserTypeSolver(new File(SOURCE_DIR)));
        
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        config.setSymbolResolver(symbolSolver);
        
        this.javaParser = new JavaParser(config);
        
        // Inicializar listas para cada capa
        for (String layer : LAYER_PACKAGES.keySet()) {
            classesByLayer.put(layer, new ArrayList<>());
        }
    }
    
    // Constructor que recibe la entidad a filtrar
    public DDDDiagramGenerator(String entityFilter) {
        this(); // Llamar al constructor por defecto
        this.entityFilter = entityFilter;
    }

    /**
     * Procesa recursivamente un directorio en busca de archivos Java
     */
    private void processDirectory(File dir, String layer) {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(file, layer);
            } else if (file.getName().endsWith(".java")) {
                try {
                    processJavaFile(file, layer);
                } catch (Exception e) {
                    System.err.println("Error al procesar archivo " + file.getPath() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Procesa un archivo Java para extraer información de clases
     */
    private void processJavaFile(File file, String layer) throws IOException {
        // Parsear archivo Java
        CompilationUnit cu = javaParser.parse(file).getResult().orElse(null);
        if (cu == null) return;
        
        // Extraer declaraciones de clases e interfaces
        for (ClassOrInterfaceDeclaration classDecl : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            // Verificar si la clase está relacionada con la entidad filtrada
            if (isEntityRelated(classDecl.getNameAsString())) {
                ClassInfo classInfo = extractClassInfo(classDecl, cu.getPackageDeclaration().get().getNameAsString());
                classesByLayer.get(layer).add(classInfo);
            }
        }
    }

    /**
     * Comprueba si una clase está relacionada con las entidades filtradas
     */
    private boolean isEntityRelated(String className) {
        // Hacer la detección más flexible para encontrar más clases relacionadas
        return className.contains(entityFilter) ||
               className.toLowerCase().contains(entityFilter.toLowerCase()) ||
               // Buscar clases que suelen estar relacionadas con entidades
               (entityFilter.equalsIgnoreCase("Patient") && className.contains("Medical")) ||
               (entityFilter.equalsIgnoreCase("Doctor") && className.contains("Dentist")) ||
               (entityFilter.equalsIgnoreCase("Appointment") && (className.contains("Schedule") || className.contains("Calendar"))) ||
               (entityFilter.equalsIgnoreCase("Odontogram") && className.contains("Dental"));
    }

    /**
     * Extrae información de una declaración de clase
     */
    private ClassInfo extractClassInfo(ClassOrInterfaceDeclaration classDecl, String packageName) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.name = classDecl.getNameAsString();
        classInfo.packageName = packageName;
        classInfo.isInterface = classDecl.isInterface();
        
        // Extraer campos
        for (FieldDeclaration field : classDecl.getFields()) {
            field.getVariables().forEach(var -> {
                String type = var.getType().asString();
                String name = var.getNameAsString();
                String visibility = getVisibility(field);
                
                classInfo.fields.add(new FieldInfo(name, type, visibility));
            });
        }
        
        // Extraer métodos
        for (MethodDeclaration method : classDecl.getMethods()) {
            String returnType = method.getType().asString();
            String name = method.getNameAsString();
            String visibility = getVisibility(method);
            
            List<String> parameters = method.getParameters().stream()
                    .map(p -> p.getType().asString() + " " + p.getNameAsString())
                    .collect(Collectors.toList());
            
            classInfo.methods.add(new MethodInfo(name, returnType, parameters, visibility));
        }
        
        // Extraer relaciones de herencia/implementación
        classDecl.getExtendedTypes().forEach(type -> {
            classInfo.extends_.add(type.getNameAsString());
        });
        
        classDecl.getImplementedTypes().forEach(type -> {
            classInfo.implements_.add(type.getNameAsString());
        });
        
        return classInfo;
    }

    /**
     * Obtiene la visibilidad de un miembro
     */
    private String getVisibility(FieldDeclaration field) {
        if (field.isPublic()) return "+";
        if (field.isPrivate()) return "-";
        if (field.isProtected()) return "#";
        return "~"; // default/package
    }
    
    /**
     * Obtiene la visibilidad de un método
     */
    private String getVisibility(MethodDeclaration method) {
        if (method.isPublic()) return "+";
        if (method.isPrivate()) return "-";
        if (method.isProtected()) return "#";
        return "~"; // default/package
    }

    /**
     * Detecta relaciones entre clases
     */
    private void detectRelationships() {
        // Para cada capa
        for (String layer : LAYER_PACKAGES.keySet()) {
            List<ClassInfo> classes = classesByLayer.get(layer);
            
            // Para cada clase en la capa
            for (ClassInfo classInfo : classes) {
                // Detectar relaciones en campos
                for (FieldInfo field : classInfo.fields) {
                    // Buscar clases que coincidan con el tipo del campo
                    findRelatedClass(field.type).ifPresent(targetClass -> {
                        relationships.add(new Relationship(
                            classInfo.name,
                            targetClass.name,
                            "-->",
                            "has"
                        ));
                    });
                }
                
                // Detectar relaciones de herencia/implementación
                for (String extendedType : classInfo.extends_) {
                    findClassByName(extendedType).ifPresent(targetClass -> {
                        relationships.add(new Relationship(
                            classInfo.name,
                            targetClass.name,
                            "--|>",
                            "extends"
                        ));
                    });
                }
                
                for (String implementedType : classInfo.implements_) {
                    findClassByName(implementedType).ifPresent(targetClass -> {
                        relationships.add(new Relationship(
                            classInfo.name,
                            targetClass.name,
                            "..|>",
                            "implements"
                        ));
                    });
                }
            }
        }
    }

    /**
     * Busca una clase relacionada por tipo
     */
    private Optional<ClassInfo> findRelatedClass(String type) {
        // Eliminar genéricos para la búsqueda
        String baseType = type.contains("<") ? type.substring(0, type.indexOf("<")) : type;
        
        // Buscar en todas las capas
        for (String layer : LAYER_PACKAGES.keySet()) {
            for (ClassInfo classInfo : classesByLayer.get(layer)) {
                if (baseType.equals(classInfo.name) || baseType.endsWith("." + classInfo.name)) {
                    return Optional.of(classInfo);
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Busca una clase por nombre exacto
     */
    private Optional<ClassInfo> findClassByName(String name) {
        // Buscar en todas las capas
        for (String layer : LAYER_PACKAGES.keySet()) {
            for (ClassInfo classInfo : classesByLayer.get(layer)) {
                if (name.equals(classInfo.name) || name.endsWith("." + classInfo.name)) {
                    return Optional.of(classInfo);
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Genera el diagrama PlantUML específico para la entidad
     */
    public void generateEntityDiagram() throws IOException {
        System.out.println("Generando diagrama de " + entityFilter + "...");
        
        StringBuilder plantUmlCode = new StringBuilder();
        
        // Encabezado PlantUML con sintaxis ultra básica
        plantUmlCode.append("@startuml\n\n");
        
        // Título sencillo
        plantUmlCode.append("title Arquitectura DDD: ").append(entityFilter).append("\n\n");
        
        // Configuraciones muy básicas
        plantUmlCode.append("skinparam backgroundColor white\n");
        plantUmlCode.append("skinparam defaultFontName Arial\n");
        plantUmlCode.append("skinparam defaultFontSize 12\n");
        
        // Ancho máximo 
        plantUmlCode.append("scale max 1024 width\n");
        
        // Dirección explícita
        plantUmlCode.append("left to right direction\n\n");
        
        // Ordenar las capas como en la arquitectura de referencia
        String[] layerOrder = {"presentation", "application", "domain", "infrastructure"};
        
        // Colores básicos usando nombres predefinidos
        Map<String, String> layerColors = Map.of(
            "domain", "lightblue",
            "application", "lightgreen",
            "infrastructure", "lightyellow",
            "presentation", "lightgray"
        );
        
        // Generar definiciones de clases por capa en el orden especificado
        for (String layer : layerOrder) {
            String layerName = layer.substring(0, 1).toUpperCase() + layer.substring(1);
            
            // Solo incluir la capa si tiene clases
            if (!classesByLayer.get(layer).isEmpty()) {
                String colorCode = layerColors.get(layer);
                
                // Definición de paquete ultra simplificada
                plantUmlCode.append("package \"").append(layerName).append("\" as ")
                          .append(layer.toLowerCase()).append(" #").append(colorCode).append(" {\n");
                
                // Organizar las clases de forma simple
                List<ClassInfo> classes = classesByLayer.get(layer);
                
                // Ordenar clases por relevancia
                List<ClassInfo> sortedClasses = new ArrayList<>(classes);
                sortedClasses.sort((c1, c2) -> {
                    boolean c1HasEntity = c1.name.contains(entityFilter);
                    boolean c2HasEntity = c2.name.contains(entityFilter);
                    
                    if (c1HasEntity && !c2HasEntity) return -1;
                    if (!c1HasEntity && c2HasEntity) return 1;
                    
                    return 0;
                });
                
                // Generar clases de forma simple, sin grid
                for (ClassInfo classInfo : sortedClasses) {
                    generateSimpleClassUml(plantUmlCode, classInfo);
                }
                
                plantUmlCode.append("}\n\n");
            }
        }
        
        // Relaciones entre capas (dependencias arquitectónicas)
        plantUmlCode.append("' Dependencias entre capas de arquitectura\n");
        plantUmlCode.append("presentation --> application\n");
        plantUmlCode.append("application --> domain\n");
        plantUmlCode.append("infrastructure --> domain\n");
        plantUmlCode.append("infrastructure --> application\n\n");
        
        // Generar relaciones entre clases más selectivas
        plantUmlCode.append("' Relaciones entre clases principales\n");
        List<Relationship> filteredRelationships = filterImportantRelationships();
        for (Relationship rel : filteredRelationships) {
            plantUmlCode.append(rel.source).append(" ").append(rel.type).append(" ")
                    .append(rel.target).append("\n");
        }
        
        // Leyenda simple
        plantUmlCode.append("legend right\n");
        plantUmlCode.append("  Arquitectura Domain-Driven Design\n");
        plantUmlCode.append("  Entidad: ").append(entityFilter).append("\n");
        plantUmlCode.append("endlegend\n");
        
        // Fin del diagrama
        plantUmlCode.append("\n@enduml");
        
        // Generar archivo de salida
        String baseFileName = entityFilter.toLowerCase() + "_ddd_diagram";
        generateDiagramFiles(plantUmlCode.toString(), baseFileName);
    }
    
    /**
     * Genera una representación simplificada de una clase
     */
    private void generateSimpleClassUml(StringBuilder sb, ClassInfo classInfo) {
        // Determinar si es clase o interfaz
        String classType = classInfo.isInterface ? "interface" : "class";
        
        // Generar definición de clase básica
        sb.append("  ").append(classType).append(" ").append(classInfo.name);
        
        // Si la clase tiene campos o métodos, mostrar algunos
        if (!classInfo.fields.isEmpty() || !classInfo.methods.isEmpty()) {
            sb.append(" {\n");
            
            // Mostrar hasta 2 campos importantes
            int fieldCount = Math.min(classInfo.fields.size(), 2);
            for (int i = 0; i < fieldCount; i++) {
                FieldInfo field = classInfo.fields.get(i);
                sb.append("    ").append(field.visibility).append(" ")
                  .append(field.name).append(": ").append(simplifyType(field.type)).append("\n");
            }
            
            // Indicador si hay más campos
            if (classInfo.fields.size() > fieldCount) {
                sb.append("    .. más campos ..\n");
            }
            
            // Separador si hay campos y métodos
            if (!classInfo.fields.isEmpty() && !classInfo.methods.isEmpty()) {
                sb.append("\n");
            }
            
            // Mostrar hasta 2 métodos importantes
            int methodCount = Math.min(classInfo.methods.size(), 2);
            for (int i = 0; i < methodCount; i++) {
                MethodInfo method = classInfo.methods.get(i);
                sb.append("    ").append(method.visibility).append(" ")
                  .append(method.name).append("()\n");
            }
            
            // Indicador si hay más métodos
            if (classInfo.methods.size() > methodCount) {
                sb.append("    .. más métodos ..\n");
            }
            
            sb.append("  }\n");
        } else {
            sb.append("\n");
        }
    }
    
    /**
     * Filtra las relaciones para mostrar solo las más importantes
     */
    private List<Relationship> filterImportantRelationships() {
        // Puntuación de importancia para cada relación
        Map<Relationship, Integer> relationshipScores = new HashMap<>();
        
        for (Relationship rel : relationships) {
            int score = 0;
            
            // Prioridad máxima: relaciones directas con la entidad principal
            if (rel.source.equals(entityFilter) || rel.target.equals(entityFilter)) {
                score += 20;
            } else if (rel.source.contains(entityFilter) || rel.target.contains(entityFilter)) {
                score += 10;
            }
            
            // Prioridad alta: herencia e implementación
            if (rel.type.equals("--|>")) score += 8;  // Herencia
            if (rel.type.equals("..|>")) score += 7;  // Implementación
            
            // Prioridad media: agregación y composición
            if (rel.type.equals("o--")) score += 5;   // Agregación
            if (rel.type.equals("*--")) score += 6;   // Composición
            
            // Prioridad baja: asociaciones simples
            if (rel.type.equals("-->")) score += 3;
            
            // Bonus por relaciones entre diferentes capas (arquitectónicamente significativas)
            String sourceLayer = determineLayer(rel.source);
            String targetLayer = determineLayer(rel.target);
            if (sourceLayer != null && targetLayer != null && !sourceLayer.equals(targetLayer)) {
                score += 5;
            }
            
            relationshipScores.put(rel, score);
        }
        
        // Ordenar por puntuación y limitar a 15 relaciones para evitar sobrecarga visual
        return relationshipScores.entrySet().stream()
                .sorted(Map.Entry.<Relationship, Integer>comparingByValue().reversed())
                .limit(15)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Determina a qué capa pertenece una clase
     */
    private String determineLayer(String className) {
        for (String layer : classesByLayer.keySet()) {
            for (ClassInfo classInfo : classesByLayer.get(layer)) {
                if (classInfo.name.equals(className)) {
                    return layer;
                }
            }
        }
        return null;
    }

    /**
     * Simplifica los nombres de los tipos para una mejor visualización en el diagrama
     */
    private String simplifyType(String type) {
        // Eliminar nombres de paquetes
        String simplified = type.replaceAll(".*\\.", "");
        
        // Simplificar tipos genéricos
        if (simplified.contains("<")) {
            String mainType = simplified.substring(0, simplified.indexOf("<"));
            String paramType = simplified.substring(simplified.indexOf("<") + 1, simplified.indexOf(">"));
            
            // Simplificar tipo de parámetro
            paramType = paramType.replaceAll(".*\\.", "");
            
            // Si hay múltiples parámetros, simplificar a "..."
            if (paramType.contains(",")) {
                paramType = "...";
            }
            
            simplified = mainType + "<" + paramType + ">";
        }
        
        // Acortar tipos comunes
        simplified = simplified
            .replace("String", "str")
            .replace("Integer", "int")
            .replace("Boolean", "bool")
            .replace("Character", "char")
            .replace("Collection", "Coll")
            .replace("Optional", "Opt");
        
        return simplified;
    }

    /**
     * Método para mantener compatibilidad con implementación anterior
     */
    public void generatePatientDiagram() throws IOException {
        generateEntityDiagram();
    }

    /**
     * Corrige todos los archivos PUML existentes
     */
    public static void fixAllPumlFiles() {
        try {
            Path dir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(dir)) {
                System.err.println("Directorio no encontrado: " + OUTPUT_DIR);
                return;
            }
            
            // Primero buscar en el directorio raíz
            processPumlFiles(dir);
            
            // Luego buscar en cada subdirectorio de entidad
            try (Stream<Path> paths = Files.list(dir)) {
                paths.filter(Files::isDirectory)
                     .forEach(entityDir -> {
                         try {
                             System.out.println("Verificando subcarpeta de entidad: " + entityDir.getFileName());
                             processPumlFiles(entityDir);
                         } catch (IOException e) {
                             System.err.println("Error al procesar subcarpeta: " + entityDir.getFileName() + " - " + e.getMessage());
                         }
                     });
            }
            
        } catch (IOException e) {
            System.err.println("Error al corregir archivos PUML: " + e.getMessage());
        }
    }
    
    /**
     * Procesa archivos PUML en un directorio dado
     */
    private static void processPumlFiles(Path dir) throws IOException {
        try (Stream<Path> paths = Files.list(dir)) {
            paths.filter(path -> path.toString().endsWith(".puml"))
                 .forEach(path -> {
                     try {
                         System.out.println("Verificando archivo: " + path.getFileName());
                         
                         // Leer el contenido
                         String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                         
                         // Verificar si contiene ##
                         if (content.contains("package \"") && content.contains(" ##")) {
                             System.out.println("Corrigiendo archivo: " + path.getFileName());
                             
                             // Reemplazar los ## por # en las definiciones de paquetes
                             content = content.replaceAll("package \"([^\"]*)\" as ([a-z]+) ##([a-f0-9]{6})", 
                                                         "package \"$1\" as $2 #$3");
                             
                             // Guardar el archivo corregido
                             Files.write(path, content.getBytes(StandardCharsets.UTF_8));
                             System.out.println("Archivo PUML corregido: " + path.getFileName());
                         }
                     } catch (IOException e) {
                         System.err.println("Error al procesar archivo: " + path.getFileName() + " - " + e.getMessage());
                     }
                 });
        }
    }

    /**
     * Genera los archivos de diagrama (PUML y PNG)
     */
    private void generateDiagramFiles(String plantUmlCode, String baseName) throws IOException {
        // Crear directorio para la entidad si no existe
        String entityDirName = entityFilter.toLowerCase();
        Path entityDir = Paths.get(OUTPUT_DIR, entityDirName);
        
        if (!Files.exists(entityDir)) {
            Files.createDirectories(entityDir);
            System.out.println("Creado directorio para entidad: " + entityDir);
        }
        
        // Guardar código PlantUML en la subcarpeta de la entidad
        Path plantUmlFile = Paths.get(entityDir.toString(), baseName + ".puml");
        Files.write(plantUmlFile, plantUmlCode.getBytes(StandardCharsets.UTF_8));
        
        // Generar imagen del diagrama en la subcarpeta de la entidad
        generateImageFromPlantUml(plantUmlCode, Paths.get(entityDir.toString(), baseName + ".png").toString());
        
        System.out.println("Archivo PlantUML generado en: " + plantUmlFile.toAbsolutePath());
        System.out.println("Imagen del diagrama generada en: " + Paths.get(entityDir.toString(), baseName + ".png").toAbsolutePath());
    }
    
    /**
     * Genera una imagen a partir del código PlantUML
     */
    private void generateImageFromPlantUml(String plantUmlCode, String outputFilePath) throws IOException {
        SourceStringReader reader = new SourceStringReader(plantUmlCode);
        
        try (FileOutputStream output = new FileOutputStream(outputFilePath)) {
            // Generar el diagrama en el archivo de salida
            reader.outputImage(output);
        }
        
        System.out.println("Imagen generada: " + outputFilePath);
    }

    // Clases internas para almacenar la información extraída
    
    private static class ClassInfo {
        String name;
        // El paquete es relevante para la generación del diagrama incluso si no se usa directamente
        @SuppressWarnings("unused")
        String packageName;
        boolean isInterface;
        List<FieldInfo> fields = new ArrayList<>();
        List<MethodInfo> methods = new ArrayList<>();
        List<String> extends_ = new ArrayList<>();
        List<String> implements_ = new ArrayList<>();
    }
    
    private static class FieldInfo {
        String name;
        String type;
        String visibility;
        
        FieldInfo(String name, String type, String visibility) {
            this.name = name;
            this.type = type;
            this.visibility = visibility;
        }
    }
    
    private static class MethodInfo {
        String name;
        String returnType;
        List<String> parameters;
        String visibility;
        
        MethodInfo(String name, String returnType, List<String> parameters, String visibility) {
            this.name = name;
            this.returnType = returnType;
            this.parameters = parameters;
            this.visibility = visibility;
        }
    }
    
    private static class Relationship {
        String source;
        String target;
        String type;
        String label;
        
        Relationship(String source, String target, String type, String label) {
            this.source = source;
            this.target = target;
            this.type = type;
            this.label = label;
        }
    }
} 