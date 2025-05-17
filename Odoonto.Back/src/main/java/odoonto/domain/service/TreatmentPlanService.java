package odoonto.domain.service;

import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Lesion;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.entities.Treatment;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.TreatmentType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para planificación de tratamientos dentales.
 * Trabaja con el odontograma, que es un objeto de valor dentro del agregado Patient.
 */
public class TreatmentPlanService {
    
    /**
     * Genera un plan de tratamiento priorizado basado en el odontograma
     * @param odontogram Odontograma del paciente (objeto de valor contenido en el agregado Patient)
     * @return Lista de tratamientos propuestos ordenados por prioridad
     */
    public List<ProposedTreatment> generateTreatmentPlan(Odontogram odontogram) {
        List<ProposedTreatment> treatmentPlan = new ArrayList<>();
        
        // 1. Recopilar todas las lesiones que requieren tratamiento
        for (Tooth tooth : odontogram.getTeeth()) {
            for (Lesion lesion : tooth.getLesions()) {
                // Determinar el tratamiento adecuado para la lesión
                TreatmentType recommendedType = recommendTreatmentForLesion(lesion.getType());
                if (recommendedType != null) {
                    // Crear propuesta de tratamiento
                    ProposedTreatment proposal = new ProposedTreatment(
                            tooth.getNumber(),
                            lesion.getFace(),
                            lesion.getType(),
                            recommendedType,
                            calculatePriority(lesion.getType())
                    );
                    treatmentPlan.add(proposal);
                }
            }
        }
        
        // 2. Ordenar tratamientos por prioridad (mayor primero)
        Collections.sort(treatmentPlan, Comparator.comparing(ProposedTreatment::getPriority).reversed());
        
        return treatmentPlan;
    }
    
    /**
     * Genera un plan de tratamiento por fases
     * @param odontogram Odontograma del paciente
     * @return Lista de fases de tratamiento
     */
    public List<TreatmentPhase> generatePhasedTreatmentPlan(Odontogram odontogram) {
        List<TreatmentPhase> phases = new ArrayList<>();
        
        // Fase 1: Urgencias - Alivio del dolor y tratamientos de emergencia
        TreatmentPhase emergencyPhase = new TreatmentPhase("Fase de Urgencias", 1);
        
        // Fase 2: Control de infección - Eliminar caries y focos infecciosos
        TreatmentPhase infectionControlPhase = new TreatmentPhase("Control de Infección", 2);
        
        // Fase 3: Rehabilitación - Restauraciones definitivas
        TreatmentPhase rehabilitationPhase = new TreatmentPhase("Fase de Rehabilitación", 3);
        
        // Fase 4: Mantenimiento - Controles y prevención
        TreatmentPhase maintenancePhase = new TreatmentPhase("Fase de Mantenimiento", 4);
        
        // Asignar tratamientos a las fases correspondientes
        for (Tooth tooth : odontogram.getTeeth()) {
            for (Lesion lesion : tooth.getLesions()) {
                TreatmentType recommendedType = recommendTreatmentForLesion(lesion.getType());
                if (recommendedType != null) {
                    ProposedTreatment treatment = new ProposedTreatment(
                            tooth.getNumber(),
                            lesion.getFace(),
                            lesion.getType(),
                            recommendedType,
                            calculatePriority(lesion.getType())
                    );
                    
                    // Asignar a la fase correspondiente
                    assignToPhase(treatment, emergencyPhase, infectionControlPhase, 
                                 rehabilitationPhase, maintenancePhase);
                }
            }
        }
        
        // Añadir fases que contengan tratamientos
        if (!emergencyPhase.getTreatments().isEmpty()) {
            phases.add(emergencyPhase);
        }
        if (!infectionControlPhase.getTreatments().isEmpty()) {
            phases.add(infectionControlPhase);
        }
        if (!rehabilitationPhase.getTreatments().isEmpty()) {
            phases.add(rehabilitationPhase);
        }
        if (!maintenancePhase.getTreatments().isEmpty()) {
            phases.add(maintenancePhase);
        }
        
        return phases;
    }
    
