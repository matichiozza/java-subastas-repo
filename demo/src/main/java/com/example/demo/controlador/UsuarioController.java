package com.example.demo.controlador;

import com.example.demo.datos.OfertaRepository;
import com.example.demo.datos.PublicacionRepository;
import com.example.demo.datos.UsuarioRepository;
import com.example.demo.modelo.CambiarContrasenaRequest;
import com.example.demo.modelo.CbuRequest;
import com.example.demo.modelo.Oferta;
import com.example.demo.modelo.Publicacion;
import com.example.demo.modelo.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    private final PublicacionRepository publicacionRepository;
    private final OfertaRepository ofertaRepository;
    private final PasswordEncoder passwordEncoder;

    // Actualizar datos personales (excepto foto de perfil y username)
    @PutMapping("/mis-datos")
    public ResponseEntity<Usuario> actualizarMisDatos(
            @RequestBody Usuario datos,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).build();
        Usuario usuario = usuarioOpt.get();
        usuario.setNombre(datos.getNombre());
        usuario.setDireccion(datos.getDireccion());
        if (datos.getCiudad() != null) usuario.setCiudad(datos.getCiudad());
        if (datos.getCodigoPostal() != null) usuario.setCodigoPostal(datos.getCodigoPostal());
        if (datos.getPais() != null) usuario.setPais(datos.getPais());
        usuario.setLatitud(datos.getLatitud());
        usuario.setLongitud(datos.getLongitud());
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuario);
    }

    // Actualizar foto de perfil
    @PutMapping(value = "/foto-perfil", consumes = {"multipart/form-data"})
    public ResponseEntity<Usuario> actualizarFotoPerfil(
            @RequestPart("fotoPerfil") MultipartFile fotoPerfil,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        System.out.println("=== Iniciando actualización de foto de perfil ===");
        System.out.println("Usuario: " + (userDetails != null ? userDetails.getUsername() : "null"));
        
        if (userDetails == null) {
            System.out.println("Error: UserDetails es null");
            return ResponseEntity.status(401).build();
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            System.out.println("Error: Usuario no encontrado");
            return ResponseEntity.status(401).build();
        }
        
        Usuario usuario = usuarioOpt.get();
        System.out.println("Usuario encontrado: " + usuario.getUsername());
        
        // Validar imagen
        final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
        final String[] allowedTypes = {"image/jpeg", "image/png", "image/webp", "image/gif"};
        
        System.out.println("Archivo recibido:");
        System.out.println("- Nombre: " + fotoPerfil.getOriginalFilename());
        System.out.println("- Tipo: " + fotoPerfil.getContentType());
        System.out.println("- Tamaño: " + fotoPerfil.getSize() + " bytes");
        
        boolean tipoValido = false;
        for (String t : allowedTypes) {
            if (t.equals(fotoPerfil.getContentType())) {
                tipoValido = true;
                break;
            }
        }
        
        if (!tipoValido) {
            System.out.println("Error: Tipo de archivo no válido: " + fotoPerfil.getContentType());
            return ResponseEntity.badRequest().build();
        }
        
        if (fotoPerfil.getSize() > MAX_SIZE) {
            System.out.println("Error: Archivo demasiado grande: " + fotoPerfil.getSize() + " bytes");
            return ResponseEntity.badRequest().build();
        }
        
        // Guardar imagen
        String nombreArchivo = System.currentTimeMillis() + "_" + StringUtils.cleanPath(fotoPerfil.getOriginalFilename());
        String rutaRelativa = "/uploads/" + nombreArchivo;
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        String rutaAbsoluta = uploadDir + File.separator + nombreArchivo;
        
        System.out.println("Guardando archivo:");
        System.out.println("- Ruta relativa: " + rutaRelativa);
        System.out.println("- Ruta absoluta: " + rutaAbsoluta);
        
        File dest = new File(rutaAbsoluta);
        dest.getParentFile().mkdirs();
        fotoPerfil.transferTo(dest);
        
        System.out.println("Archivo guardado exitosamente");
        
        usuario.setFotoPerfil(rutaRelativa);
        usuarioRepository.save(usuario);
        
        System.out.println("Usuario actualizado con nueva foto: " + usuario.getFotoPerfil());
        System.out.println("=== Fin actualización de foto de perfil ===");
        
        return ResponseEntity.ok(usuario);
    }

    // Agregar CBU
    @PostMapping("/cbu")
    public ResponseEntity<Usuario> agregarCbu(
            @RequestBody CbuRequest cbuRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).build();
        
        Usuario usuario = usuarioOpt.get();
        
        String cbu = cbuRequest.getCbu();
        
        // Validar formato CBU (22 dígitos)
        if (cbu == null || cbu.trim().isEmpty() || !cbu.matches("\\d{22}")) {
            return ResponseEntity.badRequest().build();
        }
        
        // Verificar que el CBU no esté en uso por otro usuario
        Optional<Usuario> usuarioConCbu = usuarioRepository.findByCbu(cbu);
        if (usuarioConCbu.isPresent() && !usuarioConCbu.get().getId().equals(usuario.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        usuario.setCbu(cbu);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuario);
    }

    // Eliminar CBU
    @DeleteMapping("/cbu")
    public ResponseEntity<Usuario> eliminarCbu(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).build();
        
        Usuario usuario = usuarioOpt.get();
        usuario.setCbu(null);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuario);
    }

    // Eliminar cuenta de usuario
    @DeleteMapping("/eliminar-cuenta")
    public ResponseEntity<?> eliminarCuenta(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar si tiene publicaciones activas
        List<Publicacion> publicacionesActivas = publicacionRepository.findByUsuarioId(usuario.getId())
                .stream()
                .filter(pub -> "ACTIVO".equals(pub.getEstado()) && pub.getFechaFin().after(new Date()))
                .toList();
        
        if (!publicacionesActivas.isEmpty()) {
            return ResponseEntity.badRequest().body("No se puede eliminar la cuenta. Tienes " + 
                    publicacionesActivas.size() + " subasta(s) activa(s). Debes finalizar o cancelar todas las subastas antes de eliminar tu cuenta.");
        }
        
        // Verificar si tiene ofertas en subastas activas
        List<Oferta> ofertasEnSubastasActivas = ofertaRepository.findByUsuarioId(usuario.getId())
                .stream()
                .filter(oferta -> "ACTIVO".equals(oferta.getPublicacion().getEstado()) && 
                        oferta.getPublicacion().getFechaFin().after(new Date()))
                .toList();
        
        if (!ofertasEnSubastasActivas.isEmpty()) {
            return ResponseEntity.badRequest().body("No se puede eliminar la cuenta. Tienes " + 
                    ofertasEnSubastasActivas.size() + " oferta(s) en subasta(s) activa(s). Debes esperar a que finalicen las subastas antes de eliminar tu cuenta.");
        }
        
        // Si pasa todas las verificaciones, eliminar el usuario
        try {
            usuarioRepository.deleteById(usuario.getId());
            return ResponseEntity.ok("Cuenta eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar la cuenta: " + e.getMessage());
        }
    }

    // Cambiar contraseña
    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(
            @RequestBody CambiarContrasenaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(request.getContrasenaActual(), usuario.getPassword())) {
            return ResponseEntity.badRequest().body("La contraseña actual es incorrecta");
        }
        
        // Verificar que la nueva contraseña sea diferente a la actual
        if (passwordEncoder.matches(request.getNuevaContrasena(), usuario.getPassword())) {
            return ResponseEntity.badRequest().body("La nueva contraseña debe ser diferente a la actual");
        }
        
        // Validar que la nueva contraseña tenga al menos 6 caracteres
        if (request.getNuevaContrasena().length() < 6) {
            return ResponseEntity.badRequest().body("La nueva contraseña debe tener al menos 6 caracteres");
        }
        
        // Cambiar la contraseña
        usuario.setPassword(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok("Contraseña cambiada exitosamente");
    }
} 