#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

// Configuración de diagramas
const diagrams = {
    'domain-overview': `
graph TB
    subgraph "Domain Layer"
        subgraph "Patients Context"
            PA[PatientAggregate]
            PE[PersonName]
            PID[PatientId]
        end
        
        subgraph "Records Context"
            MRA[MedicalRecordAggregate]
            OE[OdontogramEntity]
        end
        
        subgraph "Scheduling Context"
            AA[AppointmentAggregate]
            ACE[AvailabilityCalendarEntity]
        end
    end
`,
    
    'patients-detail': `
classDiagram
    class PatientAggregate {
        -PatientId patientId
        -PersonName personName
        -EmailAddress email
        +register()
        +updateContactInfo()
    }
    
    class PatientId {
        -String value
        +getValue() String
    }
    
    PatientAggregate *-- PatientId
`
};

// Crear directorio de diagramas
const diagramsDir = path.join(__dirname, '..', 'docs', 'diagrams');
if (!fs.existsSync(diagramsDir)) {
    fs.mkdirSync(diagramsDir, { recursive: true });
}

// Generar archivos .mmd
Object.entries(diagrams).forEach(([name, content]) => {
    const filePath = path.join(diagramsDir, `${name}.mmd`);
    fs.writeFileSync(filePath, content.trim());
    console.log(`✅ Generated: ${name}.mmd`);
});

console.log('🎉 All diagrams generated successfully!'); 