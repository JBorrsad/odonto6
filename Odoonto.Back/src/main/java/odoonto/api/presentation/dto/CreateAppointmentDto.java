package odoonto.api.presentation.dto;

import lombok.Data;

@Data
public class CreateAppointmentDto {
    private String patientId;
    private String doctorId;
    private String start; // formato ISO8601: "2023-05-12T14:30:00Z"
    private int slots;
}