    /**
     * Asigna un tratamiento propuesto a la fase correspondiente
     */
    private void assignToPhase(ProposedTreatment treatment, 
                              TreatmentPhase emergencyPhase,
                              TreatmentPhase infectionControlPhase,
                              TreatmentPhase rehabilitationPhase,
                              TreatmentPhase maintenancePhase) {
        // Asignar según el tipo de lesión y tratamiento
        LesionType lesionType = treatment.getLesionType();
        TreatmentType treatmentType = treatment.getTreatmentType();
        
        if (lesionType == LesionType.PULPITIS || 
            lesionType == LesionType.ABSCESO || 
            lesionType == LesionType.FRACTURA) {
            emergencyPhase.addTreatment(treatment);
        } else if (lesionType == LesionType.CARIES || 
                  lesionType == LesionType.CARIES_PROFUNDA) {
            infectionControlPhase.addTreatment(treatment);
        } else if (treatmentType.getCategoria() == TreatmentType.Categoria.RESTAURADOR || 
                  treatmentType.getCategoria() == TreatmentType.Categoria.PROTESICO) {
            rehabilitationPhase.addTreatment(treatment);
        } else {
            maintenancePhase.addTreatment(treatment);
        }
    }
    
    /**
     * Recomienda un tipo de tratamiento basado en el tipo de lesión
     * @param lesionType Tipo de lesión
     * @return Tipo de tratamiento recomendado o null si no hay recomendación
     */
    private TreatmentType recommendTreatmentForLesion(LesionType lesionType) {
        switch (lesionType) {
            case CARIES:
                return TreatmentType.OBTURACION_RESINA;
            case CARIES_PROFUNDA:
                return TreatmentType.ENDODONCIA_UNIRRADICULAR;
            case PULPITIS:
                return TreatmentType.ENDODONCIA_BIRRADICULAR;
            case ABSCESO:
                return TreatmentType.ENDODONCIA_MULTIRRADICULAR;
            case FRACTURA:
                return TreatmentType.RECONSTRUCCION;
            case DESGASTE:
                return TreatmentType.CORONA;
            default:
                return null;
        }
    }
    
    /**
     * Calcula la prioridad de tratamiento según el tipo de lesión
     * @param lesionType Tipo de lesión
     * @return Nivel de prioridad (1-10, donde 10 es la mayor prioridad)
     */
    private int calculatePriority(LesionType lesionType) {
        switch (lesionType) {
            case PULPITIS:
            case ABSCESO:
                return 10; // Máxima prioridad - Requiere atención inmediata
            case FRACTURA:
                return 9;
            case CARIES_PROFUNDA:
                return 8;
            case CARIES:
                return 7;
            case DESGASTE:
                return 5;
            case TINCION:
                return 3;
            default:
                return 1;
        }
    }
    
    /**
     * Clase que representa un tratamiento propuesto como parte de un plan
     */
    public static class ProposedTreatment {
        private final int toothNumber;
        private final String face;
        private final LesionType lesionType;
        private final TreatmentType treatmentType;
        private final int priority;
        
        public ProposedTreatment(int toothNumber, String face, LesionType lesionType, 
                                TreatmentType treatmentType, int priority) {
            this.toothNumber = toothNumber;
            this.face = face;
            this.lesionType = lesionType;
            this.treatmentType = treatmentType;
            this.priority = priority;
        }
        
        // Getters
        public int getToothNumber() { return toothNumber; }
        public String getFace() { return face; }
        public LesionType getLesionType() { return lesionType; }
        public TreatmentType getTreatmentType() { return treatmentType; }
        public int getPriority() { return priority; }
        
        @Override
        public String toString() {
            return "Diente " + toothNumber + " (" + face + "): " + 
                   treatmentType.getNombre() + " para " + lesionType.getNombre();
        }
    }
    
    /**
     * Clase que representa una fase de tratamiento
     */
    public static class TreatmentPhase {
        private final String name;
        private final int phaseNumber;
        private final List<ProposedTreatment> treatments = new ArrayList<>();
        private LocalDate proposedStartDate;
        
        public TreatmentPhase(String name, int phaseNumber) {
            this.name = name;
            this.phaseNumber = phaseNumber;
        }
        
        public void addTreatment(ProposedTreatment treatment) {
            treatments.add(treatment);
        }
        
        // Getters
        public String getName() { return name; }
        public int getPhaseNumber() { return phaseNumber; }
        public List<ProposedTreatment> getTreatments() { return treatments; }
        public LocalDate getProposedStartDate() { return proposedStartDate; }
        
        public void setProposedStartDate(LocalDate proposedStartDate) {
            this.proposedStartDate = proposedStartDate;
        }
        
        public int getEstimatedSessionCount() {
            // Estimación básica: 1 sesión cada 2 tratamientos (mínimo 1)
            return Math.max(1, (int) Math.ceil(treatments.size() / 2.0));
        }
    }
} 