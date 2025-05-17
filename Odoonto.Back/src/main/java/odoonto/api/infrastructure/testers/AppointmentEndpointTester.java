package odoonto.api.infrastructure.testers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Order(4) // Se ejecuta cuarto
public class AppointmentEndpointTester implements CommandLineRunner {

    private final WebClient webClient;
    private final PatientEndpointTester patientTester;
    private final DoctorEndpointTester doctorTester;
    
    private String appointmentTestId;

    @Autowired
    public AppointmentEndpointTester(WebClient.Builder builder, 
                                     PatientEndpointTester patientTester,
                                     DoctorEndpointTester doctorTester) {
        this.webClient = builder.baseUrl("http://localhost:8081").build();
        this.patientTester = patientTester;
        this.doctorTester = doctorTester;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n🧪 PRUEBAS DE ENDPOINTS DE CITAS");
        System.out.println("=============================");

        testAppointmentsCRUD();
        
        // Limpiar los datos de prueba creados
        cleanupTestData();
    }

    private void testAppointmentsCRUD() {
        System.out.println("\n📋 PRUEBAS DE CITAS");
        System.out.println("----------------");

        // TEST GET (Listar)
        System.out.print("→ GET /api/appointments: ");
        boolean getSuccess = testGetEndpoint("/api/appointments");
        System.out.println(getSuccess ? "✅ ÉXITO" : "❌ FALLO");

        String patientTestId = patientTester.getPatientTestId();
        String doctorTestId = doctorTester.getDoctorTestId();
        
        // Solo probamos crear cita si tenemos doctor y paciente
        if (doctorTestId != null && patientTestId != null) {
            // TEST POST (Crear)
            Map<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("doctorId", doctorTestId);
            appointmentData.put("patientId", patientTestId);
            
            // Cita para mañana a las 10:00
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
            appointmentData.put("start", tomorrow.format(DateTimeFormatter.ISO_DATE_TIME));
            appointmentData.put("durationSlots", 2);

            System.out.print("→ POST /api/appointments: ");
            appointmentTestId = testPostEndpoint("/api/appointments", appointmentData);
            boolean postSuccess = appointmentTestId != null;
            System.out.println(postSuccess ? "✅ ÉXITO (ID: " + appointmentTestId + ")" : "❌ FALLO");

            // TEST PUT (Actualizar) - Solo si el POST funcionó
            if (postSuccess) {
                Map<String, Object> updateData = new HashMap<>();
                LocalDateTime newTime = tomorrow.withHour(11);
                updateData.put("start", newTime.format(DateTimeFormatter.ISO_DATE_TIME));
                updateData.put("durationSlots", 1);

                System.out.print("→ PUT /api/appointments/" + appointmentTestId + ": ");
                boolean putSuccess = testPutEndpoint("/api/appointments/" + appointmentTestId, updateData);
                System.out.println(putSuccess ? "✅ ÉXITO" : "❌ FALLO");

                // TEST GET by ID
                System.out.print("→ GET /api/appointments/" + appointmentTestId + ": ");
                boolean getByIdSuccess = testGetEndpoint("/api/appointments/" + appointmentTestId);
                System.out.println(getByIdSuccess ? "✅ ÉXITO" : "❌ FALLO");
                
                // No borramos la cita para probarlo más adelante
            }
        } else {
            System.out.println("→ POST /api/appointments: ⚠️ OMITIDO (faltan IDs de doctor o paciente)");
        }
    }
    
    // Realizar operaciones de limpieza y eliminar datos de prueba
    private void cleanupTestData() {
        System.out.println("\n🧹 LIMPIEZA DE DATOS DE PRUEBA");
        System.out.println("---------------------------");

        // Eliminar cita de prueba
        if (appointmentTestId != null) {
            System.out.print("→ DELETE /api/appointments/" + appointmentTestId + ": ");
            boolean deleteSuccess = testDeleteEndpoint("/api/appointments/" + appointmentTestId);
            System.out.println(deleteSuccess ? "✅ ÉXITO" : "❌ FALLO");
        }

        // Eliminar doctor de prueba
        String doctorId = doctorTester.getDoctorTestId();
        if (doctorId != null) {
            System.out.print("→ DELETE /api/doctors/" + doctorId + ": ");
            boolean deleteSuccess = testDeleteEndpoint("/api/doctors/" + doctorId);
            System.out.println(deleteSuccess ? "✅ ÉXITO" : "❌ FALLO");
        }
        
        // El paciente ya fue eliminado en PatientEndpointTester
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