// src/main/java/odoonto/api/infrastructure/EndpointTester.java
package odoonto.api.infrastructure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Component
public class EndpointTester implements CommandLineRunner {

    private final WebClient webClient;

    public EndpointTester(WebClient.Builder builder) {
        // Ajusta el puerto/host si tu API no está en localhost:8080
        this.webClient = builder.baseUrl("http://localhost:8080").build();
    }

    @Override
    public void run(String... args) {
        System.out.println("\n🚀 Lanzando tests de endpoints...");

        testDoctors();
        testPatients();
        testAppointments();
        // Elegimos un ID de paciente para probar
        testPatientOdontogram("1"); // Probar con un ID de paciente (probablemente no exista)

        System.out.println("✅ Tests finalizados.\n");
    }

    private void testDoctors() {
        System.out.print("→ GET /api/doctors … ");
        webClient.get().uri("/api/doctors")
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp -> {
                System.err.println("[ERROR HTTP " + resp.statusCode().value() + "]");
                return Mono.empty();
            })
            .bodyToFlux(Object.class)
            .collectList()
            .doOnNext(list -> System.out.println("OK (" + list.size() + " doctors)"))
            .onErrorResume(e -> {
                System.err.println("[ERROR] " + e.getMessage());
                return Mono.empty();
            })
            .block();
    }

    private void testPatients() {
        System.out.print("→ GET /api/patients … ");
        webClient.get().uri("/api/patients")
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp -> {
                System.err.println("[ERROR HTTP " + resp.statusCode().value() + "]");
                return Mono.empty();
            })
            .bodyToFlux(Object.class)
            .collectList()
            .doOnNext(list -> System.out.println("OK (" + list.size() + " patients)"))
            .onErrorResume(e -> {
                System.err.println("[ERROR] " + e.getMessage());
                return Mono.empty();
            })
            .block();
    }

    private void testAppointments() {
        System.out.print("→ GET /api/appointments … ");
        webClient.get().uri("/api/appointments")
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp -> {
                System.err.println("[ERROR HTTP " + resp.statusCode().value() + "]");
                return Mono.empty();
            })
            .bodyToFlux(Object.class)
            .collectList()
            .doOnNext(list -> System.out.println("OK (" + list.size() + " appointments)"))
            .onErrorResume(e -> {
                System.err.println("[ERROR] " + e.getMessage());
                return Mono.empty();
            })
            .block();
    }

    private void testPatientOdontogram(String patientId) {
        // Usamos un ID válido obtenido listando los pacientes
        System.out.print("→ Obteniendo ID de un paciente válido... ");
        String validPatientId = webClient.get().uri("/api/patients")
            .retrieve()
            .bodyToFlux(Map.class)
            .map(map -> map.get("id").toString())
            .blockFirst();
            
        if (validPatientId == null) {
            System.out.println("No se encontraron pacientes, saltando prueba de odontograma");
            return;
        }
        
        System.out.println("usando ID: " + validPatientId);
        
        System.out.print("→ GET /api/patients/" + validPatientId + "/odontogram … ");
        webClient.get().uri("/api/patients/{id}/odontogram", validPatientId)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp -> {
                System.err.println("[ERROR HTTP " + resp.statusCode().value() + "]");
                return Mono.empty();
            })
            .bodyToMono(Object.class)
            .doOnNext(obj -> System.out.println("OK (odontograma recuperado)"))
            .onErrorResume(e -> {
                System.err.println("[ERROR] " + e.getMessage());
                return Mono.empty();
            })
            .block();
    }
}
