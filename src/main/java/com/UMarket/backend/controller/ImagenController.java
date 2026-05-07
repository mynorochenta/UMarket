package com.UMarket.backend.controller;

import com.UMarket.backend.model.ImagenPublicacion;
import com.UMarket.backend.model.Publicacion;
import com.UMarket.backend.repository.ImagenPublicacionRepository;
import com.UMarket.backend.repository.PublicacionRepository;
import com.UMarket.backend.service.CloudinaryService;
import com.UMarket.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ImagenPublicacionRepository imagenRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UsuarioService usuarioService;

    // POST /api/imagenes/subir — sube una sola imagen y devuelve el URL
    @PostMapping("/subir")
    public ResponseEntity<?> subirImagen(@RequestParam("archivo") MultipartFile archivo) {
        try {
            String url = cloudinaryService.subirImagen(archivo);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al subir la imagen: " + e.getMessage());
        }
    }

    // POST /api/imagenes/publicacion/{id} — sube múltiples imágenes a una publicación
    @PostMapping("/publicacion/{publicacionId}")
    public ResponseEntity<?> subirImagenesPublicacion(
            @PathVariable Long publicacionId,
            @RequestParam("archivos") List<MultipartFile> archivos) {
        try {
            Publicacion publicacion = publicacionRepository.findById(publicacionId)
                    .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

            Long idAutenticado = getIdAutenticado();
            if (!publicacion.getVendedor().getId().equals(idAutenticado)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permiso para subir imágenes a esta publicación");
            }

            if (archivos.size() > 5) {
                return ResponseEntity.badRequest().body("Máximo 5 imágenes por publicación");
            }

            List<ImagenPublicacion> imagenesGuardadas = new ArrayList<>();

            for (int i = 0; i < archivos.size(); i++) {
                String url = cloudinaryService.subirImagen(archivos.get(i));

                ImagenPublicacion imagen = new ImagenPublicacion();
                imagen.setUrl(url);
                imagen.setOrden(i + 1);
                imagen.setPublicacion(publicacion);

                imagenesGuardadas.add(imagenRepository.save(imagen));
            }

            return ResponseEntity.ok(imagenesGuardadas);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al subir imágenes: " + e.getMessage());
        }
    }

    private Long getIdAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"))
                .getId();
    }
}
