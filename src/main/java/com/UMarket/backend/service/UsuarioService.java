package com.UMarket.backend.service;

import com.UMarket.backend.model.Usuario;
import com.UMarket.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ─── REGISTRO ───────────────────────────────────────────────

    public Usuario registrar(Usuario usuario) {

        // SEGURIDAD #1: Verificar que el email sea .edu
        if (!esEmailEducativo(usuario.getEmail())) {
            throw new RuntimeException("Solo se permiten correos institucionales (.edu)");
        }

        // SEGURIDAD #2: Verificar que el email no esté ya registrado
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Este correo ya está registrado");
        }

        // SEGURIDAD #3: Nunca guardar contraseña en texto plano
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // El usuario empieza sin verificar y activo
        usuario.setVerificado(false);
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());

        // VERIFICACIÓN: Generar token único para confirmar el correo
        // UUID genera algo como: "a3f8c2d1-4b5e-6789-abcd-ef0123456789"
        String token = UUID.randomUUID().toString();
        usuario.setTokenVerificacion(token);

        // El token expira en 24 horas
        usuario.setFechaExpiracionToken(LocalDateTime.now().plusHours(24));

        // Guardar el usuario en la base de datos
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Enviar email de verificación
        emailService.enviarEmailVerificacion(usuario.getEmail(), token);

        return usuarioGuardado;
    }

    // ─── VERIFICAR TOKEN ────────────────────────────────────────

    public String verificarEmail(String token) {

        // Buscar usuario con ese token
        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // Verificar que el token no haya expirado
        if (LocalDateTime.now().isAfter(usuario.getFechaExpiracionToken())) {
            throw new RuntimeException("El token ha expirado");
        }

        // Marcar como verificado y limpiar el token
        usuario.setVerificado(true);
        usuario.setTokenVerificacion(null);
        usuario.setFechaExpiracionToken(null);
        usuarioRepository.save(usuario);

        return "¡Cuenta verificada exitosamente! Ya puedes iniciar sesión en UMarket.";
    }

    // ─── VALIDACIÓN .EDU ────────────────────────────────────────

    private static final Set<String> DOMINIOS_PERMITIDOS = Set.of(
            "usac.edu.gt",
            "galileo.edu",
            "uvg.edu.gt",
            "ufm.edu",
            "umg.edu.gt",
            "miumg.edu.gt",
            "url.edu.gt",
            "unis.edu.gt",
            "umes.edu.gt",
            "upana.edu.gt",
            "uni.edu.gt",
            "urural.edu.gt",
            "usanpablo.edu.gt",
            "udavinci.edu.gt",
            "udeo.edu.gt",
            "ureg.edu.gt"
    );

    private boolean esEmailEducativo(String email) {
        if (email == null || !email.contains("@")) return false;
        // Comparación exacta del dominio — evita bypass con dominios como "fakegalileo.edu"
        String dominio = email.toLowerCase().substring(email.lastIndexOf("@") + 1);
        return DOMINIOS_PERMITIDOS.contains(dominio);
    }

    // ─── CONSULTAS ──────────────────────────────────────────────

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAllByActivoTrue();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // ─── ACTUALIZAR ─────────────────────────────────────────────

    public Usuario actualizar(Long id, Usuario datosNuevos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(datosNuevos.getNombre());
        usuario.setUniversidad(datosNuevos.getUniversidad());

        return usuarioRepository.save(usuario);
    }

    // ─── DESACTIVAR (no borrar) ──────────────────────────────────

    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}