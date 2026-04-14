package com.muk.controller.api;

import com.muk.entities.Adicional;
import com.muk.service.AdicionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/adiciones")
@CrossOrigin(origins = "*")
public class AdminAdicionesApiController {

    private final AdicionalService adicionalService;

    @Autowired
    public AdminAdicionesApiController(AdicionalService adicionalService) {
        this.adicionalService = adicionalService;
    }

    @GetMapping
    public List<ApiDtos.AdicionalDto> adiciones() {
        return adicionalService.findAll().stream().map(ApiMappers::toAdicionalDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDtos.AdicionalDto> adicionById(@PathVariable Long id) {
        return adicionalService.findById(id)
                .map(adicional -> ResponseEntity.ok(ApiMappers.toAdicionalDto(adicional)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<ApiDtos.AdicionalDto> createAdicion(@RequestBody ApiDtos.AdicionalUpsertRequest request) {
        try {
            Adicional created = adicionalService.createAdicion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiMappers.toAdicionalDto(created));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiDtos.AdicionalDto> updateAdicion(
            @PathVariable Long id,
            @RequestBody ApiDtos.AdicionalUpsertRequest request) {
        try {
            return adicionalService.updateAdicion(id, request)
                    .map(adicional -> ResponseEntity.ok(ApiMappers.toAdicionalDto(adicional)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiDtos.MessageResponse> deleteAdicion(@PathVariable Long id) {
        adicionalService.delete(id);
        return ResponseEntity.ok(new ApiDtos.MessageResponse("Adición eliminada."));
    }
}
