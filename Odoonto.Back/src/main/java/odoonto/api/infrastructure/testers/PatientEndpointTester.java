package odoonto.api.infrastructure.testers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Order(1) // Se ejecuta primero
public class PatientEndpointTester implements CommandLineRunner {

    private final WebClient webClient;
    private String patientTestId;

    public PatientEndpointTester(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8081").build();
    }

    @Override
    public void run(String... args) {
        System.out.println("\n🧪 PRUEBAS DE ENDPOINTS DE PACIENTES");
        System.out.println("===============================");

        testPatientsCRUD();
        
        // Limpiar datos de prueba
        if (patientTestId != null) {
            System.out.print("→ DELETE /api/patients/" + patientTestId + ": ");
            boolean deleteSuccess = testDeleteEndpoint("/api/patients/" + patientTestId);
            System.out.println(deleteSuccess ? "✅ ÉXITO" : "❌ FALLO");
        }
    }

    public String getPatientTestId() {
        return patientTestId;
    }

    private void testPatientsCRUD() {
        System.out.println("\n📋 PRUEBAS DE PACIENTES");
        System.out.println("---------------------");

        // TEST GET (Listar)
        System.out.print("→ GET /api/patients: ");
        boolean getSuccess = testGetEndpoint("/api/patients");
        System.out.println(getSuccess ? "✅ ÉXITO" : "❌ FALLO");

        // TEST POST (Crear)
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("nombre", "Paciente Test");
        patientData.put("apellido", "Apellido Test");
        patientData.put("fechaNacimiento", "2000-01-01T00:00:00.000Z");
        patientData.put("sexo", "MASCULINO");
        patientData.put("telefono", "666123456");
        patientData.put("email", "paciente.test@example.com");

        System.out.print("→ POST /api/patients: ");
        patientTestId = testPostEndpoint("/api/patients", patientData);
        boolean postSuccess = patientTestId != null;
        System.out.println(postSuccess ? "✅ ÉXITO (ID: " + patientTestId + ")" : "❌ FALLO");

        // TEST PUT (Actualizar) - Solo si el POST funcionó
        if (postSuccess) {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("nombre", "Paciente Test Actualizado");
            updateData.put("apellido", "Apellido Test Actualizado");
            updateData.put("telefono", "666987654");

            System.out.print("→ PUT /api/patients/" + patientTestId + ": ");
            boolean putSuccess = testPutEndpoint("/api/patients/" + patientTestId, updateData);
            System.out.println(putSuccess ? "✅ ÉXITO" : "❌ FALLO");

            // TEST GET by ID
            System.out.print("→ GET /api/patients/" + patientTestId + ": ");
            boolean getByIdSuccess = testGetEndpoint("/api/patients/" + patientTestId);
            System.out.println(getByIdSuccess ? "✅ ÉXITO" : "❌ FALLO");
        }
    }

    // Métodos auxiliares para realizar las operaciones CRUD

    private boolean testGetEndpoint(String uri) {
        try {
            return webClient.get().uri(uri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp -> {
                    System.out.print("[HTTP " + resp.statusCode().value() + "] ");
                    return Mono.empty();
                })
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorReturn(false)
                .block();
        } catch (Exception e) {
            System.out.print("[ERROR: " + e.getMessage() + "] ");
            return false;
        }
    }

    private String testPostEndpoint(String uri, Map<String, Object> data) {
        try {
            AtomicReference<String> idRef = new AtomicReference<>();
            
            Boolean success = webClient.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(data))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp -> {
                    System.out.print("[HTTP " + resp.statusCode().value() + "] ");
                    return Mono.empty();
                })
                .bodyToMono(Map.class)
                .map(response -> {
                    if (response.containsKey("id")) {
                        idRef.set(response.get("id").toString());
                        return true;
                    }
                    return false;
                })
                .onErrorReturn(false)
                .block();
            
            return success ? idRef.get() : null;
        } catch (Exception e) {
            System.out.print("[ERROR: " + e.getMessage() + "] ");
            return null;
        }
    }

    private boolean testPutEndpoint(String uri, Map<String, Object> data) {
        try {
            return webClient.put().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(data))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp -> {
                    System.out.print("[HTTP " + resp.statusCode().value() + "] ");
                    return Mono.empty();
                })
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorReturn(false)
                .block();
        } catch (Exception e) {
            System.out.print("[ERROR: " + e.getMessage() + "] ");
            return false;
        }
    }

    private boolean testDeleteEndpoint(String uri) {
        try {
            return webClient.delete().uri(uri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp -> {
                    System.out.print("[HTTP " + resp.statusCode().value() + "] ");
                    return Mono.empty();
                })
                .bodyToMono(Void.class)
                .thenReturn(true)
                .onErrorReturn(false)
                .block();
        } catch (Exception e) {
            System.out.print("[ERROR: " + e.getMessage() + "] ");
            return false;
        }
    }
} 