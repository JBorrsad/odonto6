---
description: Automatic diagram generation using Cursor MCP for domain documentation
globs: src/main/java/odoonto/documentation/diagrams/**/*.mmd
alwaysApply: false
---

<rule>
  <meta>
    <title>Automatic Domain Diagram Generation</title>
    <description>Regenera automáticamente todos los diagramas del dominio usando MCP de Cursor cuando el usuario lo solicite</description>
    <created-at utc-timestamp="1744240920">January 10, 2025, 09:22 AM</created-at>
    <last-updated-at utc-timestamp="1744240920">January 10, 2025, 09:22 AM</last-updated-at>
    <applies-to>
      <action-matcher action="regenerate-diagrams">Cuando el usuario pide regenerar diagramas</action-matcher>
      <action-matcher action="update-diagrams">Cuando el usuario pide actualizar diagramas</action-matcher>
      <action-matcher action="refresh-diagrams">Cuando el usuario pide refrescar diagramas</action-matcher>
    </applies-to>
  </meta>

  <requirements>
    <non-negotiable priority="critical">
      <description>Cuando el usuario solicite regenerar diagramas, SIEMPRE usar create_diagram MCP para cada diagrama y luego guardar automáticamente en archivos .mmd correspondientes.</description>
      <examples>
        <example title="Regeneración automática de diagramas">
          <correct-example title="Flujo completo MCP + guardado" conditions="Usuario pide regenerar diagramas" expected-result="Todos los diagramas actualizados" correctness-criteria="Usa MCP y guarda archivos"><![CDATA[
// 1. USAR create_diagram MCP para vista general (SIN emojis, SIN estereotipos <<>>)
create_diagram(domain-overview con tema neutral)

// 2. GUARDAR automáticamente en archivo
edit_file(documentation/domain/domain-overview.mmd)

// 3. REPETIR para cada bounded context:
create_diagram(patients-context)
edit_file(documentation/domain/patients/patients-context.mmd)

create_diagram(records-context)  
edit_file(documentation/domain/records/records-context.mmd)

create_diagram(scheduling-context)
edit_file(documentation/domain/scheduling/scheduling-context.mmd)

create_diagram(staff-context)
edit_file(documentation/domain/staff/staff-context.mmd)

create_diagram(catalog-context)
edit_file(documentation/domain/catalog/catalog-context.mmd)

create_diagram(shared-context)
edit_file(documentation/domain/shared/shared-context.mmd)

// 4. EJECUTAR script de sincronización
run_terminal_cmd("node src/main/java/odoonto/documentation/domain/sync-diagrams.js")
]]></correct-example>
          <incorrect-example title="Solo generar sin guardar" conditions="Usuario pide regenerar diagramas" expected-result="Todos los diagramas actualizados" incorrectness-criteria="No guarda archivos"><![CDATA[
// Wrong - solo generar sin persistir
create_diagram(domain-overview-visual)
// Missing: edit_file to save the diagram
// Missing: repeat for all contexts
// Missing: sync script execution
]]></incorrect-example>
        </example>
      </examples>
    </non-negotiable>

    <requirement priority="critical">
      <description>SIEMPRE usar tema neutral en todos los diagramas: %%{init: {'theme':'neutral'}}%%</description>
      <examples>
        <example title="Tema consistente">
          <correct-example title="Tema neutral" conditions="Generando cualquier diagrama" expected-result="Diagrama con tema neutral" correctness-criteria="Usa theme neutral"><![CDATA[
%%{init: {'theme':'neutral'}}%%
classDiagram
    class PatientAggregate {
        <<aggregate>>
        // ... content
    }
]]></correct-example>
          <incorrect-example title="Tema incorrecto" conditions="Generando cualquier diagrama" expected-result="Diagrama con tema neutral" incorrectness-criteria="Usa otro tema"><![CDATA[
%%{init: {'theme':'base'}}%%
classDiagram
    // Wrong theme
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <requirement priority="critical">
      <description>NO usar estereotipos UML (<<>>) ni emojis. Solo tema neutral, nombres de clases limpios, separadores (___), y relaciones claras.</description>
      <examples>
        <example title="Estructura de diagrama">
          <correct-example title="Estructura limpia" conditions="Generando diagrama de contexto" expected-result="Diagrama sin estereotipos" correctness-criteria="Sin <<>> ni emojis"><![CDATA[
class PatientAggregate {
    -PatientId patientId
    -PersonName personName
    ___
    +register() void
    +updateContactInfo() void
}

class PatientId {
    -String value
    ___
    +PatientId(String)
    +getValue() String
}

PatientAggregate *-- PatientId : contains
]]></correct-example>
          <incorrect-example title="Con estereotipos" conditions="Generando diagrama de contexto" expected-result="Diagrama sin estereotipos" incorrectness-criteria="Usa <<>> o emojis"><![CDATA[
class PatientAggregate {
    <<aggregate>>  // Wrong - no usar estereotipos
    -PatientId patientId
}

subgraph "👥 Patients Context"  // Wrong - no usar emojis
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <requirement priority="high">
      <description>Ejecutar automáticamente el script de sincronización después de regenerar todos los diagramas para actualizar el README.md.</description>
      <examples>
        <example title="Sincronización automática">
          <correct-example title="Script ejecutado" conditions="Después de regenerar diagramas" expected-result="README actualizado" correctness-criteria="Ejecuta npm run sync-diagrams"><![CDATA[
// Después de generar y guardar todos los diagramas:
run_terminal_cmd("npm run sync-diagrams")
// o
run_terminal_cmd("node src/main/java/odoonto/documentation/domain/sync-diagrams.js")
]]></correct-example>
          <incorrect-example title="Sin sincronización" conditions="Después de regenerar diagramas" expected-result="README actualizado" incorrectness-criteria="No ejecuta script"><![CDATA[
// Wrong - generar diagramas pero no sincronizar README
create_diagram(...)
edit_file(...)
// Missing: run_terminal_cmd for sync
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <requirement priority="high">
      <description>Usar create_diagram MCP en paralelo cuando sea posible para maximizar eficiencia, pero guardar archivos secuencialmente para evitar conflictos.</description>
      <examples>
        <example title="Generación eficiente">
          <correct-example title="MCP en paralelo" conditions="Regenerando múltiples diagramas" expected-result="Generación rápida" correctness-criteria="Múltiples create_diagram simultáneos"><![CDATA[
// Generar múltiples diagramas en paralelo
create_diagram(overview-diagram)
create_diagram(patients-diagram)  
create_diagram(records-diagram)
// Luego guardar secuencialmente
edit_file(overview.mmd)
edit_file(patients.mmd)
edit_file(records.mmd)
]]></correct-example>
          <incorrect-example title="Generación secuencial" conditions="Regenerando múltiples diagramas" expected-result="Generación rápida" incorrectness-criteria="Un diagrama a la vez"><![CDATA[
// Wrong - uno por uno es lento
create_diagram(overview)
edit_file(overview.mmd)
create_diagram(patients)  // Should be parallel
edit_file(patients.mmd)
]]></incorrect-example>
        </example>
      </examples>
    </requirement>
  </requirements>

  <triggers>
    <trigger-phrase>regenera los diagramas</trigger-phrase>
    <trigger-phrase>actualiza los diagramas</trigger-phrase>
    <trigger-phrase>refresca los diagramas</trigger-phrase>
    <trigger-phrase>genera los diagramas de nuevo</trigger-phrase>
    <trigger-phrase>vuelve a generar los diagramas</trigger-phrase>
    <trigger-phrase>regenerate diagrams</trigger-phrase>
    <trigger-phrase>update diagrams</trigger-phrase>
    <trigger-phrase>refresh diagrams</trigger-phrase>
  </triggers>

  <context description="Configuración de diagramas">
    <context-item title="MCP Tool">Usar create_diagram MCP de Cursor para visualización inmediata</context-item>
    <context-item title="Tema">Siempre usar theme:'neutral' para consistencia visual</context-item>
    <context-item title="Bounded Contexts">patients, records, scheduling, staff, catalog, shared</context-item>
    <context-item title="Archivos">Guardar en src/main/java/odoonto/documentation/domain/diagrams/</context-item>
    <context-item title="Sincronización">Ejecutar npm run sync-diagrams al final</context-item>
  </context>

  <references>
    <reference as="dependency" href=".cursor/rules/rules.md" reason="Standard rule format">Base rule format and structure requirements</reference>
    <reference as="context" href="src/main/java/odoonto/documentation/domain/sync-diagrams.js" reason="Sync script">Script de sincronización de diagramas</reference>
    <reference as="context" href="src/main/java/odoonto/documentation/domain/README-template.md" reason="Template">Template del README con placeholders</reference>
  </references>
</rule> 