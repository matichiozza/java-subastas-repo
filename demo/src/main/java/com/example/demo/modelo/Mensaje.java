package com.example.demo.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Mensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "CHAT_ID", nullable = false)
    @JsonIgnoreProperties({"vendedor", "ganador"})
    private Chat chat;
    
    @ManyToOne
    @JoinColumn(name = "EMISOR_ID", nullable = false)
    @JsonIgnoreProperties({"tarjetas", "password", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"})
    private Usuario emisor;
    
    @Column(name = "CONTENIDO", length = 1000)
    private String contenido;
    
    @Column(name = "FECHA_ENVIO")
    private Date fechaEnvio;
    
    @Column(name = "LEIDO")
    private boolean leido = false;
    
    public Mensaje() {
        this.fechaEnvio = new Date();
    }
    
    public Mensaje(Chat chat, Usuario emisor, String contenido) {
        this();
        this.chat = chat;
        this.emisor = emisor;
        this.contenido = contenido;
    }
    
    // Getters y setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Chat getChat() {
        return chat;
    }
    
    public void setChat(Chat chat) {
        this.chat = chat;
    }
    
    public Usuario getEmisor() {
        return emisor;
    }
    
    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }
    
    public String getContenido() {
        return contenido;
    }
    
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    
    public Date getFechaEnvio() {
        return fechaEnvio;
    }
    
    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
    
    public boolean isLeido() {
        return leido;
    }
    
    public void setLeido(boolean leido) {
        this.leido = leido;
    }
}
