package com.example.demo.controlador;

import com.example.demo.datos.TarjetaDAO;
import com.example.demo.datos.UsuarioRepository;
import com.example.demo.modelo.Tarjeta;
import com.example.demo.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tarjetas")
@CrossOrigin(originPatterns = {"http://localhost:3000", "http://localhost:3001"})
public class TarjetaController {

    @Autowired
    private TarjetaDAO tarjetaDAO;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetasPorUsuario(
            @PathVariable int usuarioId,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).build();
        
        Usuario usuario = usuarioOpt.get();
        // Verificar que el usuario solo pueda ver sus propias tarjetas
        if (usuario.getId() != usuarioId) {
            return ResponseEntity.status(403).build();
        }
        
        List<Tarjeta> tarjetas = tarjetaDAO.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(tarjetas);
    }

    @PostMapping
    public ResponseEntity<Tarjeta> crearTarjeta(
            @RequestBody Tarjeta tarjeta,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).build();
        
        Usuario usuario = usuarioOpt.get();
        tarjeta.setUsuario(usuario);
        
        Tarjeta tarjetaGuardada = tarjetaDAO.guardar(tarjeta);
        return ResponseEntity.ok(tarjetaGuardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarjeta(
            @PathVariable int id,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) return ResponseEntity.status(401).build();
        
        // Verificar que la tarjeta pertenece al usuario autenticado
        List<Tarjeta> tarjetasUsuario = tarjetaDAO.buscarPorUsuario(usuarioOpt.get().getId());
        boolean tarjetaPerteneceAlUsuario = tarjetasUsuario.stream()
                .anyMatch(t -> t.getId() == id);
        
        if (!tarjetaPerteneceAlUsuario) {
            return ResponseEntity.status(403).build();
        }
        
        tarjetaDAO.eliminar(id);
        return ResponseEntity.ok().build();
    }
} 