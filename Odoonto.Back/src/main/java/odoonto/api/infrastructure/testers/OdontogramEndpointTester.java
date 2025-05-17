package odoonto.api.infrastructure.testers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Order(2) // Se ejecuta segundo
public class OdontogramEndpointTester implements CommandLineRunner {

    private final WebClient webClient;
    private final PatientEndpointTester patientTester;

    @Autowired
    public OdontogramEndpointTester(WebClient.Builder builder, PatientEndpointTester patientTester) {
        this.webClient = builder.baseUrl("http://localhost:8081").build();
        this.patientTester = patientTester;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n🧪 PRUEBAS DE ENDPOINTS DE ODONTOGRAMA");
        System.out.println("==================================");

        testOdontogramOperations();
    }

    private void testOdontogramOperations() {
        String patientTestId = patientTester.getPatientTestId();
        
        if (patientTestId != null) {
            System.out.println("\n📋 PRUEBAS DE ODONTOGRAMA");
            System.out.println("----------------------");
            
            // TEST GET odontograma
            System.out.print("→ GET /api/patients/" + patientTestId + "/odontogram: ");
            boolean getSuccess = testGetEndpoint("/api/patients/" + patientTestId + "/odontogram");
            System.out.println(getSuccess ? "✅ ÉXITO" : "❌ FALLO");

            // TEST POST lesión en odontograma
            Map<String, Object> lesionData = new HashMap<>();
            lesionData.put("toothId", "11");
            lesionData.put("face", "VESTIBULAR");
            lesionData.put("lesionType", "CARIES");

            System.out.print("→ POST /api/patients/" + patientTestId + "/odontogram/lesions: ");
            boolean postSuccess = testPutEndpoint("/api/patients/" + patientTestId + "/odontogram/lesions", lesionData);
            System.out.println(postSuccess ? "✅ ÉXITO" : "❌ FALLO");
            
            // TEST GET odontograma después de agregar lesión
            System.out.print("→ GET /api/patients/" + patientTestId + "/odontogram (después de agregar lesión): ");
            boolean getSuccessAfter = testGetEndpoint("/api/patients/" + patientTestId + "/odontogram");
            System.out.println(getSuccessAfter ? "✅ ÉXITO" : "❌ FALLO");
        } else {
            System.out.println("→ Pruebas de odontograma: ⚠️ OMITIDAS (falta ID de paciente)");
        }
    }

    // Métodos auxiliares para realizar las operaciones de API

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
} 