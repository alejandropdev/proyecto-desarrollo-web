package com.muk.controller.api;

import com.muk.entities.Cliente;
import com.muk.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientesApiController {
    private final ClienteService clienteService;

    @GetMapping
    public List<ApiDtos.ClienteDto> clientes() {
        return clienteService.findAll().stream().map(ApiMappers::toClienteDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> clienteById(@PathVariable Long id) {
        return clienteService.findById(id)
                .<ResponseEntity<?>>map(c -> ResponseEntity.ok(ApiMappers.toClienteDto(c)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Cliente no encontrado.")));
    }

    @PostMapping
    public ResponseEntity<?> createCliente(@RequestBody ApiDtos.ClienteUpsertRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.nombre().trim());
        cliente.setApellido(request.apellido().trim());
        cliente.setEmail(request.email().trim());
        cliente.setTelefono(request.telefono().trim());
        cliente.setDireccion(request.direccion().trim());
        cliente.setContrasenaHash(request.contrasena());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiMappers.toClienteDto(clienteService.save(cliente)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCliente(@PathVariable Long id, @RequestBody ApiDtos.ClienteUpsertRequest request) {
        Optional<Cliente> existing = clienteService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Cliente no encontrado."));
        }
        Cliente cliente = existing.get();
        cliente.setNombre(request.nombre().trim());
        cliente.setApellido(request.apellido().trim());
        cliente.setEmail(request.email().trim());
        cliente.setTelefono(request.telefono().trim());
        cliente.setDireccion(request.direccion().trim());
        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            cliente.setContrasenaHash(request.contrasena());
        }
        return ResponseEntity.ok(ApiMappers.toClienteDto(clienteService.save(cliente)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Cliente eliminado."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginCliente(@RequestBody ApiDtos.LoginRequest request) {
        ClienteService.LoginResult result = clienteService.login(request.email(), request.password());
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(ApiMappers.toClienteDto(result.cliente()));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registroCliente(@RequestBody ApiDtos.ClienteUpsertRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.nombre().trim());
        cliente.setApellido(request.apellido().trim());
        cliente.setEmail(request.email().trim());
        cliente.setTelefono(request.telefono().trim());
        cliente.setDireccion(request.direccion().trim());
        cliente.setContrasenaHash(request.contrasena());
        ClienteService.RegistroResult result = clienteService.registrarConValidacion(cliente);
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiMappers.toClienteDto(result.cliente()));
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(@RequestParam String email) {
        ClienteService.PerfilResult result = clienteService.obtenerPerfilPorEmail(email, "Debes iniciar sesión para ver tu perfil.");
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(ApiMappers.toClienteDto(result.cliente()));
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> editarPerfil(@RequestParam String emailOriginal, @RequestBody ApiDtos.ClienteUpsertRequest request) {
        Optional<Cliente> existing = clienteService.findByEmail(emailOriginal);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Usuario no encontrado."));
        }
        Cliente cliente = existing.get();
        cliente.setNombre(request.nombre().trim());
        cliente.setApellido(request.apellido().trim());
        cliente.setEmail(request.email().trim());
        cliente.setTelefono(request.telefono().trim());
        cliente.setDireccion(request.direccion().trim());
        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            cliente.setContrasenaHash(request.contrasena());
        }
        ClienteService.ActionResult result = clienteService.actualizarPerfil(cliente);
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(ApiMappers.toClienteDto(clienteService.findById(cliente.getId()).orElse(cliente)));
    }

    @DeleteMapping("/perfil")
    public ResponseEntity<?> eliminarPerfil(@RequestParam String email) {
        ClienteService.ActionResult result = clienteService.eliminarPerfilPorEmail(email, "Debes iniciar sesión para eliminar tu cuenta.");
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(Map.of("message", "Tu cuenta ha sido eliminada."));
    }
}
