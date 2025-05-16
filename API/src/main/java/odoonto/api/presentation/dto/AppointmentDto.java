package odoonto.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppointmentDto {
    private String id;
    private String patientId;
    private String doctorId;
    private String start; // formato ISO8601: "2023-05-12T14:30:00Z"
    private int slots;
    private String end; // formato ISO8601: "2023-05-12T15:00:00Z"
}
