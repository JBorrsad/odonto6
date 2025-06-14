@echo off
echo ============================================
echo   EJECUTANDO PRUEBAS DE ARQUITECTURA + DDD
echo ============================================
echo.

echo Ejecutando pruebas de ArchUnit + jMolecules...
mvn test -Dtest="odoonto.architecture.*" -q

if %ERRORLEVEL% == 0 (
    echo.
    echo ================================
    echo   TODAS LAS PRUEBAS PASARON
    echo ================================
    echo La arquitectura cumple con las reglas definidas.
) else (
    echo.
    echo ================================
    echo   ALGUNAS PRUEBAS FALLARON
    echo ================================
    echo Revisa los errores de arquitectura arriba.
)

echo.
echo Presiona cualquier tecla para continuar...
pause > nul 