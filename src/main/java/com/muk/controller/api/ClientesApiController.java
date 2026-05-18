package com.muk.controller.api;

import com.muk.dto.ClienteResponseDto;
import com.muk.mapper.ClienteMapper;
import com.muk.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClientesApiController {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    @Autowired
    public ClientesApiController(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    @GetMapping
    public List<ClienteResponseDto> clientes() {
        return clienteMapper.toDtoList(clienteService.findAll().clientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> clienteById(@PathVariable Long id) {
        ClienteService.ClienteResult result = clienteService.findById(id);
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(clienteMapper.toDto(result.cliente()));
    }

    @PostMapping
    public ResponseEntity<Object> createCliente(@RequestBody ApiDtos.ClienteUpsertRequest request) {
        ClienteService.ClienteResult result = clienteService.create(toClienteCommand(request));
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toDto(result.cliente()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCliente(@PathVariable Long id, @RequestBody ApiDtos.ClienteUpsertRequest request) {
        ClienteService.ClienteResult result = clienteService.update(id, toClienteCommand(request));
        if (!result.success()) {
            HttpStatus status = "Cliente no encontrado.".equals(result.errorMessage()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(clienteMapper.toDto(result.cliente()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCliente(@PathVariable Long id) {
        ClienteService.ActionResult result = clienteService.delete(id);
        if (!result.success()) {
            HttpStatus status = "Cliente no encontrado.".equals(result.errorMessage()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(new ApiDtos.MessageResponse("Cliente eliminado."));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginCliente(@RequestBody ApiDtos.LoginRequest request) {
        ClienteService.LoginResult result = clienteService.login(request.email(), request.password());
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(clienteMapper.toDto(result.cliente()));
    }

    @PostMapping("/registro")
    public ResponseEntity<Object> registroCliente(@RequestBody ApiDtos.ClienteUpsertRequest request) {
        ClienteService.RegistroResult result = clienteService.registrarConValidacion(toClienteCommand(request));
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toDto(result.cliente()));
    }

    @GetMapping("/perfil")
    public ResponseEntity<Object> perfil(@RequestParam String email) {
        ClienteService.PerfilResult result = clienteService.obtenerPerfilPorEmail(email, "Debes iniciar sesión para ver tu perfil.");
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(clienteMapper.toDto(result.cliente()));
    }

    @PutMapping("/perfil")
    public ResponseEntity<Object> editarPerfil(@RequestParam String emailOriginal, @RequestBody ApiDtos.ClienteUpsertRequest request) {
        ClienteService.ClienteResult existing = clienteService.findByEmail(emailOriginal);
        if (!existing.success()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Usuario no encontrado."));
        }
        ClienteService.ClienteResult result = clienteService.update(existing.cliente().getId(), toClienteCommand(request));
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(clienteMapper.toDto(result.cliente()));
    }

    @DeleteMapping("/perfil")
    public ResponseEntity<ApiDtos.MessageResponse> eliminarPerfil(@RequestParam String email) {
        ClienteService.ActionResult result = clienteService.eliminarPerfilPorEmail(email, "Debes iniciar sesión para eliminar tu cuenta.");
        if (!result.success()) {
            return ResponseEntity.badRequest().body(new ApiDtos.MessageResponse(result.errorMessage()));
        }
        return ResponseEntity.ok(new ApiDtos.MessageResponse("Tu cuenta ha sido eliminada."));
    }

    private ClienteService.ClienteUpsertCommand toClienteCommand(ApiDtos.ClienteUpsertRequest request) {
        return new ClienteService.ClienteUpsertCommand(
                request.nombre(),
                request.apellido(),
                request.email(),
                request.telefono(),
                request.direccion(),
                request.contrasena()
        );
    }
}
