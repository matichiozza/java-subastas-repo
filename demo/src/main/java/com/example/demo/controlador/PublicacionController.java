package com.example.demo.controlador;

import com.example.demo.datos.PublicacionRepository;
import com.example.demo.datos.UsuarioRepository;
import com.example.demo.datos.OfertaRepository;
import com.example.demo.modelo.Publicacion;
import com.example.demo.modelo.Usuario;
import com.example.demo.modelo.PublicacionRequest;
import com.example.demo.modelo.Oferta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/publicaciones")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PublicacionController {
    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final OfertaRepository ofertaRepository;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Publicacion> crearPublicacion(
            @RequestPart("publicacion") PublicacionRequest publicacionRequest,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        Usuario usuario = usuarioOpt.get();

        // Validar imágenes
        final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
        final List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/webp", "image/gif");

        // Guardar imágenes
        List<String> rutasImagenes = new ArrayList<>();
        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (!file.isEmpty()) {
                    if (!allowedTypes.contains(file.getContentType())) {
                        return ResponseEntity.badRequest().body(null);
                    }
                    if (file.getSize() > MAX_SIZE) {
                        return ResponseEntity.badRequest().body(null);
                    }
                    String nombreArchivo = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
                    String rutaRelativa = "/uploads/" + nombreArchivo;
                    String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
                    String rutaAbsoluta = uploadDir + File.separator + nombreArchivo;
                    File dest = new File(rutaAbsoluta);
                    dest.getParentFile().mkdirs();
                    file.transferTo(dest);
                    rutasImagenes.add(rutaRelativa);
                }
            }
        }

        // Crear entidad Publicacion
        Publicacion publicacion = new Publicacion();
        publicacion.setTitulo(publicacionRequest.getTitulo());
        publicacion.setDescripcion(publicacionRequest.getDescripcion());
        publicacion.setCategoria(publicacionRequest.getCategoria());
        publicacion.setCondicion(publicacionRequest.getCondicion());
        publicacion.setPrecioInicial(publicacionRequest.getPrecioInicial());
        publicacion.setIncrementoMinimo(publicacionRequest.getIncrementoMinimo());
        publicacion.setFechaFin(publicacionRequest.getFechaFin());
        publicacion.setImagenes(rutasImagenes);
        publicacion.setUsuario(usuario);
        publicacion.setFechaInicio(new Date());
        publicacion.setEstado("ACTIVO");
        Publicacion guardada = publicacionRepository.save(publicacion);
        return ResponseEntity.ok(guardada);
    }

    @GetMapping("/mias")
    public ResponseEntity<List<Publicacion>> misPublicaciones(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        Usuario usuario = usuarioOpt.get();
        List<Publicacion> publicaciones = publicacionRepository.findAll()
            .stream()
            .filter(pub -> pub.getUsuario() != null && pub.getUsuario().getId() == usuario.getId())
            .toList();
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping
    public List<Publicacion> todasPublicaciones() {
        return publicacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publicacion> obtenerPublicacionPorId(@PathVariable Integer id) {
        Optional<Publicacion> pubOpt = publicacionRepository.findById(id);
        if (pubOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pubOpt.get());
    }

    @GetMapping("/{id}/ofertas")
    public ResponseEntity<List<Oferta>> obtenerOfertasPorPublicacion(@PathVariable Integer id) {
        List<Oferta> ofertas = ofertaRepository.findByPublicacionIdOrderByFechaDesc(id);
        return ResponseEntity.ok(ofertas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPublicacion(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<Publicacion> pubOpt = publicacionRepository.findById(id);
        if (pubOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Publicacion pub = pubOpt.get();
        // Solo el dueño puede borrar
        if (pub.getUsuario() == null || !pub.getUsuario().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }
        // Borrar imágenes del disco
        if (pub.getImagenes() != null) {
            for (String ruta : pub.getImagenes()) {
                try {
                    String absPath = System.getProperty("user.dir") + ruta.replace("/", File.separator);
                    File f = new File(absPath);
                    if (f.exists()) f.delete();
                } catch (Exception ignored) {}
            }
        }
        publicacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 