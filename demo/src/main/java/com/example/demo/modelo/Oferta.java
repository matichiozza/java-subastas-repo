package com.example.demo.modelo;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "MONTO")
    private float monto;

    @Column(name = "FECHA")
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "PUBLICACION_ID", nullable = false)
    private Publicacion publicacion;

    public Oferta(float monto, LocalDate fecha, Usuario usuario, Publicacion publicacion) {
        this.monto = monto;
        this.fecha = fecha;
        this.usuario = usuario;
        this.publicacion = publicacion;
    }

    public Oferta() {

    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }
}
