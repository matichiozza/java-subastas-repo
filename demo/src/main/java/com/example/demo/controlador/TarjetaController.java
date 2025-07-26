package com.example.demo.controlador;

import com.example.demo.datos.TarjetaDAO;
import com.example.demo.modelo.Tarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarjetas")
@CrossOrigin(originPatterns = {"http://localhost:3000", "http://localhost:3001"})
public class TarjetaController {

    @Autowired
    private TarjetaDAO tarjetaDAO;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetasPorUsuario(@PathVariable int usuarioId) {
        List<Tarjeta> tarjetas = tarjetaDAO.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(tarjetas);
    }

    @PostMapping
    public ResponseEntity<Tarjeta> crearTarjeta(@RequestBody Tarjeta tarjeta) {
        Tarjeta tarjetaGuardada = tarjetaDAO.guardar(tarjeta);
        return ResponseEntity.ok(tarjetaGuardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarjeta(@PathVariable int id) {
        tarjetaDAO.eliminar(id);
        return ResponseEntity.ok().build();
    }
} 