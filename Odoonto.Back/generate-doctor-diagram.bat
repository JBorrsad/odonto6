@echo off
echo Generando diagrama DDD para la entidad Doctor...

cd %~dp0
java -cp "target/classes;target/dependency/*" odoonto.documentation.plantuml.tools.DDDDiagramGenerator Doctor

if exist "src\main\java\odoonto\documentation\plantuml\doctor\doctor_ddd_diagram.png" (
    echo Diagrama generado exitosamente en:
    echo src\main\java\odoonto\documentation\plantuml\doctor\doctor_ddd_diagram.png
    echo src\main\java\odoonto\documentation\plantuml\doctor\doctor_ddd_diagram.puml
    start explorer "src\main\java\odoonto\documentation\plantuml\doctor"
) else (
    echo No se pudo generar el diagrama. Revisa los errores para más detalles.
)

pause 