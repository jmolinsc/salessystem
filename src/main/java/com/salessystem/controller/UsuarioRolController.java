package com.salessystem.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salessystem.model.Rol;
import com.salessystem.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRolController {

    private final UsuarioService usuarioService;

    public UsuarioRolController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/{usuarioId}/roles/{rolId}")
    public ResponseEntity<?> addRolToUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Long rolId) {
        usuarioService.addRolToUsuario(usuarioId, rolId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{usuarioId}/roles/{rolId}")
    public ResponseEntity<?> removeRolFromUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Long rolId) {
        usuarioService.removeRolFromUsuario(usuarioId, rolId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}/roles")
    public ResponseEntity<Set<Rol>> getRolesByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(usuarioService.getRolesByUsuarioId(usuarioId));
    }
}
