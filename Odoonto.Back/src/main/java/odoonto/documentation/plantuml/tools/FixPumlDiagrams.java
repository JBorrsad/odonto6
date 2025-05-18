package odoonto.documentation.plantuml.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Clase independiente para corregir archivos PlantUML existentes
 * que tienen problemas de sintaxis con los colores de paquetes.
 */
public class FixPumlDiagrams {
    
    private static final String OUTPUT_DIR = "src/main/java/odoonto/documentation/plantuml";
    
    /**
     * Punto de entrada principal
     */
    public static void main(String[] args) {
        System.out.println("Iniciando corrección de archivos PlantUML...");
        fixAllPumlFiles();
        System.out.println("Proceso completado.");
    }
    
    /**
     * Corrige todos los archivos PUML en el directorio de documentación
     */
    public static void fixAllPumlFiles() {
        try {
            Path dir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(dir)) {
                System.err.println("Directorio no encontrado: " + OUTPUT_DIR);
                return;
            }
            
            // Procesar archivos en el directorio raíz
            processPumlDirectory(dir);
            
            // Procesar archivos en los subdirectorios (carpetas de entidades)
            try (Stream<Path> paths = Files.list(dir)) {
                paths.filter(Files::isDirectory)
                     .forEach(entityDir -> {
                         System.out.println("Verificando subcarpeta de entidad: " + entityDir.getFileName());
                         try {
                             processPumlDirectory(entityDir);
                         } catch (Exception e) {
                             System.err.println("Error al procesar subcarpeta: " + entityDir.getFileName() + " - " + e.getMessage());
                         }
                     });
            }
            
        } catch (IOException e) {
            System.err.println("Error al corregir archivos PUML: " + e.getMessage());
        }
    }
    
    /**
     * Procesa los archivos PUML en un directorio específico
     */
    private static void processPumlDirectory(Path dir) {
        // Buscar todos los archivos PUML en el directorio
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
                         } else {
                             System.out.println("El archivo ya tiene la sintaxis correcta.");
                         }
                     } catch (IOException e) {
                         System.err.println("Error al procesar archivo: " + path.getFileName() + " - " + e.getMessage());
                     }
                 });
        } catch (IOException e) {
            // Esta excepción puede ser lanzada por Files.list(dir)
            System.err.println("Error al listar archivos del directorio: " + dir.getFileName() + " - " + e.getMessage());
        }
    }
} 