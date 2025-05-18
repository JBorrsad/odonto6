package odoonto.infrastructure.tools;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
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

    private static final String BASE_PACKAGE = "odoonto";
    private static final String SOURCE_DIR = "src/main/java";
    private static final String OUTPUT_DIR = "src/main/java/odoonto/presentation/documentation";
    
    // Formato de imagen a generar
    private static final String IMAGE_FORMAT = "PNG";
    
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
    
    // Entidades o palabras clave para filtrar
    private static final List<String> ENTITY_FILTERS = List.of(
        "Patient", "patient", "Pacient", "pacient", "Paciente", "paciente"
    );
    
    // Variable para almacenar la entidad filtrada actual
    private String entityFilter = "Patient";

    // Configuración del parser de Java
    private final JavaParser javaParser;
    
    // Almacenamiento para las clases encontradas organizadas por capa
    private final Map<String, List<ClassInfo>> classesByLayer = new HashMap<>();
    
    // Relaciones entre clases
    private final List<Relationship> relationships = new ArrayList<>();

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
            deleteExistingDiagrams(entityName.toLowerCase());
            
            // Iniciar generador con la entidad específica
            DDDDiagramGenerator generator = new DDDDiagramGenerator(entityName);
            
            // Analizar código y generar diagramas
            generator.analyzeCode();
            generator.generateEntityDiagram();
            
            // Copiar el archivo PUML al directorio target/diagrams para facilitar el acceso
            try {
                Path sourcePath = Paths.get(OUTPUT_DIR, entityName.toLowerCase() + "_ddd_diagram.puml");
                Path targetPath = Paths.get("target/diagrams", entityName.toLowerCase() + "_ddd_diagram.puml");
                
                if (Files.exists(sourcePath)) {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Copiado diagrama PUML a: target/diagrams/" + entityName.toLowerCase() + "_ddd_diagram.puml");
                }
            } catch (Exception e) {
                System.out.println("No se pudo copiar el archivo al directorio target: " + e.getMessage());
                // Continuar sin copiar el archivo
            }
            
            System.out.println("Diagrama generado exitosamente en: " + OUTPUT_DIR);
            System.out.println("NOTA: Para visualizar el diagrama, puedes usar una herramienta online como https://www.plantuml.com/plantuml/");
        } catch (Exception e) {
            System.err.println("Error al generar diagrama: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Elimina los diagramas existentes para una entidad específica
     */
    private static void deleteExistingDiagrams(String entityName) throws IOException {
        Path dir = Paths.get(OUTPUT_DIR);
        if (!Files.exists(dir)) {
            return;
        }
        
        // Buscar archivos que coincidan con el patrón de nombre de la entidad
        try (Stream<Path> paths = Files.list(dir)) {
            paths.filter(path -> {
                String fileName = path.getFileName().toString();
                return fileName.startsWith(entityName + "_ddd_diagram") && 
                       (fileName.endsWith(".puml") || fileName.endsWith(".png") || 
                        fileName.endsWith(".svg") || fileName.endsWith(".pdf"));
            }).forEach(path -> {
                try {
                    Files.delete(path);
                    System.out.println("Eliminado diagrama anterior: " + path.getFileName());
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
     * Analiza el código fuente y recopila información sobre clases y relaciones
     */
    public void analyzeCode() throws IOException {
        System.out.println("Analizando código fuente...");
        
        // Recorrer estructura de directorios de código fuente
        for (String layer : LAYER_PACKAGES.keySet()) {
            String packagePath = LAYER_PACKAGES.get(layer).replace('.', '/');
            Path dirPath = Paths.get(SOURCE_DIR, packagePath);
            
            if (Files.exists(dirPath)) {
                processDirectory(dirPath.toFile(), layer);
            }
        }
        
        // Detectar relaciones entre clases
        detectRelationships();
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
        return className.contains(entityFilter);
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
        
        // Encabezado PlantUML
        plantUmlCode.append("@startuml " + entityFilter + " - Diagrama DDD End-to-End\n\n");
        plantUmlCode.append("!theme plain\n");
        plantUmlCode.append("skinparam linetype ortho\n");
        plantUmlCode.append("skinparam packageStyle rectangle\n");
        plantUmlCode.append("skinparam classAttributeIconSize 0\n");
        plantUmlCode.append("skinparam shadowing false\n\n");
        
        // Generar definiciones de clases por capa
        for (String layer : LAYER_PACKAGES.keySet()) {
            String layerName = layer.substring(0, 1).toUpperCase() + layer.substring(1);
            
            // Solo incluir la capa si tiene clases
            if (!classesByLayer.get(layer).isEmpty()) {
                plantUmlCode.append("package \"").append(layerName).append("\" as ")
                        .append(layer.toLowerCase()).append(" #")
                        .append(LAYER_COLORS.get(layer)).append(" {\n");
                
                // Generar código para cada clase en la capa
                for (ClassInfo classInfo : classesByLayer.get(layer)) {
                    generateClassUml(plantUmlCode, classInfo);
                }
                
                plantUmlCode.append("}\n\n");
            }
        }
        
        // Generar relaciones
        plantUmlCode.append("' Relaciones entre clases\n");
        for (Relationship rel : relationships) {
            plantUmlCode.append(rel.source).append(" ").append(rel.type).append(" ")
                    .append(rel.target).append("\n");
        }
        
        // Pie de diagrama
        plantUmlCode.append("\n@enduml");
        
        // Generar archivo de salida
        String baseFileName = entityFilter.toLowerCase() + "_ddd_diagram";
        generateDiagramFiles(plantUmlCode.toString(), baseFileName);
    }

    /**
     * Método para mantener compatibilidad con implementación anterior
     */
    public void generatePatientDiagram() throws IOException {
        generateEntityDiagram();
    }

    /**
     * Genera el código PlantUML para una clase
     */
    private void generateClassUml(StringBuilder sb, ClassInfo classInfo) {
        // Determinar si es clase o interfaz
        String classType = classInfo.isInterface ? "interface" : "class";
        
        // Iniciar definición de clase
        sb.append("  ").append(classType).append(" ").append(classInfo.name).append(" {\n");
        
        // Agregar campos
        for (FieldInfo field : classInfo.fields) {
            sb.append("    ").append(field.visibility).append(" ")
                    .append(field.name).append(": ").append(field.type).append("\n");
        }
        
        // Separador si hay campos y métodos
        if (!classInfo.fields.isEmpty() && !classInfo.methods.isEmpty()) {
            sb.append("\n");
        }
        
        // Agregar métodos
        for (MethodInfo method : classInfo.methods) {
            sb.append("    ").append(method.visibility).append(" ")
                    .append(method.name).append("(");
            
            // Parámetros
            if (!method.parameters.isEmpty()) {
                sb.append(String.join(", ", method.parameters));
            }
            
            sb.append("): ").append(method.returnType).append("\n");
        }
        
        // Cerrar definición de clase
        sb.append("  }\n");
    }

    /**
     * Genera los archivos de diagrama (PUML y PNG)
     */
    private void generateDiagramFiles(String plantUmlCode, String baseName) throws IOException {
        // Guardar código PlantUML
        Path plantUmlFile = Paths.get(OUTPUT_DIR, baseName + ".puml");
        Files.write(plantUmlFile, plantUmlCode.getBytes(StandardCharsets.UTF_8));
        
        // Generar imagen del diagrama
        generateImageFromPlantUml(plantUmlCode, Paths.get(OUTPUT_DIR, baseName + ".png").toString());
        
        // También crear un archivo HTML que muestre cómo usar el diagrama
        Path htmlFile = Paths.get(OUTPUT_DIR, "index.html");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile.toFile()))) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html lang=\"es\">\n");
            writer.write("<head>\n");
            writer.write("    <meta charset=\"UTF-8\">\n");
            writer.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            writer.write("    <title>Diagrama DDD de " + entityFilter + "</title>\n");
            writer.write("    <style>\n");
            writer.write("        body { font-family: Arial, sans-serif; max-width: 1000px; margin: 0 auto; padding: 20px; }\n");
            writer.write("        h1 { color: #2196F3; }\n");
            writer.write("        .instructions { background-color: #f5f5f5; padding: 15px; border-radius: 5px; }\n");
            writer.write("        pre { background-color: #f0f0f0; padding: 10px; border-radius: 5px; overflow-x: auto; }\n");
            writer.write("        .notes { font-style: italic; color: #666; }\n");
            writer.write("        .diagram-container { margin: 20px 0; }\n");
            writer.write("        .btn { display: inline-block; padding: 10px 15px; background-color: #2196F3; color: white; text-decoration: none; border-radius: 4px; margin-right: 10px; }\n");
            writer.write("        img { max-width: 100%; border: 1px solid #ddd; }\n");
            writer.write("    </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("    <h1>Diagrama DDD de la entidad " + entityFilter + "</h1>\n");
            writer.write("    <p>Se ha generado el diagrama de clases de la entidad " + entityFilter + " con su estructura DDD.</p>\n");
            
            writer.write("    <div class=\"diagram-container\">\n");
            writer.write("        <h2>Diagrama generado</h2>\n");
            writer.write("        <img src=\"" + baseName + ".png\" alt=\"Diagrama DDD de " + entityFilter + "\">\n");
            writer.write("    </div>\n");
            
            writer.write("    <div class=\"diagram-container\">\n");
            writer.write("        <h2>Acciones</h2>\n");
            writer.write("        <a href=\"" + baseName + ".puml\" class=\"btn\" target=\"_blank\">Ver código PlantUML</a>\n");
            writer.write("        <a href=\"" + baseName + ".png\" class=\"btn\" target=\"_blank\">Ver imagen en tamaño completo</a>\n");
            writer.write("    </div>\n");
            
            writer.write("    <div class=\"instructions\">\n");
            writer.write("        <h2>Información</h2>\n");
            writer.write("        <p>Este diagrama se ha generado automáticamente analizando el código fuente del proyecto.</p>\n");
            writer.write("        <p>Si deseas editar el diagrama, puedes modificar el archivo .puml y utilizar PlantUML para regenerarlo.</p>\n");
            writer.write("    </div>\n");
            
            writer.write("    <h2>Estructura del proyecto</h2>\n");
            writer.write("    <p>El diagrama muestra las clases relacionadas con la entidad " + entityFilter + " en la arquitectura DDD, organizada por capas:</p>\n");
            writer.write("    <ul>\n");
            writer.write("        <li><strong>Dominio:</strong> Las entidades y objetos de valor del núcleo del negocio</li>\n");
            writer.write("        <li><strong>Aplicación:</strong> Los casos de uso que coordinan el dominio</li>\n");
            writer.write("        <li><strong>Infraestructura:</strong> Las implementaciones técnicas de los repositorios</li>\n");
            writer.write("        <li><strong>Presentación:</strong> Los controladores REST que exponen la API</li>\n");
            writer.write("    </ul>\n");
            
            writer.write("</body>\n");
            writer.write("</html>\n");
        }

        System.out.println("Archivo PlantUML generado en: " + plantUmlFile.toAbsolutePath());
        System.out.println("Imagen del diagrama generada en: " + Paths.get(OUTPUT_DIR, baseName + ".png").toAbsolutePath());
        System.out.println("Archivo HTML con instrucciones generado en: " + htmlFile.toAbsolutePath());
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