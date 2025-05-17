// src/main/java/odoonto/api/presentation/controller/DoctorController.java
package odoonto.api.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import odoonto.api.application.ManageDoctorsService;
import odoonto.api.presentation.dto.DoctorDto;
import odoonto.api.presentation.dto.CreateDoctorDto;
import odoonto.api.presentation.dto.mappers.DoctorMapper;
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final ManageDoctorsService service;
    private final DoctorMapper mapper;

    public DoctorController(ManageDoctorsService service,
                            DoctorMapper mapper) {
        this.service = service;
        this.mapper  = mapper;
    }

    @GetMapping
    public Flux<DoctorDto> getAll() {
        return service.findAll().map(mapper::toDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DoctorDto>> getById(@PathVariable String id) {
        return service.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // ← Aquí: recibe CreateDoctorDto y mapea con el método específico
    @PostMapping
    public Mono<ResponseEntity<DoctorDto>> create(@RequestBody CreateDoctorDto createDto) {
        return service.create(mapper.toEntity(createDto))
                .map(mapper::toDto)
                .map(created ->
                        ResponseEntity
                                .created(null) // podrías usar UriComponentsBuilder si quieres
                                .body(created)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<DoctorDto>> update(@PathVariable String id,
                                                  @RequestBody DoctorDto dto) {
        return service.findById(id)
                .flatMap(existing ->
                        service.update(id, mapper.toEntity(dto))
                )
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.findById(id)
                .flatMap(existing ->
                        service.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build())
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

