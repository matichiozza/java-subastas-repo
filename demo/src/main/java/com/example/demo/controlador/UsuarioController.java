package com.example.demo.controlador;

import com.example.demo.datos.UsuarioRepository;
import com.example.demo.modelo.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;

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
        if (userDetails == null) return ResponseEntity.status(401).build();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).build();
        Usuario usuario = usuarioOpt.get();
        // Validar imagen
        final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
        final String[] allowedTypes = {"image/jpeg", "image/png", "image/webp", "image/gif"};
        boolean tipoValido = false;
        for (String t : allowedTypes) if (t.equals(fotoPerfil.getContentType())) tipoValido = true;
        if (!tipoValido) return ResponseEntity.badRequest().build();
        if (fotoPerfil.getSize() > MAX_SIZE) return ResponseEntity.badRequest().build();
        // Guardar imagen
        String nombreArchivo = System.currentTimeMillis() + "_" + StringUtils.cleanPath(fotoPerfil.getOriginalFilename());
        String rutaRelativa = "/uploads/" + nombreArchivo;
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        String rutaAbsoluta = uploadDir + File.separator + nombreArchivo;
        File dest = new File(rutaAbsoluta);
        dest.getParentFile().mkdirs();
        fotoPerfil.transferTo(dest);
        usuario.setFotoPerfil(rutaRelativa);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuario);
    }
} 