@echo off
echo Diagnóstico de PlantUML...

rem Detectar instalación de PlantUML
where plantuml.jar >nul 2>nul
if %ERRORLEVEL% equ 0 (
  echo PlantUML encontrado en PATH.
  plantuml -version
) else (
  echo PlantUML no encontrado en PATH.
)

echo.
echo Ruta de los archivos PUML:
echo src\main\java\odoonto\documentation\plantuml\doctor\doctor_ddd_diagram.puml
echo src\main\java\odoonto\documentation\plantuml\doctor\plantuml_diagnostico.puml

echo.
echo Intentando generar diagrama diagnóstico con PlantUML...
java -jar plantuml.jar src\main\java\odoonto\documentation\plantuml\doctor\plantuml_diagnostico.puml -v

echo.
echo Si PlantUML está instalado, se habrán generado imágenes PNG en el mismo directorio.
echo Verifique si se encuentran los archivos:
echo - src\main\java\odoonto\documentation\plantuml\doctor\plantuml_diagnostico.png

echo.
echo Recomendaciones para problemas con PlantUML:
echo 1. Descargue la última versión desde: https://plantuml.com/download
echo 2. Coloque plantuml.jar en su PATH o especifique la ruta completa
echo 3. Para el diagrama doctor_ddd_diagram.puml, pruebe abrirlo en http://www.plantuml.com/plantuml/

pause 