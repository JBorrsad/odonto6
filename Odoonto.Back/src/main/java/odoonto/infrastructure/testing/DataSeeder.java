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
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.domain.model.valueobjects.OdontogramId;

import odoonto.application.port.out.ReactiveDoctorRepository;
import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.application.port.out.ReactiveAppointmentRepository;
import odoonto.application.port.out.ReactiveOdontogramRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Importar Firestore para limpieza directa
import com.google.cloud.firestore.Firestore;

@Configuration
@Order(5) // Se ejecutará después de todos los endpoint testers (1-4)
public class DataSeeder {

    private final Firestore firestore;

    // Constructor para inyectar Firestore
    public DataSeeder(Firestore firestore) {
        this.firestore = firestore;
    }

    @Bean
    CommandLineRunner seedData(ReactiveDoctorRepository dr, ReactivePatientRepository pr, ReactiveAppointmentRepository ar, ReactiveOdontogramRepository or) {
        return args -> {
            try {
                System.out.println("\n\n📊 GENERANDO DATOS DE EJEMPLO PARA LA BASE DE DATOS");
                System.out.println("==================================================");
                
                // Limpiar datos existentes primero
                System.out.println("\n🧹 Limpiando datos existentes antes de cargar nuevos datos de ejemplo...");
                
                // Usar limpieza directa de Firestore para evitar errores de mapeo
                try {
                    cleanFirestoreCollections();
                    System.out.println("✅ Base de datos limpiada completamente");
                } catch (Exception e) {
                    System.err.println("⚠️ Error al limpiar la base de datos: " + e.getMessage());
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
                try {
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
                } catch (Exception e) {
                    System.err.println("❌ Error al crear doctores: " + e.getMessage());
                    e.printStackTrace();
                    return; // No continuar si no hay doctores
                }
                
                // Verificar que se crearon doctores
                if (doctorIds.isEmpty()) {
                    System.err.println("❌ No se pudieron crear doctores. Abortando creación de datos.");
                    return;
                }
                
                // Crear 10 pacientes
                List<Patient> pacientes = new ArrayList<>();
                Random random = new Random();
                
                for (int i = 1; i <= 10; i++) {
                    try {
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
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al crear paciente " + i + ": " + e.getMessage());
                    }
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
                            
                            // Guardar también el odontograma del paciente por separado
                            try {
                                Odontogram odontogram = savedPatient.getOdontogram();
                                if (odontogram != null) {
                                    // CORRECCIÓN: Asignar el ID del odontograma basado en el ID del paciente
                                    // si no tiene ID asignado
                                    if (odontogram.getId() == null) {
                                        PatientId savedPatientId = savedPatient.getId();
                                        if (savedPatientId != null) {
                                            OdontogramId odontogramId = OdontogramId.fromPatientId(savedPatientId);
                                            odontogram.setId(odontogramId);
                                        } else {
                                            System.err.println("⚠️ Error: El paciente guardado no tiene ID asignado");
                                            continue; // Saltar al siguiente paciente
                                        }
                                    }
                                    
                                    Odontogram savedOdontogram = or.save(odontogram).block();
                                    if (savedOdontogram != null) {
                                        System.out.println("🦷 Odontograma guardado para paciente " + patientId + 
                                                          " con " + odontogram.getTeeth().size() + " dientes registrados");
                                    } else {
                                        System.err.println("⚠️ Error: El odontograma del paciente " + patientId + " no se guardó correctamente");
                                    }
                                }
                            } catch (Exception odontogramError) {
                                System.err.println("⚠️ Error al guardar odontograma del paciente " + patientId + ": " + odontogramError.getMessage());
                                // No interrumpir la creación del paciente si falla el odontograma
                            }
                            
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
                
                // Verificar que se crearon pacientes
                if (patientIds.isEmpty()) {
                    System.err.println("❌ No se pudieron crear pacientes. Abortando creación de citas.");
                    return;
                }
                
                // Crear citas para cada doctor durante 10 días a partir de una fecha futura
                // Cada doctor tendrá 3 citas por día
                List<Appointment> citas = new ArrayList<>();
                
                // Fecha de inicio: 5 días a partir de hoy para asegurar que no hay problemas de timing
                LocalDate today = LocalDate.now();
                LocalDate startDate = findNextWorkingDay(today.plusDays(5));
                
                System.out.println("\n📅 Generando citas a partir del día " + startDate);
                
                // Generar citas para 10 días laborables (no consecutivos, solo laborables)
                LocalDate currentDate = startDate;
                int diasLaborablesGenerados = 0;
                
                while (diasLaborablesGenerados < 10) {
                    // Solo procesar si es día laborable
                    if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && 
                        currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                        
                        System.out.println("\n📅 Generando citas para el día " + currentDate + " (" + currentDate.getDayOfWeek() + ")");
                        
                        // Para cada doctor, crear 3 citas en este día
                        for (String doctorId : doctorIds) {
                            // Horario de trabajo: 8:00 a 17:00 (evitar horarios muy tarde)
                            // Dividimos en slots de 30 minutos (18 slots posibles)
                            Set<Integer> usedTimeSlots = new HashSet<>();
                            
                            // Crear 3 citas para este doctor en este día
                            for (int citaNum = 1; citaNum <= 3; citaNum++) {
                                try {
                                    // Seleccionar un paciente aleatorio
                                    String patientId = patientIds.get(random.nextInt(patientIds.size()));
                                    
                                    // Seleccionar un horario aleatorio que no se solape
                                    int timeSlot;
                                    int attempts = 0;
                                    do {
                                        // 8:00 AM = slot 0, 8:30 AM = slot 1, etc. hasta 5:00 PM = slot 17
                                        timeSlot = random.nextInt(18); // 0-17
                                        attempts++;
                                        if (attempts > 20) break; // Evitar bucles infinitos
                                    } while (usedTimeSlots.contains(timeSlot) || 
                                            usedTimeSlots.contains(timeSlot-1) || 
                                            usedTimeSlots.contains(timeSlot+1));
                                    
                                    if (attempts > 20) {
                                        System.err.println("⚠️ No se pudo encontrar un slot disponible para la cita " + citaNum + " del doctor " + doctorId);
                                        continue;
                                    }
                                    
                                    // Marcar este slot y los adyacentes como usados para evitar solapamientos
                                    usedTimeSlots.add(timeSlot);
                                    
                                    // Convertir el slot a hora y minutos
                                    int hour = 8 + (timeSlot / 2);
                                    int minute = (timeSlot % 2) * 30;
                                    
                                    // Crear el momento de inicio con un buffer de tiempo para evitar problemas de validación
                                    LocalDateTime inicio = LocalDateTime.of(
                                        currentDate, 
                                        LocalTime.of(hour, minute, 0)
                                    );
                                    
                                    // Duración fija de 1 slot (30 minutos) para simplificar
                                    int duracionSlots = 1;
                                    
                                    // Verificar que la fecha sea válida antes de crear la cita
                                    if (inicio.isBefore(LocalDateTime.now().plusHours(1))) {
                                        System.err.println("⚠️ Fecha de cita muy cercana al presente, omitiendo: " + inicio);
                                        continue;
                                    }
                                    
                                    // Crear la cita
                                    Appointment cita = new Appointment(patientId, doctorId, inicio, duracionSlots);
                                    citas.add(cita);
                                    
                                    System.out.println("🕒 Cita creada: " + 
                                                      "Doctor ID " + doctorId + 
                                                      " con Paciente ID " + patientId + 
                                                      " el " + inicio + 
                                                      " (" + (duracionSlots * 30) + " min)");
                                } catch (Exception e) {
                                    System.err.println("⚠️ Error al crear cita " + citaNum + " para doctor " + doctorId + ": " + e.getMessage());
                                }
                            }
                        }
                        
                        // Incrementar el contador de días laborables procesados
                        diasLaborablesGenerados++;
                    }
                    
                    // Avanzar al siguiente día
                    currentDate = currentDate.plusDays(1);
                }
                
                // Guardar todas las citas con manejo de errores individual
                System.out.println("\n💾 Guardando " + citas.size() + " citas en la base de datos...");
                int citasGuardadas = 0;
                
                for (Appointment cita : citas) {
                    try {
                        Appointment savedAppointment = ar.save(cita).block();
                        if (savedAppointment != null) {
                            citasGuardadas++;
                        }
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al guardar cita: " + e.getMessage());
                    }
                }
                
                // Mostrar resumen de datos creados
                System.out.println("\n📊 RESUMEN DE DATOS CREADOS");
                System.out.println("------------------------");
                System.out.println("👨‍⚕️ Doctores: " + doctorIds.size());
                System.out.println("👨‍👩‍👧 Pacientes: " + patientIds.size());
                System.out.println("🦷 Odontogramas: " + patientIds.size() + " (uno por paciente)");
                System.out.println("📅 Citas programadas: " + citas.size());
                System.out.println("📅 Citas guardadas: " + citasGuardadas);
                System.out.println("✅ Datos de ejemplo cargados completamente");
                System.out.println("\n💡 Los odontogramas contienen lesiones de ejemplo en diferentes dientes y caras.");
                System.out.println("💡 Puedes verificar los datos en Firebase Console o usando el test de la API.");
                
            } catch (Exception e) {
                System.err.println("❌ Error al crear datos de ejemplo: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
    
    /**
     * Encuentra el siguiente día laborable a partir de una fecha dada
     */
    private LocalDate findNextWorkingDay(LocalDate date) {
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }
        return date;
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
                try {
                    // Añadir una lesión real (SANO) que permanecerá en el registro
                    odontogram.addLesion(String.valueOf(id), 
                                        odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                        odoonto.domain.model.valueobjects.LesionType.SANO);
                } catch (Exception e) {
                    // Ignora errores en dientes específicos
                }
            }
            
            // Algunos dientes permanentes ya pueden estar erupcionando (primeros molares)
            try {
                odontogram.addLesion("16", odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                  odoonto.domain.model.valueobjects.LesionType.SANO);
                
                odontogram.addLesion("26", odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                  odoonto.domain.model.valueobjects.LesionType.SANO);
                
                odontogram.addLesion("36", odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                  odoonto.domain.model.valueobjects.LesionType.SANO);
                
                odontogram.addLesion("46", odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                  odoonto.domain.model.valueobjects.LesionType.SANO);
            } catch (Exception e) {
                // Ignora errores en dientes específicos
            }
        } 
        else if (edad >= 6 && edad <= 12) {
            // Dentición mixta
            // Añadir todos los dientes temporales y parte de los permanentes según la edad
            
            for (int id : temporaryIds) {
                try {
                    odontogram.addLesion(String.valueOf(id), 
                                       odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                       odoonto.domain.model.valueobjects.LesionType.SANO);
                } catch (Exception e) {
                    // Ignora errores en dientes específicos
                }
            }
            
            // Primeros molares permanentes y algunos incisivos
            for (int id : new int[] {16, 26, 36, 46, 11, 21, 31, 41, 12, 22, 32, 42}) {
                try {
                    odontogram.addLesion(String.valueOf(id), 
                                       odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                       odoonto.domain.model.valueobjects.LesionType.SANO);
                } catch (Exception e) {
                    // Ignora errores en dientes específicos
                }
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
                
                try {
                    odontogram.addLesion(String.valueOf(id), 
                                       odoonto.domain.model.valueobjects.ToothFace.VESTIBULAR, 
                                       odoonto.domain.model.valueobjects.LesionType.SANO);
                } catch (Exception e) {
                    // Ignora errores en dientes específicos
                }
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
            
            try {
                // Seleccionar diente aleatorio
                String toothId = toothIds.get(random.nextInt(toothIds.size()));
                
                // Seleccionar cara aleatoria
                ToothFace face = faces[random.nextInt(faces.length)];
                
                // Seleccionar tipo de lesión aleatorio
                LesionType lesionType = lesionTypes[random.nextInt(lesionTypes.length)];
                
                // Intentar añadir la lesión
                odontogram.addLesion(toothId, face, lesionType);
                addedLesions++;
                
            } catch (Exception e) {
                // Si falla (por ejemplo, la lesión ya existe en esa cara), intentar con otra combinación
                // No imprimir error aquí porque es normal que algunas combinaciones fallen
            }
        }
        
        if (addedLesions < count) {
            System.out.println("ℹ️ Se añadieron " + addedLesions + " de " + count + " lesiones solicitadas al odontograma");
        }
    }

    /**
     * Limpia todas las colecciones de Firestore de forma directa
     */
    private void cleanFirestoreCollections() {
        try {
            // Limpiar colección de citas
            System.out.println("🗑️ Limpiando colección 'appointments'...");
            firestore.collection("appointments").listDocuments().forEach(docRef -> {
                try {
                    docRef.delete().get();
                } catch (Exception e) {
                    System.err.println("Error al eliminar documento de cita: " + e.getMessage());
                }
            });
            
            // Limpiar colección de pacientes
            System.out.println("🗑️ Limpiando colección 'patients'...");
            firestore.collection("patients").listDocuments().forEach(docRef -> {
                try {
                    docRef.delete().get();
                } catch (Exception e) {
                    System.err.println("Error al eliminar documento de paciente: " + e.getMessage());
                }
            });
            
            // Limpiar colección de doctores  
            System.out.println("🗑️ Limpiando colección 'doctors'...");
            firestore.collection("doctors").listDocuments().forEach(docRef -> {
                try {
                    docRef.delete().get();
                } catch (Exception e) {
                    System.err.println("Error al eliminar documento de doctor: " + e.getMessage());
                }
            });
            
            // Limpiar colección de odontogramas si existe
            System.out.println("🗑️ Limpiando colección 'odontograms'...");
            firestore.collection("odontograms").listDocuments().forEach(docRef -> {
                try {
                    docRef.delete().get();
                } catch (Exception e) {
                    System.err.println("Error al eliminar documento de odontograma: " + e.getMessage());
                }
            });
            
            // Limpiar colección de historiales médicos si existe
            System.out.println("🗑️ Limpiando colección 'medicalrecords'...");
            firestore.collection("medicalrecords").listDocuments().forEach(docRef -> {
                try {
                    docRef.delete().get();
                } catch (Exception e) {
                    System.err.println("Error al eliminar documento de historial médico: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error general al limpiar Firestore: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
