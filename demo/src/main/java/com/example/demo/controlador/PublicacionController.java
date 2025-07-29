package com.example.demo.controlador;

import com.example.demo.datos.PublicacionRepository;
import com.example.demo.datos.UsuarioRepository;
import com.example.demo.datos.OfertaRepository;
import com.example.demo.modelo.Publicacion;
import com.example.demo.modelo.Usuario;
import com.example.demo.modelo.PublicacionRequest;
import com.example.demo.modelo.Oferta;
import com.example.demo.modelo.OfertaRequest;
import com.example.demo.modelo.OfertaResponse;
import com.example.demo.modelo.CancelacionRequest;
import com.example.demo.modelo.MotivoCancelacion;
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
import java.util.Map;
import java.util.Optional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
@RequestMapping("/publicaciones")
@CrossOrigin(originPatterns = {"http://localhost:3000", "http://localhost:3001"})
@RequiredArgsConstructor
public class PublicacionController {
    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final OfertaRepository ofertaRepository;
    private final SimpMessagingTemplate messagingTemplate;

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

        // Validar fecha de finalización
        Date fechaFin = publicacionRequest.getFechaFin();
        Date hoy = new Date();
        // Resetear la hora de hoy a 00:00:00 para comparar solo fechas
        hoy.setHours(0);
        hoy.setMinutes(0);
        hoy.setSeconds(0);
        
        if (fechaFin != null && fechaFin.before(hoy)) {
            return ResponseEntity.badRequest().body(null);
        }

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

    @PostMapping("/ofertas")
    public ResponseEntity<?> crearOferta(@RequestBody OfertaRequest ofertaRequest, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }
        Usuario usuario = usuarioOpt.get();
        Optional<Publicacion> pubOpt = publicacionRepository.findById(ofertaRequest.getPublicacionId());
        if (pubOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Publicación no encontrada");
        }
        Publicacion pub = pubOpt.get();
        if (!"ACTIVO".equals(pub.getEstado())) {
            return ResponseEntity.badRequest().body("La publicación no está activa");
        }
        float precioActual = pub.getPrecioActual() > 0 ? pub.getPrecioActual() : pub.getPrecioInicial();
        float incremento = pub.getIncrementoMinimo() > 0 ? pub.getIncrementoMinimo() : 1;
        if (ofertaRequest.getMonto() < precioActual + incremento) {
            return ResponseEntity.badRequest().body("La oferta debe ser al menos $" + (precioActual + incremento));
        }
        // Crear y guardar oferta
        Oferta oferta = new Oferta();
        oferta.setMonto(ofertaRequest.getMonto());
        oferta.setFecha(new Date());
        oferta.setUsuario(usuario);
        oferta.setPublicacion(pub);
        ofertaRepository.save(oferta);
        // Actualizar precio actual y total de ofertas
        pub.setPrecioActual(ofertaRequest.getMonto());
        pub.setOfertasTotales(pub.getOfertasTotales() + 1);
        publicacionRepository.save(pub);
        // Devolver publicación actualizada y ofertas
        List<Oferta> ofertas = ofertaRepository.findByPublicacionIdOrderByFechaDesc(pub.getId());
        OfertaResponse response = new OfertaResponse(pub, ofertas);
        
        // Notificar a todos los clientes suscritos a la publicación
        messagingTemplate.convertAndSend("/topic/publicacion." + pub.getId(), response);
        
        // Enviar actualización general para todos los clientes
        Map<String, Object> updateMessage = Map.of(
            "type", "oferta_actualizada",
            "publicacion", pub
        );
        messagingTemplate.convertAndSend("/topic/publicaciones", updateMessage);
        
        return ResponseEntity.ok(response);
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

