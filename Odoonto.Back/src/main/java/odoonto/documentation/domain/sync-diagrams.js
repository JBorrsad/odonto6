#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

// Rutas relativas desde esta carpeta de documentación del dominio
const CURRENT_DIR = __dirname;
const README_PATH = path.join(CURRENT_DIR, '../../domain/README.md');
const TEMPLATE_PATH = path.join(CURRENT_DIR, 'README-template.md');

// Mapeo de archivos .mmd a sus placeholders en el README
const DIAGRAM_MAPPINGS = {
    'diagrams/overview/domain-overview-visual.mmd': '<!-- DIAGRAM: domain-overview-visual -->',
    'diagrams/contexts/patients-context.mmd': '<!-- DIAGRAM: patients-context -->',
    'diagrams/contexts/records-context.mmd': '<!-- DIAGRAM: records-context -->',
    'diagrams/contexts/scheduling-context.mmd': '<!-- DIAGRAM: scheduling-context -->',
    'diagrams/contexts/staff-context.mmd': '<!-- DIAGRAM: staff-context -->',
    'diagrams/contexts/catalog-context.mmd': '<!-- DIAGRAM: catalog-context -->',
    'diagrams/contexts/shared-context.mmd': '<!-- DIAGRAM: shared-context -->'
};

function readDiagramFile(filePath) {
    try {
        const fullPath = path.join(CURRENT_DIR, filePath);
        return fs.readFileSync(fullPath, 'utf8').trim();
    } catch (error) {
        console.warn(`Warning: Could not read diagram file ${filePath}`);
        return null;
    }
}

function updateReadme() {
    console.log('🔄 Synchronizing domain diagrams with README...');
    
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
    console.log('🎉 Domain README.md synchronized successfully!');
}

// Ejecutar sincronización
updateReadme();

// Opcional: Watch mode para desarrollo
if (process.argv.includes('--watch')) {
    console.log('👀 Watching for domain diagram changes...');
    fs.watch(CURRENT_DIR, { recursive: true }, (eventType, filename) => {
        if (filename && filename.endsWith('.mmd')) {
            console.log(`📝 Domain diagram changed: ${filename}`);
            setTimeout(updateReadme, 100); // Debounce
        }
    });
} 