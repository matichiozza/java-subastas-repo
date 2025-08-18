package com.example.demo.controlador;

import com.example.demo.datos.ChatRepository;
import com.example.demo.datos.MensajeRepository;
import com.example.demo.datos.UsuarioRepository;
import com.example.demo.modelo.Chat;
import com.example.demo.modelo.Mensaje;
import com.example.demo.modelo.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chats")
@CrossOrigin(originPatterns = {"http://localhost:3000", "http://localhost:3001"})
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatRepository chatRepository;
    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @GetMapping("/mis-chats")
    public ResponseEntity<List<Chat>> getMisChats(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        Usuario usuario = usuarioOpt.get();
        List<Chat> chats = chatRepository.findByVendedorIdOrGanadorIdOrderByFechaCreacionDesc(usuario.getId());
        
        return ResponseEntity.ok(chats);
    }
    
    @GetMapping("/{chatId}/mensajes")
    public ResponseEntity<List<Mensaje>> getMensajes(@PathVariable Integer chatId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        // Verificar acceso al chat primero
        if (!chatRepository.existsByIdAndVendedorIdOrGanadorId(chatId, usuarioOpt.get().getId())) {
            return ResponseEntity.status(403).build();
        }
        
        // Obtener mensajes del chat
        List<Mensaje> mensajes = mensajeRepository.findByChatIdOrderByFechaEnvio(chatId);
        
        // Marcar mensajes como leídos (solo los del otro usuario)
        List<Mensaje> mensajesNoLeidos = mensajeRepository.findByChatIdAndLeidoFalseAndEmisorIdNot(chatId, usuarioOpt.get().getId());
        if (!mensajesNoLeidos.isEmpty()) {
            mensajesNoLeidos.forEach(m -> m.setLeido(true));
            mensajeRepository.saveAll(mensajesNoLeidos);
        }
        
        return ResponseEntity.ok(mensajes);
    }
    
    @PostMapping("/{chatId}/mensajes")
    public ResponseEntity<?> enviarMensaje(
            @PathVariable Integer chatId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        String contenido = request.get("contenido");
        if (contenido == null || contenido.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El mensaje no puede estar vacío");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Chat chat = chatOpt.get();
        Usuario usuario = usuarioOpt.get();
        
        // Verificar que el usuario sea parte del chat
        if (!chat.getVendedor().getId().equals(usuario.getId()) && 
            !chat.getGanador().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        // Crear y guardar mensaje
        Mensaje mensaje = new Mensaje(chat, usuario, contenido.trim());
        mensajeRepository.save(mensaje);
        
        // Notificar via WebSocket
        messagingTemplate.convertAndSend("/topic/chat." + chatId, mensaje);
        
        return ResponseEntity.ok(mensaje);
    }
    
    @PostMapping("/{chatId}/escribiendo")
    public ResponseEntity<?> indicarEscribiendo(
            @PathVariable Integer chatId,
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        System.out.println("📝 Backend: Recibida señal de escritura para chat " + chatId);
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Chat chat = chatOpt.get();
        Usuario usuario = usuarioOpt.get();
        
        System.out.println("📝 Backend: Usuario " + usuario.getId() + " enviando señal de escritura: " + request.get("escribiendo"));
        
        // Verificar que el usuario sea parte del chat
        if (!chat.getVendedor().getId().equals(usuario.getId()) && 
            !chat.getGanador().getId().equals(usuario.getId())) {
            System.out.println("📝 Backend: Usuario " + usuario.getId() + " no tiene acceso al chat " + chatId);
            return ResponseEntity.status(403).build();
        }
        
        System.out.println("📝 Backend: Enviando señal al topic /topic/chat." + chatId + ".escribiendo");
        
        // Enviar señal de "escribiendo" via WebSocket
        messagingTemplate.convertAndSend("/topic/chat." + chatId + ".escribiendo", Map.of(
            "usuarioId", usuario.getId(),
            "escribiendo", request.get("escribiendo")
        ));
        
        System.out.println("📝 Backend: Señal enviada exitosamente");
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/publicacion/{publicacionId}")
    public ResponseEntity<Chat> getChatPorPublicacion(@PathVariable Integer publicacionId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<Chat> chatOpt = chatRepository.findByPublicacionId(publicacionId);
        if (chatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Chat chat = chatOpt.get();
        Usuario usuario = usuarioOpt.get();
        
        // Verificar que el usuario sea parte del chat
        if (!chat.getVendedor().getId().equals(usuario.getId()) && 
            !chat.getGanador().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(chat);
    }
}
