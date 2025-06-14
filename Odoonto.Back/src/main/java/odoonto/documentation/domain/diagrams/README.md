# Domain Diagrams

Esta carpeta contiene todos los diagramas de la capa de dominio generados automáticamente usando Mermaid.

## 📁 Estructura

```
diagrams/
├── overview/
│   ├── domain-overview.mmd          # Vista general de todos los bounded contexts
│   └── bounded-contexts.mmd         # Relaciones entre contextos
├── contexts/
│   ├── patients-context.mmd         # Diagrama detallado del contexto Patients
│   ├── records-context.mmd          # Diagrama detallado del contexto Records
│   ├── scheduling-context.mmd       # Diagrama detallado del contexto Scheduling
│   ├── staff-context.mmd            # Diagrama detallado del contexto Staff
│   └── catalog-context.mmd          # Diagrama detallado del contexto Catalog
├── events/
│   └── domain-events.mmd            # Todos los eventos de dominio
├── services/
│   └── domain-services.mmd          # Servicios de dominio
└── specifications/
    └── domain-specifications.mmd    # Especificaciones DDD
```

## 🔄 Actualización Automática

Los diagramas se actualizan automáticamente cuando:
1. Se modifican clases de dominio
2. Se ejecuta el comando de regeneración
3. Se solicita actualización via MCP de Cursor

## 🎨 Sintaxis Mermaid

Todos los diagramas usan sintaxis Mermaid estándar:
- `classDiagram` para diagramas de clases UML
- `graph TB` para diagramas de arquitectura
- `sequenceDiagram` para flujos de eventos

## 📖 Cómo usar

1. Abrir cualquier archivo `.mmd` en VS Code/Cursor
2. Usar la extensión Mermaid Preview para visualizar
3. Los diagramas se renderizan automáticamente en GitHub/GitLab 