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
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@Order(5) // Se ejecutará después de todos los endpoint testers (1-4)
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(DoctorRepository dr, PatientRepository pr, AppointmentRepository ar) {
        return args -> {
            try {
                System.out.println("\n\n📊 GENERANDO DATOS DE EJEMPLO PARA LA BASE DE DATOS");
                System.out.println("==================================================");
                
                // Limpiar datos existentes primero
                System.out.println("\n🧹 Limpiando datos existentes antes de cargar nuevos datos de ejemplo...");
                
                Mono<Void> deleteAppointments = ar.deleteAll();
                Mono<Void> deletePatients = pr.deleteAll();
                Mono<Void> deleteDoctors = dr.deleteAll();
                
                // Esperar a que todas las operaciones de eliminación terminen
                Mono.when(deleteAppointments, deletePatients, deleteDoctors)
                    .doOnSuccess(v -> System.out.println("✅ Base de datos limpiada con éxito"))
                    .block();
                
                System.out.println("🌱 Iniciando carga de datos de ejemplo...");
                
                // Crear 3 doctores con especialidades
                List<Doctor> doctores = new ArrayList<>();
                doctores.add(new Doctor("Doctor 1", "Especialidad 1"));
                doctores.add(new Doctor("Doctor 2", "Especialidad 2"));
                doctores.add(new Doctor("Doctor 3", "Especialidad 3"));
                
                // Guardar los doctores y obtener sus IDs
                List<String> doctorIds = new ArrayList<>();
                Flux.fromIterable(doctores)
                    .flatMap(dr::save)
                    .doOnNext(doctor -> {
                        doctorIds.add(doctor.getId());
                        System.out.println("👨‍⚕️ Doctor creado: " + doctor.getNombreCompleto() + 
                                          " - " + doctor.getEspecialidad() + 
                                          " (ID: " + doctor.getId() + ")");
                    })
                    .blockLast();
                
                System.out.println("✅ " + doctorIds.size() + " doctores creados con éxito");
                
                // Crear 10 pacientes
                List<Patient> pacientes = new ArrayList<>();
                Random random = new Random();
                
                for (int i = 1; i <= 10; i++) {
                    // Crear datos básicos del paciente
                    EmailAddress email = new EmailAddress("paciente" + i + "@ejemplo.com");
                    PhoneNumber telefono = new PhoneNumber("66612345" + (i < 10 ? "0" + i : i));
                    
                    // Edad aleatoria entre 18 y 70 años
                    int edad = 18 + random.nextInt(52);
                    LocalDate fechaNacimiento = LocalDate.now().minusYears(edad);
                    Instant nacimiento = fechaNacimiento.atStartOfDay().toInstant(ZoneOffset.UTC);
                    
                    // Género alternado
                    Sexo genero = i % 2 == 0 ? Sexo.MASCULINO : Sexo.FEMENINO;
                    
                    // Crear el paciente
                    Patient patient = new Patient(
                            "Paciente " + i,
                            "Apellido " + i,
                            nacimiento,
                            genero,
                            telefono,
                            email,
                            edad
                    );
                    
                    // Añadir lesiones aleatorias al odontograma (1-3 lesiones)
                    Odontogram odontogram = patient.getOdontogram();
                    addRandomLesions(odontogram, 1 + random.nextInt(3));
                    
                    pacientes.add(patient);
                }
                
                // Guardar los pacientes y obtener sus IDs
                List<String> patientIds = new ArrayList<>();
                Flux.fromIterable(pacientes)
                    .flatMap(pr::save)
                    .doOnNext(patient -> {
                        patientIds.add(patient.getId());
                        System.out.println("👨‍👩‍👧 Paciente creado: " + patient.getNombre() + " " + patient.getApellido() + 
                                          " - " + patient.getEmail().getEmailAddress() + 
                                          " - Tel: " + patient.getTelefono().getNumero() +
                                          " (ID: " + patient.getId() + ")");
                    })
                    .blockLast();
                
                System.out.println("✅ " + patientIds.size() + " pacientes creados con éxito");
                
                // Crear citas para cada doctor durante 10 días a partir del 17/05/2025
                // Cada doctor tendrá 3 citas por día
                List<Appointment> citas = new ArrayList<>();
                
                // Fecha de inicio: 17/05/2025
                LocalDate startDate = LocalDate.of(2025, 5, 17);
                
                for (int dia = 0; dia < 10; dia++) {
                    LocalDate currentDate = startDate.plusDays(dia);
                    System.out.println("\n📅 Generando citas para el día " + currentDate);
                    
                    // Para cada doctor, crear 3 citas en este día
                    for (String doctorId : doctorIds) {
                        // Horario de trabajo: 8:00 a 20:00
                        // Dividimos en slots de 30 minutos (24 slots posibles)
                        Set<Integer> usedTimeSlots = new HashSet<>();
                        
                        // Crear 3 citas para este doctor en este día
                        for (int citaNum = 1; citaNum <= 3; citaNum++) {
                            // Seleccionar un paciente aleatorio
                            String patientId = patientIds.get(random.nextInt(patientIds.size()));
                            
                            // Seleccionar un horario aleatorio que no se solape
                            int timeSlot;
                            do {
                                // 8:00 AM = slot 0, 8:30 AM = slot 1, etc. hasta 7:30 PM = slot 23
                                timeSlot = random.nextInt(24); // 0-23
                            } while (usedTimeSlots.contains(timeSlot) || 
                                    usedTimeSlots.contains(timeSlot-1) || 
                                    usedTimeSlots.contains(timeSlot+1));
                            
                            // Marcar este slot y los adyacentes como usados para evitar solapamientos
                            usedTimeSlots.add(timeSlot);
                            
                            // Convertir el slot a hora y minutos
                            int hour = 8 + (timeSlot / 2);
                            int minute = (timeSlot % 2) * 30;
                            
                            // Crear el momento de inicio
                            LocalDateTime inicio = LocalDateTime.of(
                                currentDate, 
                                LocalTime.of(hour, minute, 0)
                            );
                            
                            // Duración aleatoria: 1 o 2 slots (30 o 60 minutos)
                            int duracionSlots = 1 + random.nextInt(2);
                            
                            // Si la duración es 2 slots, asegurarse de que el siguiente slot esté libre
                            if (duracionSlots == 2) {
                                usedTimeSlots.add(timeSlot + 1);
                            }
                            
                            // Crear la cita
                            Appointment cita = new Appointment(patientId, doctorId, inicio, duracionSlots);
                            citas.add(cita);
                            
                            System.out.println("🕒 Cita creada: " + 
                                              "Doctor ID " + doctorId + 
                                              " con Paciente ID " + patientId + 
                                              " el " + inicio + 
                                              " (" + (duracionSlots * 30) + " min)");
                        }
                    }
                }
                
                // Guardar todas las citas
                Flux.fromIterable(citas)
                    .flatMap(ar::save)
                    .blockLast();
                
                // Mostrar resumen de datos creados
                System.out.println("\n📊 RESUMEN DE DATOS CREADOS");
                System.out.println("------------------------");
                System.out.println("👨‍⚕️ Doctores: " + doctorIds.size());
                System.out.println("👨‍👩‍👧 Pacientes: " + patientIds.size());
                System.out.println("📅 Citas: " + citas.size());
                System.out.println("✅ Datos de ejemplo cargados completamente");
                
            } catch (Exception e) {
                System.err.println("❌ Error al crear datos de ejemplo: " + e.getMessage());
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
        }
    }
}
