package odoonto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Scanner;
import odoonto.infrastructure.tools.DDDDiagramGenerator;

/**
 * Clase principal de la aplicación Odoonto.
 * Punto de entrada para iniciar la aplicación Spring Boot.
 * 
 * Esta aplicación incluye un menú interactivo al inicio que permite:
 * - Generar diagramas DDD para diferentes entidades
 * - Iniciar la aplicación normalmente
 * 
 * El menú es implementado por la clase StartupMenuConfig en el paquete
 * odoonto.infrastructure.config
 */
@SpringBootApplication
public class OdoontoApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        // Verificar si hay argumentos para generar diagramas directamente
        if (args.length > 0 && args[0].equals("--generate-diagrams")) {
            // Modo generación de diagramas sin iniciar Spring
            generateDiagramsOnly();
            return;
        }

        // Si no se especifica ningún argumento, mostrar el menú interactivo
        System.out.println("\n=================================================");
        System.out.println("           BIENVENIDO A ODOONTO BACK             ");
        System.out.println("=================================================");
        System.out.println("\nSeleccione una opción:");
        System.out.println("1. Generar diagrama de clases");
        System.out.println("2. Iniciar aplicación");
        System.out.println("\nIngrese el número de la opción deseada: ");
        
        try (Scanner scanner = new Scanner(System.in)) {
            String option = scanner.nextLine().trim();
            
            switch (option) {
                case "1":
                    // Ejecutar solo la generación de diagramas
                    generateDiagramsOnly();
                    return;
                    
                case "2":
                    System.out.println("\nIniciando aplicación...");
                    break;
                    
                default:
                    System.out.println("\nOpción no válida. Iniciando aplicación por defecto...");
                    break;
            }
        }
        
        // Iniciar la aplicación con el contexto de Spring Boot
        SpringApplication.run(OdoontoApplication.class, args);
    }
    
    /**
     * Método que maneja la generación de diagramas sin iniciar el contexto de Spring
     */
    private static void generateDiagramsOnly() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\n--- GENERACIÓN DE DIAGRAMAS ---");
            System.out.println("Seleccione la entidad para generar el diagrama:");
            System.out.println("1. Paciente");
            System.out.println("2. Odontograma");
            System.out.println("3. Doctor");
            System.out.println("4. Cita");
            System.out.println("5. Historial Médico");
            System.out.println("\nIngrese el número de la entidad: ");
            
            String entityOption = scanner.nextLine().trim();
            String entityFilter = "";
            
            switch (entityOption) {
                case "1":
                    entityFilter = "Patient";
                    break;
                case "2":
                    entityFilter = "Odontogram";
                    break;
                case "3":
                    entityFilter = "Doctor";
                    break;
                case "4":
                    entityFilter = "Appointment";
                    break;
                case "5":
                    entityFilter = "MedicalRecord";
                    break;
                default:
                    System.out.println("\nEntidad no válida. Utilizando Paciente por defecto.");
                    entityFilter = "Patient";
                    break;
            }
            
            try {
                System.out.println("\nGenerando diagrama para entidad: " + entityFilter);
                DDDDiagramGenerator.generateDiagramForEntity(entityFilter);
                System.out.println("\nDiagrama generado exitosamente. Saliendo...");
            } catch (Exception e) {
                System.err.println("\nError al generar el diagrama: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 