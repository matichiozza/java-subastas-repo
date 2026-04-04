package com.example.demo.datos;

import com.example.demo.modelo.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @Query("SELECT DISTINCT c FROM Chat c LEFT JOIN FETCH c.vendedor LEFT JOIN FETCH c.ganador LEFT JOIN FETCH c.publicacion WHERE c.publicacion.id = :publicacionId")
    Optional<Chat> findByPublicacionIdWithRelations(@Param("publicacionId") Integer publicacionId);
    
    @Query("SELECT c FROM Chat c LEFT JOIN FETCH c.vendedor LEFT JOIN FETCH c.ganador LEFT JOIN FETCH c.publicacion WHERE c.publicacion.id = :publicacionId AND (c.vendedor.id = :usuarioId OR c.ganador.id = :usuarioId)")
    Optional<Chat> findByPublicacionIdAndUsuarioIdWithRelations(@Param("publicacionId") Integer publicacionId, @Param("usuarioId") Integer usuarioId);
    
    @Query("SELECT c FROM Chat c LEFT JOIN FETCH c.vendedor LEFT JOIN FETCH c.ganador WHERE c.vendedor.id = :usuarioId OR c.ganador.id = :usuarioId ORDER BY c.fechaCreacion DESC")
    List<Chat> findByVendedorIdOrGanadorIdOrderByFechaCreacionDesc(@Param("usuarioId") Integer usuarioId);

    // Método para verificar acceso a un chat específico
    @Query("SELECT COUNT(c) > 0 FROM Chat c WHERE c.id = :chatId AND (c.vendedor.id = :usuarioId OR c.ganador.id = :usuarioId)")
    boolean existsByIdAndVendedorIdOrGanadorId(@Param("chatId") Integer chatId, @Param("usuarioId") Integer usuarioId);
}
