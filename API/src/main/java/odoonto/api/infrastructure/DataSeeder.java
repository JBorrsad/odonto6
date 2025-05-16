package odoonto.api.infrastructure;

import odoonto.api.domain.models.Doctor;
import odoonto.api.domain.models.Patient;
import odoonto.api.domain.models.Appointment;
import odoonto.api.domain.models.Odontogram;
import odoonto.api.domain.core.valueobjects.Sexo;
import odoonto.api.domain.core.valueobjects.EmailAddress;
import odoonto.api.domain.core.valueobjects.PhoneNumber;
import odoonto.api.domain.core.valueobjects.ToothFace;
import odoonto.api.domain.core.valueobjects.LesionType;
import odoonto.api.domain.repositories.DoctorRepository;
import odoonto.api.domain.repositories.PatientRepository;
import odoonto.api.domain.repositories.AppointmentRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner init(DoctorRepository dr, PatientRepository pr, AppointmentRepository ar) {
        return args -> {
            try {
                System.out.println("\n\n🧹 Limpiando datos existentes antes de cargar nuevos datos de prueba...");
                
                // Limpiar datos existentes primero
                Mono<Void> deleteAppointments = ar.deleteAll();
                Mono<Void> deletePatients = pr.deleteAll();
                Mono<Void> deleteDoctors = dr.deleteAll();
                
                // Esperar a que todas las operaciones de eliminación terminen
                Mono.when(deleteAppointments, deletePatients, deleteDoctors)
                    .doOnSuccess(v -> System.out.println("✅ Base de datos limpiada con éxito"))
                    .block();
                
                System.out.println("🌱 Iniciando carga de datos de prueba...");
                
                // Crear 5 doctores
                List<Doctor> doctores = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    Doctor doctor = new Doctor("Doctor " + i, "Especialidad " + i);
                    doctores.add(doctor);
                }
                
                // Guardar los doctores y obtener sus IDs
                List<String> doctorIds = new ArrayList<>();
                Flux.fromIterable(doctores)
                    .flatMap(dr::save)
                    .doOnNext(doctor -> {
                        doctorIds.add(doctor.getId());
                        System.out.println("Doctor creado: " + doctor.getNombreCompleto() + " (ID: " + doctor.getId() + ")");
                    })
                    .blockLast();
                
                System.out.println("👨‍⚕️ " + doctorIds.size() + " doctores creados con éxito");
                
                // Crear 5 pacientes
                List<Patient> pacientes = new ArrayList<>();
                Random random = new Random();
                
                for (int i = 1; i <= 5; i++) {
                    // Crear datos básicos del paciente
                    EmailAddress email = new EmailAddress("paciente" + i + "@ejemplo.com");
                    PhoneNumber telefono = new PhoneNumber("66612345" + i);
                    
                    // Fecha de nacimiento aleatoria entre 10 y 50 años atrás
                    int yearsAgo = 10 + random.nextInt(40);
                    LocalDate fechaNacimiento = LocalDate.now().minusYears(yearsAgo);
                    // Convertimos directamente a Instant para usar el constructor existente
                    Instant nacimiento = fechaNacimiento.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
                    
                    // Crear el paciente (el constructor interno convertirá el Instant a String)
                    Patient patient = new Patient(
                            "Paciente " + i,
                            "Apellido " + i,
                            nacimiento,
                            i % 2 == 0 ? Sexo.MASCULINO : Sexo.FEMENINO,
                            telefono,
                            email,
                            yearsAgo
                    );
                    
                    // Añadir lesiones al odontograma
                    Odontogram odontogram = patient.getOdontogram();
                    addRandomLesions(odontogram, 3 + random.nextInt(5)); // 3-7 lesiones aleatorias
                    
                    pacientes.add(patient);
                }
                
                // Guardar los pacientes y obtener sus IDs
                List<String> patientIds = new ArrayList<>();
                Flux.fromIterable(pacientes)
                    .flatMap(pr::save)
                    .doOnNext(patient -> {
                        patientIds.add(patient.getId());
                        System.out.println("Paciente creado: " + patient.getNombre() + " " + patient.getApellido() + " (ID: " + patient.getId() + ")");
                        System.out.println("  → Odontograma: " + patient.getOdontogram().getTeeth().size() + " dientes, con lesiones en algunas caras");
                    })
                    .blockLast();
                
                System.out.println("🧑‍🦰 " + patientIds.size() + " pacientes creados con éxito");
                
                // Verificar que los IDs no estén vacíos
                if (patientIds.isEmpty() || doctorIds.isEmpty()) {
                    throw new RuntimeException("No se pudieron crear pacientes o doctores");
                }
                
                // Crear citas entre los pacientes y doctores
                List<Appointment> citas = new ArrayList<>();
                LocalDateTime hoy = LocalDateTime.now();
                
                for (int i = 0; i < 5; i++) {
                    // Seleccionar paciente y doctor aleatoriamente
                    String patientId = patientIds.get(random.nextInt(patientIds.size()));
                    String doctorId = doctorIds.get(random.nextInt(doctorIds.size()));
                    
                    // Crear cita en los próximos 7 días, a hora entera o media hora
                    int diasFuturos = random.nextInt(7);
                    int hora = 9 + random.nextInt(8); // Entre 9 AM y 5 PM
                    int minutos = random.nextInt(2) * 30; // 0 o 30 minutos
                    int duracionSlots = 1 + random.nextInt(2); // 1 o 2 bloques (30-60 min)
                    
                    LocalDateTime inicio = hoy.plusDays(diasFuturos)
                                            .withHour(hora)
                                            .withMinute(minutos)
                                            .withSecond(0)
                                            .withNano(0);
                    
                    // El constructor de Appointment se encargará de la conversión a String
                    Appointment cita = new Appointment(patientId, doctorId, inicio, duracionSlots);
                    citas.add(cita);
                }
                
                // Guardar las citas
                Flux.fromIterable(citas)
                    .flatMap(ar::save)
                    .doOnNext(cita -> {
                        System.out.println("Cita creada: Doctor ID " + cita.getDoctorId() + 
                                          " con Paciente ID " + cita.getPatientId() + 
                                          " el " + cita.getStart());
                    })
                    .blockLast();
                
                System.out.println("📅 " + citas.size() + " citas creadas con éxito");
                System.out.println("✅ Datos de prueba cargados completamente");
                
                // Verificación final
                dr.count().doOnNext(count -> System.out.println("Total doctores en BD: " + count)).block();
                pr.count().doOnNext(count -> System.out.println("Total pacientes en BD: " + count)).block();
                ar.count().doOnNext(count -> System.out.println("Total citas en BD: " + count)).block();
                
            } catch (Exception e) {
                System.err.println("❌ Error al crear datos de prueba: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
    
    /**
     * Añade un número determinado de lesiones aleatorias al odontograma
     */
    private void addRandomLesions(Odontogram odontogram, int count) {
        Random random = new Random();
        LesionType[] lesionTypes = LesionType.values();
        ToothFace[] faces = ToothFace.values();
        
        // Obtiene los IDs de dientes disponibles
        List<String> toothIds = new ArrayList<>(odontogram.getTeeth().keySet());
        
        for (int i = 0; i < count; i++) {
            // Selecciona un diente aleatorio
            String toothId = toothIds.get(random.nextInt(toothIds.size()));
            
            // Selecciona una cara aleatoria
            ToothFace face = faces[random.nextInt(faces.length)];
            
            // Selecciona un tipo de lesión aleatorio
            LesionType lesion = lesionTypes[random.nextInt(lesionTypes.length)];
            
            // Añade la lesión
            odontogram.addLesion(toothId, face, lesion);
            System.out.println("  → Lesión añadida: Diente " + toothId + ", Cara " + face + ", Tipo " + lesion);
        }
    }
}
