# test_simple.ps1 - Solo pruebas HTTP REST
# Usa este script DESPUES de iniciar manualmente tu aplicacion Spring Boot

Param(
    [string]$BaseUrl = "http://localhost:8080",
    [switch]$SoloDoctores,
    [int]$TimeoutSec = 2,
    [int]$MaxAttempts = 10
)

function Wait-ForEndpoint {
    Param(
        $Url,
        [switch]$Optional
    )
    Write-Host "Esperando a que $Url este disponible..."
    $attempts = 0
    while ($attempts -lt $MaxAttempts) {
        try {
            $r = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec $TimeoutSec -ErrorAction Stop
            if ($r.StatusCode -eq 200) { 
                Write-Host "`n$Url esta disponible."
                return $true 
            }
        } catch {
            Write-Host -NoNewline "."
        }
        Start-Sleep -Seconds 2
        $attempts++
    }
    
    if ($Optional) {
        Write-Host "`nAdvertencia: No se pudo conectar a $Url despues de $MaxAttempts intentos."
        Write-Host "Continuando sin probar este endpoint..."
        return $false
    } else {
        Write-Host "`nError: No se pudo conectar a $Url despues de $MaxAttempts intentos."
        Write-Host "Asegurate que la aplicacion esta en ejecucion antes de usar este script."
        exit 1
    }
}

Write-Host "=== Script de pruebas para API REST ==="
Write-Host "Asegura que tu aplicacion Spring Boot esta en ejecucion en $BaseUrl"
Write-Host "Intentando conectar con la API..."

# Esperar a que /api/doctors responda
Wait-ForEndpoint "$BaseUrl/api/doctors"

# --- DOCTORS CRUD ----------------------------------

Write-Host "`n=== Tests para DOCTORS ==="

# GET all
Write-Host "GET /api/doctors"
Invoke-RestMethod -Uri "$BaseUrl/api/doctors" | Write-Host

# POST create
$docBody = @{
    nombreCompleto = "Dr. ScriptPS"
    especialidad   = "Testing"
} | ConvertTo-Json
Write-Host "POST /api/doctors"
$createdDoc = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/doctors" -Body $docBody -ContentType "application/json"
$docId = $createdDoc.id
Write-Host "-> creado id=$docId"

# GET by id
Write-Host "GET /api/doctors/$docId"
Invoke-RestMethod -Uri "$BaseUrl/api/doctors/$docId" | Write-Host

# PUT update
$updBody = @{
    id = $docId
    nombreCompleto = "Dr. ScriptPS Updated"
    especialidad   = "Testing-Updated"
} | ConvertTo-Json
Write-Host "PUT /api/doctors/$docId"
Invoke-RestMethod -Method Put -Uri "$BaseUrl/api/doctors/$docId" -Body $updBody -ContentType "application/json" | Write-Host

# DELETE
Write-Host "DELETE /api/doctors/$docId"
Invoke-WebRequest -Method Delete -Uri "$BaseUrl/api/doctors/$docId" -UseBasicParsing | Write-Host

# GET post-delete (debe 404)
Write-Host "GET /api/doctors/$docId (esperado 404)"
try {
    Invoke-WebRequest -Uri "$BaseUrl/api/doctors/$docId" -UseBasicParsing -ErrorAction Stop
} catch {
    Write-Host "-> Recibido $($_.Exception.Response.StatusCode)"
}

# --- PATIENTS CRUD ----------------------------------

