package com.example.demo.datos;

import com.example.demo.modelo.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Integer> {
    List<Oferta> findByPublicacionIdOrderByFechaDesc(Integer publicacionId);
    Optional<Oferta> findFirstByPublicacionIdAndUsuarioIdOrderByFechaDesc(Integer publicacionId, Integer usuarioId);
    List<Oferta> findByPublicacionIdAndUsuarioId(Integer publicacionId, Integer usuarioId);
}
