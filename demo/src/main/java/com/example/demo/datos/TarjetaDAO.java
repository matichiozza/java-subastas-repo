package com.example.demo.datos;

import com.example.demo.modelo.Tarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TarjetaDAO {
    @Autowired
    private TarjetaRepository tarjetaRepository;

    public Tarjeta guardar(Tarjeta tarjeta) {
        return tarjetaRepository.save(tarjeta);
    }

    public Optional<Tarjeta> buscarPorId(int id) {
        return tarjetaRepository.findById(id);
    }

    public List<Tarjeta> buscarPorUsuario(int usuarioId) {
        return tarjetaRepository.findByUsuarioId(usuarioId);
    }

    public void eliminar(int id) {
        tarjetaRepository.deleteById(id);
    }
} 