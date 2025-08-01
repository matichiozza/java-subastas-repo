package com.example.demo.datos;

import com.example.demo.modelo.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Integer> {
    List<Oferta> findByPublicacionIdOrderByFechaDesc(Integer publicacionId);
    Optional<Oferta> findFirstByPublicacionIdAndUsuarioIdOrderByFechaDesc(Integer publicacionId, Integer usuarioId);
    List<Oferta> findByPublicacionIdAndUsuarioId(Integer publicacionId, Integer usuarioId);
    List<Oferta> findByUsuarioId(Integer usuarioId);
    
    @Query("SELECT o FROM Oferta o WHERE o.usuario.id = :usuarioId AND o.monto = (SELECT MAX(o2.monto) FROM Oferta o2 WHERE o2.publicacion.id = o.publicacion.id AND o2.usuario.id = :usuarioId)")
    List<Oferta> findHighestOfertaByUsuarioPerPublicacion(@Param("usuarioId") Integer usuarioId);
}