if ($SoloDoctores) {
    Write-Host "`n=== Omitiendo pruebas de PATIENTS por parametro SoloDoctores ==="
} else {
    $patientsAvailable = Wait-ForEndpoint "$BaseUrl/api/patients" -Optional
    
    if ($patientsAvailable) {
        Write-Host "`n=== Tests para PATIENTS ==="
        # GET all
        Write-Host "GET /api/patients"
        Invoke-RestMethod -Uri "$BaseUrl/api/patients" | Write-Host

        # POST create
        $patBody = @{
            nombre    = "AnaPS"
            apellido  = "Prueba"
            fechaNacimiento = "1990-05-12T00:00:00Z"
            sexo      = "FEMENINO"
            telefono  = "600123456"
            email     = "ana.ps@example.com"
            edad      = 34
        } | ConvertTo-Json
        Write-Host "POST /api/patients"
        $createdPat = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/patients" -Body $patBody -ContentType "application/json"
        $patId = $createdPat.id
        Write-Host "-> creado id=$patId"

        # GET by id
        Invoke-RestMethod -Uri "$BaseUrl/api/patients/$patId" | Write-Host

        # PUT update
        $patUpd = @{
            id       = $patId
            nombre   = "AnaPS Updated"
            apellido = "PruebaUpd"
            fechaNacimiento = "1990-05-12T00:00:00Z"
            sexo     = "FEMENINO"
            telefono = "600654321"
            email    = "ana.updated@example.com"
            edad     = 34
        } | ConvertTo-Json
        Write-Host "PUT /api/patients/$patId"
        Invoke-RestMethod -Method Put -Uri "$BaseUrl/api/patients/$patId" -Body $patUpd -ContentType "application/json" | Write-Host

        # DELETE
        Write-Host "DELETE /api/patients/$patId"
        Invoke-WebRequest -Method Delete -Uri "$BaseUrl/api/patients/$patId" -UseBasicParsing | Write-Host
    } else {
        Write-Host "Continuando sin ID de paciente disponible."
        # Variable a usar para appointments, si no tenemos paciente real
        $patId = "paciente-prueba-1"
    }
}

# --- APPOINTMENTS CRUD ------------------------------

if ($SoloDoctores) {
    Write-Host "`n=== Omitiendo pruebas de APPOINTMENTS por parametro SoloDoctores ==="
} else {
    $appointmentsAvailable = Wait-ForEndpoint "$BaseUrl/api/appointments" -Optional
    
    if ($appointmentsAvailable) {
        Write-Host "`n=== Tests para APPOINTMENTS ==="
        # GET all
        Write-Host "GET /api/appointments"
        Invoke-RestMethod -Uri "$BaseUrl/api/appointments" | Write-Host

        # POST create (usa el doctor y paciente recien creados)
        $apptBody = @{
            patientId = $patId
            doctorId  = $docId
            start     = "2025-05-16T10:00:00"
            slots     = 2
        } | ConvertTo-Json
        Write-Host "POST /api/appointments"
        try {
            $createdAppt = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/appointments" -Body $apptBody -ContentType "application/json"
            $apptId = $createdAppt.id
            Write-Host "-> creado id=$apptId"

            # Tambien probar /book endpoint
            $bookBody = @{
                patientId = $patId
                doctorId  = $docId
                start     = "2025-05-17T10:00:00"
                slots     = 1
            } | ConvertTo-Json
            Write-Host "POST /api/appointments/book"
            try {
                $bookedAppt = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/appointments/book" -Body $bookBody -ContentType "application/json"
                $bookedId = $bookedAppt.id
                Write-Host "-> cita reservada id=$bookedId"
            } catch {
                Write-Host "Error en booking: $_"
                $bookedId = $null
            }

            # GET by id
            Invoke-RestMethod -Uri "$BaseUrl/api/appointments/$apptId" | Write-Host

            # PUT update
            $apptUpd = @{
                patientId = $patId
                doctorId  = $docId
                start     = "2025-05-16T11:00:00"
                slots     = 1
            } | ConvertTo-Json
            Write-Host "PUT /api/appointments/$apptId"
            Invoke-RestMethod -Method Put -Uri "$BaseUrl/api/appointments/$apptId" -Body $apptUpd -ContentType "application/json" | Write-Host

            # DELETE
            Write-Host "DELETE /api/appointments/$apptId"
            Invoke-WebRequest -Method Delete -Uri "$BaseUrl/api/appointments/$apptId" -UseBasicParsing | Write-Host

            # Limpiar la cita reservada tambien
            if ($bookedId) {
                Write-Host "DELETE /api/appointments/$bookedId"
                Invoke-WebRequest -Method Delete -Uri "$BaseUrl/api/appointments/$bookedId" -UseBasicParsing | Write-Host
            }
        } catch {
            Write-Host "Error creando cita: $_"
        }
    }
}

Write-Host "`nTest completado!"
Write-Host "Recuerda detener manualmente tu aplicacion Spring Boot cuando ya no la necesites."
