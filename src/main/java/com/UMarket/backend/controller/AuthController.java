package com.UMarket.backend.controller;

import com.UMarket.backend.dto.LoginRequest;
import com.UMarket.backend.dto.LoginResponse;
import com.UMarket.backend.model.Usuario;
import com.UMarket.backend.repository.UsuarioRepository;
import com.UMarket.backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 1. Verificar email y contraseña
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasena())
            );

            // 2. Buscar el usuario en la base de datos
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow();

            // 3. Generar el token JWT
            String token = jwtUtil.generarToken(request.getEmail());

            // 4. Devolver el token junto con datos básicos del usuario
            return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(), usuario.getId()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o contraseña incorrectos");
        }
    }
}