package com.example.demo.controlador;

import com.example.demo.datos.OfertaDAO;
import com.example.demo.datos.OfertaRepository;
import com.example.demo.modelo.Oferta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ofertas")
@CrossOrigin(origins = "*")
public class OfertaController {

    @Autowired
    private OfertaDAO ofertaDAO;

    @Autowired
    private OfertaRepository ofertaRepository;

    @GetMapping("/mis-ofertas")
    public ResponseEntity<List<Oferta>> getMisOfertas() {
        try {
            // Por ahora, devolver todas las ofertas para testing
            // En producción, esto debería filtrar por usuario autenticado
            List<Oferta> ofertas = ofertaDAO.getAllOfertas();
            
            // Ordenar por fecha descendente (más recientes primero)
            ofertas.sort((o1, o2) -> o2.getFecha().compareTo(o1.getFecha()));
            
            return ResponseEntity.ok(ofertas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/publicacion/{publicacionId}")
    public ResponseEntity<List<Oferta>> getOfertasByPublicacion(@PathVariable Integer publicacionId) {
        try {
            List<Oferta> ofertas = ofertaRepository.findByPublicacionIdOrderByFechaDesc(publicacionId);
            return ResponseEntity.ok(ofertas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Oferta> getOfertaById(@PathVariable Integer id) {
        try {
            return ofertaDAO.getOfertaById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Oferta> crearOferta(@RequestBody Oferta oferta) {
        try {
            Oferta nuevaOferta = ofertaDAO.agregarOferta(oferta);
            return ResponseEntity.ok(nuevaOferta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Oferta> actualizarOferta(@PathVariable Integer id, @RequestBody Oferta oferta) {
        try {
            oferta.setId(id);
            Oferta ofertaActualizada = ofertaDAO.actualizarOferta(oferta);
            if (ofertaActualizada != null) {
                return ResponseEntity.ok(ofertaActualizada);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOferta(@PathVariable Integer id) {
        try {
            ofertaDAO.eliminarOferta(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 