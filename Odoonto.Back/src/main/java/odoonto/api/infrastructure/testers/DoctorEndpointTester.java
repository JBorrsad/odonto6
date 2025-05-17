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
@Order(3) // Se ejecuta tercero
public class DoctorEndpointTester implements CommandLineRunner {

    private final WebClient webClient;
    private String doctorTestId;

    public DoctorEndpointTester(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8081").build();
    }

    @Override
    public void run(String... args) {
        System.out.println("\n🧪 PRUEBAS DE ENDPOINTS DE DOCTORES");
        System.out.println("===============================");

        testDoctorsCRUD();
    }

    public String getDoctorTestId() {
        return doctorTestId;
    }

    private void testDoctorsCRUD() {
        System.out.println("\n📋 PRUEBAS DE DOCTORES");
        System.out.println("--------------------");

        // TEST GET (Listar)
        System.out.print("→ GET /api/doctors: ");
        boolean getSuccess = testGetEndpoint("/api/doctors");
        System.out.println(getSuccess ? "✅ ÉXITO" : "❌ FALLO");

        // TEST POST (Crear)
        Map<String, Object> doctorData = new HashMap<>();
        doctorData.put("nombreCompleto", "Doctor Test");
        doctorData.put("especialidad", "Especialidad Test");

        System.out.print("→ POST /api/doctors: ");
        doctorTestId = testPostEndpoint("/api/doctors", doctorData);
        boolean postSuccess = doctorTestId != null;
        System.out.println(postSuccess ? "✅ ÉXITO (ID: " + doctorTestId + ")" : "❌ FALLO");

        // TEST PUT (Actualizar) - Solo si el POST funcionó
        if (postSuccess) {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("nombreCompleto", "Doctor Test Actualizado");
            updateData.put("especialidad", "Especialidad Test Actualizada");

            System.out.print("→ PUT /api/doctors/" + doctorTestId + ": ");
            boolean putSuccess = testPutEndpoint("/api/doctors/" + doctorTestId, updateData);
            System.out.println(putSuccess ? "✅ ÉXITO" : "❌ FALLO");

            // TEST GET by ID
            System.out.print("→ GET /api/doctors/" + doctorTestId + ": ");
            boolean getByIdSuccess = testGetEndpoint("/api/doctors/" + doctorTestId);
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