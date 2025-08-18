package com.example.demo.datos;

import com.example.demo.modelo.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
    
    @Query("SELECT m FROM Mensaje m WHERE m.chat.id = :chatId ORDER BY m.fechaEnvio")
    List<Mensaje> findByChatIdOrderByFechaEnvio(@Param("chatId") Integer chatId);
    
    @Query("SELECT m FROM Mensaje m WHERE m.chat.id = :chatId AND m.leido = false AND m.emisor.id != :emisorId")
    List<Mensaje> findByChatIdAndLeidoFalseAndEmisorIdNot(@Param("chatId") Integer chatId, @Param("emisorId") Integer emisorId);
}
