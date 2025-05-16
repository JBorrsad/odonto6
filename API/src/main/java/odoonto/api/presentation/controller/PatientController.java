package odoonto.api.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import odoonto.api.application.ManagePatientsService;
import odoonto.api.presentation.dto.PatientDto;
import odoonto.api.presentation.dto.CreatePatientDto;
import odoonto.api.presentation.dto.OdontogramDto;
import odoonto.api.presentation.dto.LesionCommandDto;
import odoonto.api.presentation.dto.mappers.PatientMapper;
import odoonto.api.presentation.dto.mappers.OdontogramMapper;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final ManagePatientsService service;
    private final PatientMapper mapper;
    private final OdontogramMapper odontogramMapper;

    public PatientController(ManagePatientsService service,
                             PatientMapper mapper,
                             OdontogramMapper odontogramMapper) {
        this.service = service;
        this.mapper = mapper;
        this.odontogramMapper = odontogramMapper;
    }

    @GetMapping
    public Flux<PatientDto> getAll() {
        return service.findAll().map(mapper::toDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PatientDto>> getById(@PathVariable String id) {
        return service.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<PatientDto>> create(@RequestBody CreatePatientDto dto) {
        return service.create(mapper.toEntity(dto))
                .map(mapper::toDto)
                .map(p -> ResponseEntity.created(null).body(p));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<PatientDto>> update(@PathVariable String id,
                                                   @RequestBody CreatePatientDto dto) {
        return service.update(id, mapper.toEntity(dto))
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    // ============ Odontograma ============

    @GetMapping("/{id}/odontogram")
    public Mono<ResponseEntity<OdontogramDto>> getOdontogram(@PathVariable String id) {
        return service.getOdontogram(id)
                .map(odontogramMapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/odontogram/lesions")
    public Mono<ResponseEntity<OdontogramDto>> addLesions(
            @PathVariable String id,
            @RequestBody List<LesionCommandDto> cmds) {
        return service.addLesions(id, cmds)
                .map(odontogramMapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/odontogram/lesions")
    public Mono<ResponseEntity<OdontogramDto>> removeLesions(
            @PathVariable String id,
            @RequestBody List<LesionCommandDto> cmds) {
        return service.removeLesions(id, cmds)
                .map(odontogramMapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
