package odoonto.infrastructure.testing;

import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.Sexo;
import odoonto.domain.model.valueobjects.EmailAddress;
import odoonto.domain.model.valueobjects.PhoneNumber;
import odoonto.domain.model.valueobjects.ToothFace;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.Specialty;

import odoonto.application.port.out.ReactiveDoctorRepository;
import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.application.port.out.ReactiveAppointmentRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;

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
    CommandLineRunner seedData(ReactiveDoctorRepository dr, ReactivePatientRepository pr, ReactiveAppointmentRepository ar) {
        return args -> {
            try {
                System.out.println("\n\n📊 GENERANDO DATOS DE EJEMPLO PARA LA BASE DE DATOS");
                System.out.println("==================================================");
                
                // Limpiar datos existentes primero
                System.out.println("\n🧹 Limpiando datos existentes antes de cargar nuevos datos de ejemplo...");
                
                try {
                    // Eliminar todas las entidades - como no existe deleteAll(), 
                    // obtenemos todas y eliminamos una por una con manejo de errores
                    
                    // Intentar eliminar todas las citas
                    try {
                        Mono<Void> deleteAppointments = ar.findAll()
                            .flatMap(appointment -> ar.deleteById(appointment.getId())
                                .onErrorResume(e -> {
                                    System.err.println("Error al eliminar cita " + appointment.getId() + ": " + e.getMessage());
                                    return Mono.empty();
                                }))
                            .then();
                        
                        deleteAppointments.block();
                        System.out.println("✅ Citas eliminadas correctamente");
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al limpiar citas: " + e.getMessage());
                        // Continuar con la siguiente operación
                    }
                    
                    // Intentar eliminar todos los pacientes
                    try {
                        Mono<Void> deletePatients = pr.findAll()
                            .flatMap(patient -> {
                                try {
                                    String patientId = patient.getId() != null ? patient.getId().getValue() : null;
                                    if (patientId == null || patientId.isEmpty()) {
                                        System.err.println("⚠️ Paciente con ID nulo o vacío encontrado, omitiendo eliminación");
                                        return Mono.empty();
                                    }
                                    return pr.deleteById(patientId)
                                        .onErrorResume(e -> {
                                            System.err.println("Error al eliminar paciente " + patientId + ": " + e.getMessage());
                                            return Mono.empty();
                                        });
                                } catch (Exception e) {
                                    System.err.println("Error al procesar paciente para eliminación: " + e.getMessage());
                                    return Mono.empty();
                                }
                            })
                            .then();
                        
                        deletePatients.block();
                        System.out.println("✅ Pacientes eliminados correctamente");
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al limpiar pacientes: " + e.getMessage());
                        // Continuar con la siguiente operación
                    }
                    
                    // Intentar eliminar todos los doctores
                    try {
                        Mono<Void> deleteDoctors = dr.findAll()
                            .flatMap(doctor -> {
                                try {
                                    String doctorId = doctor.getId();
                                    if (doctorId == null || doctorId.trim().isEmpty()) {
                                        System.err.println("⚠️ Doctor con ID nulo o vacío encontrado, omitiendo eliminación");
                                        return Mono.empty();
                                    }
                                    return dr.deleteById(doctorId)
                                        .onErrorResume(e -> {
                                            System.err.println("Error al eliminar doctor " + doctorId + ": " + e.getMessage());
                                            return Mono.empty();
                                        });
                                } catch (Exception e) {
                                    System.err.println("Error al procesar doctor para eliminación: " + e.getMessage());
                                    return Mono.empty();
                                }
                            })
                            .then();
                        
                        deleteDoctors.block();
                        System.out.println("✅ Doctores eliminados correctamente");
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al limpiar doctores: " + e.getMessage());
                        // Continuar con la siguiente operación
                    }
                    
                    System.out.println("✅ Base de datos limpiada completamente o con errores controlados");
                } catch (Exception e) {
                    System.err.println("⚠️ Error general al limpiar la base de datos: " + e.getMessage());
                    e.printStackTrace();
                    // Continuar con la carga de datos a pesar del error
                }
                
                System.out.println("🌱 Iniciando carga de datos de ejemplo...");
                
                // Crear 3 doctores con especialidades
                List<Doctor> doctores = new ArrayList<>();
                doctores.add(new Doctor("Doctor 1", Specialty.ODONTOLOGIA_GENERAL));
                doctores.add(new Doctor("Doctor 2", Specialty.ORTODONCIA));
                doctores.add(new Doctor("Doctor 3", Specialty.ENDODONCIA));
                
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
                    
                    // Género alternado
                    Sexo genero = i % 2 == 0 ? Sexo.MASCULINO : Sexo.FEMENINO;
                    
                    // Crear el paciente con constructor correcto
                    Patient patient = new Patient(
                            "Paciente " + i,
                            "Apellido " + i,
                            fechaNacimiento,
                            genero,
                            telefono,
                            email
                    );
                    
                    // Inicializar dientes en el odontograma con base en la edad del paciente
                    Odontogram odontogram = patient.getOdontogram();
                    initializeTeeth(odontogram, edad);
                    
                    // Añadir lesiones aleatorias al odontograma (1-3 lesiones)
                    addRandomLesions(odontogram, 1 + random.nextInt(3));
                    
                    pacientes.add(patient);
                }
                
                // Guardar los pacientes y obtener sus IDs
                List<String> patientIds = new ArrayList<>();
                System.out.println("\n👨‍👩‍👧 Creando pacientes de ejemplo...");
                
                for (Patient patient : pacientes) {
                    try {
                        Patient savedPatient = pr.save(patient).block();
                        if (savedPatient != null && savedPatient.getId() != null) {
                            String patientId = savedPatient.getId().getValue();
                            patientIds.add(patientId);
                            System.out.println("👨‍👩‍👧 Paciente creado: " + savedPatient.getNombre() + " " + savedPatient.getApellido() + 
                                              " - " + savedPatient.getEmail().getValue() + 
                                              " - Tel: " + savedPatient.getTelefono().getValue() +
                                              " (ID: " + patientId + ")");
                        } else {
                            System.err.println("⚠️ Error: El paciente " + patient.getNombre() + " " + patient.getApellido() + 
                                              " no se guardó correctamente");
                        }
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al guardar paciente " + patient.getNombre() + " " + patient.getApellido() + 
                                          ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                System.out.println("✅ " + patientIds.size() + " pacientes creados con éxito");
                
                // Crear citas para cada doctor durante 10 días a partir de una fecha futura
                // Cada doctor tendrá 3 citas por día
                List<Appointment> citas = new ArrayList<>();
                
                // Fecha de inicio: 3 días a partir de hoy
                LocalDate today = LocalDate.now();
                LocalDate startDate = today.plusDays(3);
                
                System.out.println("\n📅 Generando citas a partir del día " + startDate);
                
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
     * Inicializa los dientes en el odontograma según la edad del paciente
     */
    private void initializeTeeth(Odontogram odontogram, int edad) {
        // Arreglos con los IDs de dientes según FDI (Federación Dental Internacional)
        int[] permanentIds = {
                11,12,13,14,15,16,17,18,
                21,22,23,24,25,26,27,28,
                31,32,33,34,35,36,37,38,
                41,42,43,44,45,46,47,48
        };
        
        int[] temporaryIds = {
                51,52,53,54,55,
                61,62,63,64,65,
                71,72,73,74,75,
                81,82,83,84,85
        };
        
        // Añadir dientes según la edad:
        // - Niños pequeños (< 6 años): principalmente dientes temporales
        // - Niños (6-12 años): dentición mixta
        // - Adolescentes y adultos (>12 años): principalmente dientes permanentes
        
        if (edad < 6) {
            // Principalmente dientes temporales
            for (int id : temporaryIds) {
                // Añadir una lesión real (SANO) que permanecerá en el registro
                odontogram.addLesion(String.valueOf(id), 
                                    odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                    odoonto.domain.model.valueobjects.LesionType.SANO);
            }
            
            // Algunos dientes permanentes ya pueden estar erupcionando (primeros molares)
            odontogram.addLesion("16", odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                              odoonto.domain.model.valueobjects.LesionType.SANO);
            
            odontogram.addLesion("26", odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                              odoonto.domain.model.valueobjects.LesionType.SANO);
            
            odontogram.addLesion("36", odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                              odoonto.domain.model.valueobjects.LesionType.SANO);
            
            odontogram.addLesion("46", odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                              odoonto.domain.model.valueobjects.LesionType.SANO);
        } 
        else if (edad >= 6 && edad <= 12) {
            // Dentición mixta
            // Añadir todos los dientes temporales y parte de los permanentes según la edad
            
            for (int id : temporaryIds) {
                odontogram.addLesion(String.valueOf(id), 
                                   odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                   odoonto.domain.model.valueobjects.LesionType.SANO);
            }
            
            // Primeros molares permanentes y algunos incisivos
            for (int id : new int[] {16, 26, 36, 46, 11, 21, 31, 41, 12, 22, 32, 42}) {
                odontogram.addLesion(String.valueOf(id), 
                                   odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                   odoonto.domain.model.valueobjects.LesionType.SANO);
            }
        } 
        else {
            // Dentición permanente completa o casi completa
            // Para adultos mayores podríamos omitir algunos dientes (como las muelas del juicio)
            
            for (int id : permanentIds) {
                // Si es mayor de 25 años, omitir algunas muelas del juicio con probabilidad
                if ((id == 18 || id == 28 || id == 38 || id == 48) && edad > 25 && Math.random() > 0.7) {
                    // Omitir algunas muelas del juicio
                    continue;
                }
                
                odontogram.addLesion(String.valueOf(id), 
                                   odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                   odoonto.domain.model.valueobjects.LesionType.SANO);
            }
        }
        
        // Agregar registro sencillo para caras adicionales en algunos dientes para tener más diversidad
        // Lista corta de dientes a los que añadiremos más caras
        int[] sampleTeeth = {16, 26, 36, 46, 11, 21}; // Molares e incisivos centrales
        
        for (int id : sampleTeeth) {
            // Verificar si este diente está en el odontograma antes de añadir más lesiones
            try {
                // Intentar añadir lesiones en otras caras
                odontogram.addLesion(String.valueOf(id), 
                                  odoonto.domain.model.valueobjects.ToothFace.LINGUAL, 
                                  odoonto.domain.model.valueobjects.LesionType.SANO);
                
                odontogram.addLesion(String.valueOf(id), 
                                  odoonto.domain.model.valueobjects.ToothFace.OCLUSAL, 
                                  odoonto.domain.model.valueobjects.LesionType.SANO);
            } catch (Exception e) {
                // Ignora errores - el diente podría no estar presente o ya tener lesiones
            }
        }
    }
    
    /**
     * Añade un número determinado de lesiones aleatorias al odontograma
     */
    private void addRandomLesions(Odontogram odontogram, int count) {
        Random random = new Random();
        // Excluir LesionType.SANO para añadir solo lesiones reales
        LesionType[] lesionTypes = {LesionType.CARIES, LesionType.RESTAURACION, 
                                   LesionType.SELLANTE, LesionType.CORONA, 
                                   LesionType.AUSENTE, LesionType.ENDODONCIA};
        
        // Caras distintas a VESTIBULAR para no interferir con los registros base
        ToothFace[] faces = {ToothFace.DISTAL, ToothFace.MESIAL, 
                           ToothFace.OCLUSAL, ToothFace.INCISAL, 
                           ToothFace.PALATINA, ToothFace.LINGUAL};
        
        // Obtiene los IDs de dientes disponibles
        List<String> toothIds = new ArrayList<>(odontogram.getTeeth().keySet());
        
        // Verificar que haya dientes disponibles
        if (toothIds.isEmpty()) {
            System.err.println("⚠️ Odontograma sin dientes disponibles, no se pueden añadir lesiones");
            return; // Salir sin intentar añadir lesiones si no hay dientes
        }
        
        // Contador de lesiones realmente añadidas
        int addedLesions = 0;
        int attempts = 0;
        int maxAttempts = count * 3; // Permitir hasta 3 intentos por lesión deseada
        
        while (addedLesions < count && attempts < maxAttempts) {
            attempts++;
            
            // Selecciona un diente aleatorio
            String toothId = toothIds.get(random.nextInt(toothIds.size()));
            
            // Selecciona una cara aleatoria
            ToothFace face = faces[random.nextInt(faces.length)];
            
            // Selecciona un tipo de lesión aleatorio
            LesionType lesion = lesionTypes[random.nextInt(lesionTypes.length)];
            
            // Añade la lesión
            try {
                odontogram.addLesion(toothId, face, lesion);
                addedLesions++;
                System.out.println("✅ Lesión añadida: Diente " + toothId + ", Cara " + face + ", Tipo: " + lesion);
            } catch (Exception e) {
                // Ignora errores - pueden ser por duplicados u otras restricciones
            }
        }
        
        if (addedLesions > 0) {
            System.out.println("✅ " + addedLesions + " lesiones añadidas al odontograma");
        }
    }
}
