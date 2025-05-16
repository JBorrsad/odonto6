package odoonto.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title       = "API REST DE ODOONTO",
				version     = "0.5",
				description = "[CRUD] Doctors, Patients y Appointments"
		)
)
@SpringBootApplication
public class StartApi {
	public static void main(String[] args) {
		SpringApplication.run(StartApi.class, args);
	}
}
