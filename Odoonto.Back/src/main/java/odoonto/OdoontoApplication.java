package odoonto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import odoonto.documentation.plantuml.tools.DDDDiagramGenerator;

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
            boolean exitMenu = false;
            
            while (!exitMenu) {
                // Detectar entidades automáticamente
                Set<String> detectedEntities = DDDDiagramGenerator.detectAllEntities();
                List<String> entities = new ArrayList<>(detectedEntities);
                
                // Agregar opción "Todas las entidades" al inicio
                entities.add(0, "TODAS LAS ENTIDADES");
                entities.add("Salir");
                
                System.out.println("\n--- GENERACIÓN DE DIAGRAMAS ---");
                System.out.println("Entidades detectadas en el proyecto:");
                
                // Mostrar menú dinámico con todas las entidades detectadas
                for (int i = 0; i < entities.size(); i++) {
                    String entityName = entities.get(i);
                    System.out.println((i + 1) + ". " + (i == 0 ? entityName : 
                                       (i == entities.size() - 1 ? entityName : formatEntityName(entityName))));
                }
                
                System.out.println("\nIngrese el número de la entidad o 'q' para salir: ");
                
                String entityOption = scanner.nextLine().trim();
                
                // Salir si se presiona 'q'
                if (entityOption.equalsIgnoreCase("q")) {
                    exitMenu = true;
                    System.out.println("Saliendo del generador de diagramas...");
                    continue;
                }
                
                try {
                    int option = Integer.parseInt(entityOption);
                    
                    if (option < 1 || option > entities.size()) {
                        System.out.println("\nOpción no válida. Por favor, seleccione una opción válida.");
                        continue;
                    }
                    
                    String selectedEntity = entities.get(option - 1);
                    
                    if (option == 1) {
                        // Generar diagramas para todas las entidades
                        System.out.println("\nGenerando diagramas para todas las entidades...");
                        DDDDiagramGenerator.generateDiagramsForAllEntities();
                        System.out.println("\nTodos los diagramas generados exitosamente.");
                    } else if (option == entities.size()) {
                        // Salir del menú
                        exitMenu = true;
                        System.out.println("Saliendo del generador de diagramas...");
                    } else {
                        // Generar diagrama para la entidad seleccionada
                        System.out.println("\nGenerando diagrama para entidad: " + selectedEntity);
                        DDDDiagramGenerator.generateDiagramForEntity(selectedEntity);
                        System.out.println("\nDiagrama generado exitosamente.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\nEntrada no válida. Por favor, ingrese un número o 'q' para salir.");
                }
                
                if (!exitMenu) {
                    System.out.println("\n¿Desea generar otro diagrama? (s/n): ");
                    String continueOption = scanner.nextLine().trim().toLowerCase();
                    exitMenu = !continueOption.equals("s");
                }
            }
            
            System.out.println("\nSaliendo...");
        } catch (Exception e) {
            System.err.println("\nError al generar diagrama: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Formatea el nombre de la entidad para mostrarlo en el menú
     */
    private static String formatEntityName(String entityName) {
        // Convertir CamelCase a formato más legible
        StringBuilder formatted = new StringBuilder(entityName.length() + 10);
        char[] chars = entityName.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            // Si es la primera letra o la letra anterior es minúscula y esta es mayúscula
            if (i == 0 || (Character.isLowerCase(chars[i-1]) && Character.isUpperCase(chars[i]))) {
                if (i > 0) formatted.append(" ");
            }
            formatted.append(chars[i]);
        }
        
        return formatted.toString();
    }
} 