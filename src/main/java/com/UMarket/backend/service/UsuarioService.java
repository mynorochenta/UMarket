package com.UMarket.backend.service;

import com.UMarket.backend.model.Usuario;
import com.UMarket.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    private boolean esEmailEducativo(String email) {
        if (email == null) return false;

        String[] dominiosPermitidos = {
                "usac.edu.gt",       // Universidad de San Carlos de Guatemala
                "galileo.edu",       // Universidad Galileo
                "uvg.edu.gt",        // Universidad del Valle de Guatemala
                "ufm.edu",           // Universidad Francisco Marroquín
                "umg.edu.gt",        // Universidad Mariano Gálvez
                "miumg.edu.gt",      // Universidad Mariano Gálvez (sistema nuevo)
                "url.edu.gt",        // Universidad Rafael Landívar
                "unis.edu.gt",       // Universidad del Istmo
                "umes.edu.gt",       // Universidad Mesoamericana
                "upana.edu.gt",      // Universidad Panamericana
                "uni.edu.gt",        // Universidad InterNaciones
                "urural.edu.gt",     // Universidad Rural
                "usanpablo.edu.gt",  // Universidad de San Pablo
                "udavinci.edu.gt",   // Universidad Da Vinci
                "udeo.edu.gt",       // Universidad de Occidente
                "ureg.edu.gt",       // Universidad Regional

        };

        String emailLower = email.toLowerCase();
        for (String dominio : dominiosPermitidos) {
            if (emailLower.endsWith(dominio)) {
                return true;
            }
        }
        return false;
    }

    // ─── CONSULTAS ──────────────────────────────────────────────

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
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