package com.example.demo.datos;

import com.example.demo.modelo.Oferta;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OfertaDAO {

    @Autowired
    private OfertaRepository ofertaRepository;

    public List<Oferta> getAllOfertas() {
        return ofertaRepository.findAll();
    }

    public Optional<Oferta> getOfertaById(Integer id) {
        return ofertaRepository.findById(id);
    }

    @Transactional
    public Oferta agregarOferta(Oferta oferta) {
        return ofertaRepository.save(oferta);
    }

    @Transactional
    public Oferta actualizarOferta(Oferta oferta) {
        if (ofertaRepository.existsById(oferta.getId())) {
            return ofertaRepository.save(oferta);
        }
        return null;
    }

    @Transactional
    public void eliminarOferta(Integer id) {
        if (ofertaRepository.existsById(id)) {
            ofertaRepository.deleteById(id);
        } else {
            throw new RuntimeException("Oferta con ID " + id + " no encontrada.");
        }
    }
}