# Script para iniciar tanto el backend como el frontend
Write-Host "=== Iniciando aplicación completa ===" -ForegroundColor Cyan

# Ruta base del proyecto (donde se encuentra este script)
$proyectoBase = Get-Location

# 1. Iniciar el backend
Write-Host "`n=== Iniciando el backend ===" -ForegroundColor Green
Set-Location -Path "$proyectoBase\API"
Write-Host "Ubicación actual: $(Get-Location)" -ForegroundColor Yellow

# Verificar si existe el script run.ps1
if (-not (Test-Path ".\run.ps1")) {
    Write-Host "Error: No se encuentra run.ps1 en la carpeta API" -ForegroundColor Red
    Write-Host "Por favor, ejecuta este script desde la raíz del proyecto" -ForegroundColor Red
    Set-Location $proyectoBase
    exit 1
}

# Crear una versión modificada del script run.ps1 que use el puerto 8080
$runScriptContent = Get-Content -Path ".\run.ps1" -Raw
$modifiedRunScriptContent = $runScriptContent -replace '"-Dserver.port=8081"', '"-Dserver.port=8080"'
$modifiedRunScriptContent | Out-File -FilePath ".\run_temp.ps1" -Encoding utf8

# Iniciar el backend en segundo plano
Write-Host "Ejecutando backend en segundo plano (puerto 8080)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "& {Set-Location -Path '$proyectoBase\API'; Write-Host 'Iniciando servidor backend...' -ForegroundColor Cyan; .\run_temp.ps1}"

# Esperar a que el backend se inicie completamente
Write-Host "Esperando a que el backend esté disponible..." -ForegroundColor Yellow

$backendReady = $false
$maxRetries = 10
$retryCount = 0

while (-not $backendReady -and $retryCount -lt $maxRetries) {
    $retryCount++
    Start-Sleep -Seconds 5
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/doctors" -Method GET -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            $backendReady = $true
            Write-Host "El backend está disponible!" -ForegroundColor Green
        }
    } catch {
        Write-Host "Esperando a que el backend esté disponible... Intento $retryCount de $maxRetries" -ForegroundColor Yellow
    }
}

if (-not $backendReady) {
    Write-Host "No se pudo conectar al backend después de varios intentos." -ForegroundColor Red
    Write-Host "Verifique que el servidor backend esté configurado correctamente." -ForegroundColor Red
    Write-Host "Continuando con la configuración del frontend de todos modos..." -ForegroundColor Yellow
}

# 2. Configurar e iniciar el frontend
Write-Host "`n=== Configurando e iniciando el frontend ===" -ForegroundColor Green
Set-Location -Path "$proyectoBase\dental-clinic-frontend"
Write-Host "Ubicación actual: $(Get-Location)" -ForegroundColor Yellow

# Instalar dependencias si node_modules no existe o si el usuario lo solicita
if (-not (Test-Path ".\node_modules") -or $args[0] -eq "-force-install") {
    Write-Host "Instalando dependencias del frontend (npm install)..." -ForegroundColor Yellow
    npm install
} else {
    Write-Host "La carpeta node_modules ya existe. Omitiendo npm install." -ForegroundColor Yellow
    Write-Host "Para forzar la instalación, ejecuta el script con: -force-install" -ForegroundColor Yellow
}

# Iniciar el frontend
Write-Host "Iniciando servidor de desarrollo del frontend..." -ForegroundColor Yellow
npm run dev

# Limpiar archivo temporal y volver a la ubicación original
Remove-Item -Path "$proyectoBase\API\run_temp.ps1" -ErrorAction SilentlyContinue
Set-Location $proyectoBase 