package odoonto.documentation.plantuml;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controlador para proveer acceso a la documentación de DDD
 * Este controlador permite visualizar los diagramas generados
 * directamente desde la aplicación.
 */
@Controller
@RequestMapping("/documentation/ddd")
public class DDDDocumentation {

    private static final String DOCUMENTATION_DIR = "src/main/java/odoonto/documentation/plantuml";
    
    /**
     * Página principal de documentación DDD - Lista todas las entidades disponibles
     */
    @GetMapping("")
    @ResponseBody
    public String index() {
        try {
            // Obtener lista de subdirectorios (entidades)
            List<String> entities = new ArrayList<>();
            try (Stream<Path> paths = Files.list(Paths.get(DOCUMENTATION_DIR))) {
                entities = paths
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
            }
            
            StringBuilder response = new StringBuilder();
            response.append("Documentación DDD - Entidades disponibles:\n\n");
            
            if (entities.isEmpty()) {
                response.append("No se han encontrado diagramas generados.\n");
            } else {
                for (String entity : entities) {
                    Path diagramPath = Paths.get(DOCUMENTATION_DIR, entity, entity + "_ddd_diagram.puml");
                    boolean exists = Files.exists(diagramPath);
                    response.append("- ").append(entity.substring(0, 1).toUpperCase() + entity.substring(1))
                           .append(": ")
                           .append(exists ? "✅ [Disponible]" : "❌ [No generado]")
                           .append(" [/documentation/ddd/").append(entity).append(".puml]")
                           .append("\n");
                }
            }
            
            return response.toString();
        } catch (Exception e) {
            return "Error al obtener la lista de entidades: " + e.getMessage();
        }
    }
    
    /**
     * Devuelve el código PlantUML del diagrama de una entidad específica
     */
    @GetMapping("/{entity}.puml")
    @ResponseBody
    public String getEntityDiagram(@PathVariable("entity") String entity) {
        try {
            Path filePath = Paths.get(DOCUMENTATION_DIR, entity.toLowerCase(), entity.toLowerCase() + "_ddd_diagram.puml");
            if (Files.exists(filePath)) {
                return new String(Files.readAllBytes(filePath));
            } else {
                return "# El diagrama de " + entity + " no ha sido generado todavía.\n# Inicia la aplicación para generarlo automáticamente.";
            }
        } catch (Exception e) {
            return "# Error al leer el diagrama: " + e.getMessage();
        }
    }
    
    /**
     * Comprueba si los diagramas han sido generados
     */
    @GetMapping("/status")
    @ResponseBody
    public String getStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Estado de la documentación DDD:\n\n");
        
        // Comprobar directorios de entidades
        File dir = new File(DOCUMENTATION_DIR);
        File[] entityDirs = dir.listFiles(File::isDirectory);
        
        if (entityDirs != null && entityDirs.length > 0) {
            for (File entityDir : entityDirs) {
                String entityName = entityDir.getName();
                File pumlFile = new File(entityDir, entityName + "_ddd_diagram.puml");
                File pngFile = new File(entityDir, entityName + "_ddd_diagram.png");
                
                status.append("- Entidad: ").append(entityName.substring(0, 1).toUpperCase() + entityName.substring(1)).append("\n");
                status.append("  - Diagrama PlantUML: ").append(pumlFile.exists() ? "✅ Generado" : "❌ No generado").append("\n");
                status.append("  - Imagen PNG: ").append(pngFile.exists() ? "✅ Generada" : "❌ No generada").append("\n");
            }
        } else {
            status.append("No se han encontrado directorios de entidades.\n");
        }
        
        return status.toString();
    }
} 