    @GetMapping("/{publicacionId}/ofertas/usuario/{usuarioId}")
    public ResponseEntity<Oferta> obtenerOfertaAnteriorUsuario(@PathVariable Integer publicacionId, @PathVariable Integer usuarioId) {
        Optional<Oferta> ofertaOpt = ofertaRepository.findFirstByPublicacionIdAndUsuarioIdOrderByFechaDesc(publicacionId, usuarioId);
        return ofertaOpt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{publicacionId}/ofertas/usuario/{usuarioId}")
    public ResponseEntity<?> eliminarOfertasUsuario(@PathVariable Integer publicacionId, @PathVariable Integer usuarioId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Verificar que el usuario autenticado es el mismo que quiere eliminar sus ofertas
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty() || usuarioOpt.get().getId() != usuarioId) {
            return ResponseEntity.status(403).build();
        }
        
        // Verificar que la publicación existe
        Optional<Publicacion> publicacionOpt = publicacionRepository.findById(publicacionId);
        if (publicacionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Eliminar todas las ofertas del usuario en esta publicación
        List<Oferta> ofertasAEliminar = ofertaRepository.findByPublicacionIdAndUsuarioId(publicacionId, usuarioId);
        if (ofertasAEliminar.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ofertaRepository.deleteAll(ofertasAEliminar);
        
        // Actualizar el precio actual de la publicación si es necesario
        Publicacion publicacion = publicacionOpt.get();
        List<Oferta> ofertasRestantes = ofertaRepository.findByPublicacionIdOrderByFechaDesc(publicacionId);
        
        if (ofertasRestantes.isEmpty()) {
            // Si no quedan ofertas, volver al precio inicial
            publicacion.setPrecioActual(publicacion.getPrecioInicial());
        } else {
            // Si quedan ofertas, usar la más alta
            publicacion.setPrecioActual(ofertasRestantes.get(0).getMonto());
        }
        
        publicacion.setOfertasTotales(ofertasRestantes.size());
        publicacionRepository.save(publicacion);
        
        // Notificar a todos los clientes suscritos
        OfertaResponse response = new OfertaResponse(publicacion, ofertasRestantes);
        messagingTemplate.convertAndSend("/topic/publicacion." + publicacionId, response);
        
        // Enviar actualización general para todos los clientes
        Map<String, Object> updateMessage = Map.of(
            "type", "oferta_actualizada",
            "publicacion", publicacion
        );
        messagingTemplate.convertAndSend("/topic/publicaciones", updateMessage);
        
        return ResponseEntity.ok().build();
    }



    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPublicacion(
            @PathVariable Integer id, 
            @RequestBody CancelacionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<Publicacion> pubOpt = publicacionRepository.findById(id);
        if (pubOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Publicacion pub = pubOpt.get();
        // Solo el dueño puede cancelar
        if (pub.getUsuario() == null || !pub.getUsuario().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }
        
        // Verificar si ya está cancelada
        if ("CANCELADO".equals(pub.getEstado())) {
            return ResponseEntity.badRequest().body("La publicación ya está cancelada");
        }
        
        // Obtener el usuario actual
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        Usuario usuario = usuarioOpt.get();
        
        // Verificar si hay ofertas activas
        boolean tieneOfertas = pub.getOfertasTotales() > 0;
        
        // Si tiene ofertas, verificar sanciones disponibles
        if (tieneOfertas && usuario.getSancionesDisponibles() <= 0) {
            // Eliminar permanentemente la cuenta del usuario
            usuarioRepository.deleteById(usuario.getId());
            return ResponseEntity.status(401).body(Map.of(
                "mensaje", "Tu cuenta ha sido eliminada permanentemente por cancelar una publicación con ofertas activas sin sanciones disponibles",
                "cuentaEliminada", true
            ));
        }
        
        // Aplicar sanción si tiene ofertas
        if (tieneOfertas) {
            usuario.setSancionesDisponibles(usuario.getSancionesDisponibles() - 1);
            usuarioRepository.save(usuario);
        }
        
        // Eliminar todas las ofertas asociadas a la publicación primero
        List<Oferta> ofertasAEliminar = ofertaRepository.findByPublicacionIdOrderByFechaDesc(id);
        if (!ofertasAEliminar.isEmpty()) {
            ofertaRepository.deleteAll(ofertasAEliminar);
        }
        
        // Eliminar imágenes del disco
        if (pub.getImagenes() != null) {
            for (String ruta : pub.getImagenes()) {
                try {
                    String absPath = System.getProperty("user.dir") + ruta.replace("/", File.separator);
                    File f = new File(absPath);
                    if (f.exists()) f.delete();
                } catch (Exception ignored) {}
            }
        }
        
        // Ahora eliminar la publicación (ya no hay ofertas que la referencien)
        publicacionRepository.deleteById(id);
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Publicación cancelada y eliminada exitosamente",
            "sancionAplicada", tieneOfertas,
            "sancionesRestantes", usuario.getSancionesDisponibles()
        ));
    }

    @GetMapping("/usuario/sanciones")
    public ResponseEntity<Map<String, Object>> obtenerSancionesUsuario(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar si el usuario tiene 0 sanciones y eliminarlo automáticamente
        if (usuario.getSancionesDisponibles() <= 0) {
            // Eliminar todas las ofertas del usuario
            List<Oferta> ofertasUsuario = ofertaRepository.findByUsuarioId(usuario.getId());
            if (!ofertasUsuario.isEmpty()) {
                ofertaRepository.deleteAll(ofertasUsuario);
            }
            
            // Eliminar todas las publicaciones del usuario
            List<Publicacion> publicacionesUsuario = publicacionRepository.findByUsuarioId(usuario.getId());
            if (!publicacionesUsuario.isEmpty()) {
                publicacionRepository.deleteAll(publicacionesUsuario);
            }
            
            // Eliminar el usuario
            usuarioRepository.deleteById(usuario.getId());
            
            return ResponseEntity.status(401).body(Map.of(
                "mensaje", "Tu cuenta ha sido eliminada por tener 0 sanciones disponibles",
                "cuentaEliminada", true
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "sancionesDisponibles", usuario.getSancionesDisponibles(),
            "maximoSanciones", 2
        ));
    }

    @PostMapping("/{id}/sincronizar-ofertas")
    public ResponseEntity<Publicacion> sincronizarOfertas(@PathVariable Integer id) {
        Optional<Publicacion> pubOpt = publicacionRepository.findById(id);
        if (pubOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Publicacion pub = pubOpt.get();
        List<Oferta> ofertas = ofertaRepository.findByPublicacionIdOrderByFechaDesc(id);
        pub.setOfertasTotales(ofertas.size());
        
        // Actualizar precio actual si hay ofertas
        if (!ofertas.isEmpty()) {
            pub.setPrecioActual(ofertas.get(0).getMonto());
        } else {
            pub.setPrecioActual(pub.getPrecioInicial());
        }
        
        Publicacion actualizada = publicacionRepository.save(pub);
        return ResponseEntity.ok(actualizada);
    }

    @PostMapping("/limpiar-usuarios-sin-sanciones")
    public ResponseEntity<Map<String, Object>> limpiarUsuariosSinSanciones() {
        // Buscar usuarios con 0 sanciones
        List<Usuario> usuariosSinSanciones = usuarioRepository.findBySancionesDisponiblesLessThanEqual(0);
        
        int usuariosEliminados = 0;
        
        for (Usuario usuario : usuariosSinSanciones) {
            // Eliminar ofertas del usuario
            List<Oferta> ofertasUsuario = ofertaRepository.findByUsuarioId(usuario.getId());
            if (!ofertasUsuario.isEmpty()) {
                ofertaRepository.deleteAll(ofertasUsuario);
            }
            
            // Eliminar publicaciones del usuario
            List<Publicacion> publicacionesUsuario = publicacionRepository.findByUsuarioId(usuario.getId());
            if (!publicacionesUsuario.isEmpty()) {
                publicacionRepository.deleteAll(publicacionesUsuario);
            }
            
            // Eliminar el usuario
            usuarioRepository.deleteById(usuario.getId());
            usuariosEliminados++;
        }
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Limpieza completada",
            "usuariosEliminados", usuariosEliminados,
            "usuariosEncontrados", usuariosSinSanciones.size()
        ));
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
        // Eliminar todas las ofertas asociadas a la publicación primero
        List<Oferta> ofertasAEliminar = ofertaRepository.findByPublicacionIdOrderByFechaDesc(id);
        if (!ofertasAEliminar.isEmpty()) {
            ofertaRepository.deleteAll(ofertasAEliminar);
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
        
        // Ahora eliminar la publicación (ya no hay ofertas que la referencien)
        publicacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 