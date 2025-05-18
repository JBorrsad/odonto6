package odoonto.infrastructure.tools;

/**
 * Clase utilitaria para facilitar la generación de diagramas DDD
 * desde una línea de comandos.
 */
public class GenerateDDDDiagrams {

    /**
     * Punto de entrada para generar diagramas DDD.
     * Esto facilita ejecutar el generador desde Maven o línea de comandos.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println("==== Iniciando generación de diagramas DDD para arquitectura de Paciente ====");
        DDDDiagramGenerator.main(args);
    }
} 