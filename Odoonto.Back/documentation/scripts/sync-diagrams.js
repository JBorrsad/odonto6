#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const DIAGRAMS_DIR = 'documentation';
const README_PATH = 'src/main/java/odoonto/domain/README.md';
const TEMPLATE_PATH = 'documentation/domain/README-template.md';

// Mapeo de archivos .mmd a sus placeholders en el README
const DIAGRAM_MAPPINGS = {
    'domain/domain-overview.mmd': '<!-- DIAGRAM: documentation/domain/domain-overview.mmd -->',
    'domain/patients/patients-context.mmd': '<!-- DIAGRAM: documentation/domain/patients/patients-context.mmd -->',
    'domain/records/records-context.mmd': '<!-- DIAGRAM: documentation/domain/records/records-context.mmd -->',
    'domain/scheduling/scheduling-context.mmd': '<!-- DIAGRAM: documentation/domain/scheduling/scheduling-context.mmd -->',
    'domain/staff/staff-context.mmd': '<!-- DIAGRAM: documentation/domain/staff/staff-context.mmd -->',
    'domain/catalog/catalog-context.mmd': '<!-- DIAGRAM: documentation/domain/catalog/catalog-context.mmd -->',
    'domain/shared/shared-context.mmd': '<!-- DIAGRAM: documentation/domain/shared/shared-context.mmd -->'
};

function readDiagramFile(filePath) {
    try {
        const fullPath = path.join(DIAGRAMS_DIR, filePath);
        return fs.readFileSync(fullPath, 'utf8').trim();
    } catch (error) {
        console.warn(`Warning: Could not read diagram file ${filePath}`);
        return null;
    }
}

function updateReadme() {
    console.log('🔄 Synchronizing diagrams with README...');
    
    // Leer template como base
    let readmeContent = fs.readFileSync(TEMPLATE_PATH, 'utf8');
    
    // Reemplazar cada placeholder con el contenido del diagrama
    Object.entries(DIAGRAM_MAPPINGS).forEach(([diagramFile, placeholder]) => {
        const diagramContent = readDiagramFile(diagramFile);
        
        if (diagramContent) {
            // Reemplazar placeholder con bloque mermaid
            const replacement = `\`\`\`mermaid\n${diagramContent}\n\`\`\``;
            readmeContent = readmeContent.replace(placeholder, replacement);
            console.log(`✅ Updated diagram: ${diagramFile}`);
        } else {
            console.log(`⚠️  Skipped diagram: ${diagramFile} (not found)`);
        }
    });
    
    // Escribir README actualizado
    fs.writeFileSync(README_PATH, readmeContent);
    console.log('🎉 README.md synchronized successfully!');
}

// Ejecutar sincronización
updateReadme();

// Opcional: Watch mode para desarrollo
if (process.argv.includes('--watch')) {
    console.log('👀 Watching for diagram changes...');
    fs.watch(DIAGRAMS_DIR, { recursive: true }, (eventType, filename) => {
        if (filename && filename.endsWith('.mmd')) {
            console.log(`📝 Diagram changed: ${filename}`);
            setTimeout(updateReadme, 100); // Debounce
        }
    });
} 