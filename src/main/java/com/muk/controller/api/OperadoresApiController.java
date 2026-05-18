package com.muk.controller.api;

import com.muk.dto.OperadorResponseDto;
import com.muk.mapper.OperadorMapper;
import com.muk.service.OperadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operadores")
@CrossOrigin(origins = "*")
public class OperadoresApiController {

    private final OperadorService operadorService;
    private final OperadorMapper operadorMapper;

    @Autowired
    public OperadoresApiController(OperadorService operadorService, OperadorMapper operadorMapper) {
        this.operadorService = operadorService;
        this.operadorMapper = operadorMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody ApiDtos.OperarioLoginRequest request) {
        OperadorService.LoginResult result = operadorService.login(request.usuario(), request.password());
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(new ApiDtos.OperarioLoginResponse(
                "Bienvenido operario",
                result.operador().getId(),
                result.operador().getUsuario(),
                result.operador().getNombre()
        ));
    }

    @GetMapping
    public List<OperadorResponseDto> operadores() {
        return operadorMapper.toDtoList(operadorService.findAllActive().operadores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> operadorById(@PathVariable Long id) {
        OperadorService.OperadorResult result = operadorService.findActiveById(id);
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(operadorMapper.toDto(result.operador()));
    }

    @PostMapping
    public ResponseEntity<Object> createOperador(@RequestBody ApiDtos.OperadorRequest request) {
        OperadorService.OperadorResult result = operadorService.create(toCommand(request));
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(operadorMapper.toDto(result.operador()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateOperador(@PathVariable Long id, @RequestBody ApiDtos.OperadorRequest request) {
        OperadorService.OperadorResult result = operadorService.update(id, toCommand(request));
        if (!result.success()) {
            HttpStatus status = "Operador no encontrado.".equals(result.errorMessage()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(operadorMapper.toDto(result.operador()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOperador(@PathVariable Long id) {
        OperadorService.ActionResult result = operadorService.delete(id);
        if (!result.success()) {
            HttpStatus status = "Operador no encontrado.".equals(result.errorMessage()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(new ApiDtos.MessageResponse("Operador eliminado."));
    }

    private OperadorService.OperadorUpsertCommand toCommand(ApiDtos.OperadorRequest request) {
        return new OperadorService.OperadorUpsertCommand(request.nombre(), request.usuario(), request.contrasena());
    }
}
