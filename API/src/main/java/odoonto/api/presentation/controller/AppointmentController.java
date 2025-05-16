package odoonto.api.presentation.controller;

import odoonto.api.application.ManageAppointmentsService;
import odoonto.api.application.BookAppointmentService;
import odoonto.api.presentation.dto.AppointmentDto;
import odoonto.api.presentation.dto.CreateAppointmentDto;
import odoonto.api.presentation.dto.mappers.AppointmentMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final ManageAppointmentsService crudService;
    private final BookAppointmentService bookService;
    private final AppointmentMapper mapper;

    public AppointmentController(
            ManageAppointmentsService crudService,
            BookAppointmentService bookService,
            AppointmentMapper mapper
    ) {
        this.crudService = crudService;
        this.bookService = bookService;
        this.mapper = mapper;
    }

    // Listar todas las citas
    @GetMapping
    public Flux<AppointmentDto> getAll() {
        return crudService.findAll().map(mapper::toDto);
    }

    // Obtener cita por id
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AppointmentDto>> getById(@PathVariable String id) {
        return crudService.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // CRUD create
    @PostMapping
    public Mono<ResponseEntity<AppointmentDto>> create(@RequestBody CreateAppointmentDto dto) {
        return crudService.create(mapper.toEntity(dto))
                .map(mapper::toDto)
                .map(saved ->
                        ResponseEntity
                                .created(URI.create("/api/appointments/" + saved.getId()))
                                .body(saved)
                );
    }

    // Endpoint específico de "booking" (con validación de solapamientos)
    @PostMapping("/book")
    public Mono<ResponseEntity<AppointmentDto>> book(@RequestBody CreateAppointmentDto dto) {
        // Convertir String a LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.ofInstant(
                Instant.parse(dto.getStart()), 
                ZoneId.systemDefault()
        );
        
        return bookService.book(
                        dto.getPatientId(),
                        dto.getDoctorId(),
                        startDateTime,
                        dto.getSlots()
                )
                .map(mapper::toDto)
                .map(saved ->
                        ResponseEntity
                                .created(URI.create("/api/appointments/" + saved.getId()))
                                .body(saved)
                );
    }

    // Actualizar
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AppointmentDto>> update(
            @PathVariable String id,
            @RequestBody CreateAppointmentDto dto
    ) {
        return crudService.findById(id)
                .flatMap(existing ->
                        crudService.update(id, mapper.toEntity(dto))
                )
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Borrar
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return crudService.findById(id)
                .flatMap(existing ->
                        crudService.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build())
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
