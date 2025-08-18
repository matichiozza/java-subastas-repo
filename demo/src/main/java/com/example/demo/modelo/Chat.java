package com.example.demo.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Chat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "PUBLICACION_ID", nullable = false)
    @JsonIgnoreProperties({"ofertas", "imagenes"})
    private Publicacion publicacion;
    
    @ManyToOne
    @JoinColumn(name = "VENDEDOR_ID", nullable = false)
    @JsonIgnoreProperties({"tarjetas", "password", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"})
    private Usuario vendedor;
    
    @ManyToOne
    @JoinColumn(name = "GANADOR_ID", nullable = false)
    @JsonIgnoreProperties({"tarjetas", "password", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"})
    private Usuario ganador;
    
    @Column(name = "FECHA_CREACION")
    private Date fechaCreacion;
    
    @Column(name = "ESTADO")
    private String estado = "ACTIVO"; // ACTIVO, CERRADO
    
    public Chat() {
        this.fechaCreacion = new Date();
    }
    
    public Chat(Publicacion publicacion, Usuario vendedor, Usuario ganador) {
        this();
        this.publicacion = publicacion;
        this.vendedor = vendedor;
        this.ganador = ganador;
    }
    
    // Getters y setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Publicacion getPublicacion() {
        return publicacion;
    }
    
    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }
    
    public Usuario getVendedor() {
        return vendedor;
    }
    
    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }
    
    public Usuario getGanador() {
        return ganador;
    }
    
    public void setGanador(Usuario ganador) {
        this.ganador = ganador;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
