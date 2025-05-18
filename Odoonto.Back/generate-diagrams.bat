@echo off
echo Generando diagramas DDD para la arquitectura de Paciente...

REM Verificar que Maven esté disponible
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven no está disponible en el PATH.
    echo Por favor instala Maven o asegúrate de que esté en tu PATH.
    exit /b 1
)

REM Compilar el proyecto para asegurarse de que las clases estén disponibles
echo Compilando el proyecto...
mvn clean compile

REM Ejecutar el generador de diagramas
echo Ejecutando generador de diagramas...
mvn exec:java -Dexec.mainClass="odoonto.infrastructure.tools.GenerateDDDDiagrams"

REM Verificar si los diagramas se generaron correctamente
if exist "target\diagrams\patient_ddd_diagram.png" (
    echo.
    echo ======================================
    echo Diagramas generados exitosamente en:
    echo   - target\diagrams\patient_ddd_diagram.puml (código PlantUML)
    echo   - target\diagrams\patient_ddd_diagram.png (imagen PNG)
    echo   - target\diagrams\patient_ddd_diagram.svg (imagen SVG de alta calidad)
    echo ======================================
    echo.
    echo Abriendo directorio de diagramas...
    start explorer "target\diagrams"
) else (
    echo.
    echo ERROR: Los diagramas no se generaron correctamente.
    echo Revisa los errores anteriores para más detalles.
)

exit /b 0 