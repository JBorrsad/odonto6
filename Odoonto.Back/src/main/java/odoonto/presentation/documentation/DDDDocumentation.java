package odoonto.presentation.documentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controlador para proveer acceso a la documentación de DDD
 * Este controlador permite visualizar los diagramas generados
 * directamente desde la aplicación.
 */
@Controller
@RequestMapping("/documentation/ddd")
public class DDDDocumentation {

    private static final String DOCUMENTATION_DIR = "src/main/java/odoonto/presentation/documentation";
    
    /**
     * Página principal de documentación DDD
     */
    @GetMapping("")
    public String index() {
        return "forward:/documentation/ddd/index.html";
    }
    
    /**
     * Devuelve el código PlantUML del diagrama de Paciente
     */
    @GetMapping("/patient.puml")
    @ResponseBody
    public String getPatientDiagram() {
        try {
            Path filePath = Paths.get(DOCUMENTATION_DIR, "patient_ddd_diagram.puml");
            if (Files.exists(filePath)) {
                return new String(Files.readAllBytes(filePath));
            } else {
                return "# El diagrama de paciente no ha sido generado todavía.\n# Inicia la aplicación para generarlo automáticamente.";
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
        File pumlFile = new File(DOCUMENTATION_DIR, "patient_ddd_diagram.puml");
        File htmlFile = new File(DOCUMENTATION_DIR, "index.html");
        
        StringBuilder status = new StringBuilder();
        status.append("Estado de la documentación DDD:\n\n");
        status.append("- Diagrama PlantUML de Paciente: ").append(pumlFile.exists() ? "✅ Generado" : "❌ No generado").append("\n");
        status.append("- Página HTML de documentación: ").append(htmlFile.exists() ? "✅ Generada" : "❌ No generada").append("\n");
        
        return status.toString();
    }
} 