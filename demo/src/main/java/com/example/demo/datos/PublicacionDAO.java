package com.example.demo.datos;

import com.example.demo.modelo.Publicacion;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PublicacionDAO {

    @Autowired
    private PublicacionRepository publicacionRepository;

    public List<Publicacion> getAllPublicaciones() {
        return publicacionRepository.findAll();
    }

    public Optional<Publicacion> getPublicacionById(Integer id) {
        return publicacionRepository.findById(id);
    }

    @Transactional
    public Publicacion agregarPublicacion(Publicacion publicacion) {
        return publicacionRepository.save(publicacion);
    }

    @Transactional
    public Publicacion actualizarPublicacion(Publicacion publicacion) {
        if (publicacionRepository.existsById(publicacion.getId())) {
            return publicacionRepository.save(publicacion);
        }
        return null;
    }

    @Transactional
    public void eliminarPublicacion(Integer id) {
        if (publicacionRepository.existsById(id)) {
            publicacionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Publicación con ID " + id + " no encontrada.");
        }
    }
}