package com.example.demo.modelo;

import jakarta.persistence.*;
import java.util.Date;
import com.example.demo.modelo.Categoria;
import java.util.List;

@Entity
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "TITULO", length = 200)
    private String titulo;

    @Column(name = "DESCRIPCION", length = 3000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORIA")
    private Categoria categoria;

    @Column(name = "ESTADO", length = 50)
    private String estado = "ACTIVO";

    @Column(name = "FECHA_INICIO")
    private Date fechaInicio;

    @Column(name = "FECHA_FIN")
    private Date fechaFin;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "publicacion_imagenes", joinColumns = @JoinColumn(name = "publicacion_id"))
    @Column(name = "imagen")
    private List<String> imagenes;

    @Column(name = "CONDICION", length = 100)
    private String condicion;

    @Column(name = "PRECIO_INICIAL")
    private float precioInicial;

    @Column(name = "PRECIO_ACTUAL")
    private float precioActual;

    @Column(name = "INCREMENTO_MINIMO")
    private float incrementoMinimo;

    @Column(name = "OFERTAS_TOTALES")
    private int ofertasTotales;

    @Column(name = "LATITUD")
    private Float latitud;

    @Column(name = "LONGITUD")
    private Float longitud;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "GANADOR_ID")
    private Usuario ganador;

    @Enumerated(EnumType.STRING)
    @Column(name = "MOTIVO_CANCELACION")
    private MotivoCancelacion motivoCancelacion;

    @Column(name = "COMENTARIO_CANCELACION", length = 500)
    private String comentarioCancelacion;

    public Publicacion(String titulo, String descripcion, Categoria categoria, String estado, Date fechaInicio, Date fechaFin, String condicion, float precioInicial, float incrementoMinimo, Usuario usuario) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.condicion = condicion;
        this.precioInicial = precioInicial;
        this.incrementoMinimo = incrementoMinimo;
        this.usuario = usuario;
    }

    public Publicacion() {

    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public float getPrecioInicial() {
        return precioInicial;
    }

    public void setPrecioInicial(float precioInicial) {
        this.precioInicial = precioInicial;
    }

    public float getPrecioActual() {
        return precioActual;
    }

    public void setPrecioActual(float precioActual) {
        this.precioActual = precioActual;
    }

    public float getIncrementoMinimo() {
        return incrementoMinimo;
    }

    public void setIncrementoMinimo(float incrementoMinimo) {
        this.incrementoMinimo = incrementoMinimo;
    }

    public int getOfertasTotales() {
        return ofertasTotales;
    }

    public void setOfertasTotales(int ofertasTotales) {
        this.ofertasTotales = ofertasTotales;
    }

    public Float getLatitud() {
        return latitud;
    }

    public void setLatitud(Float latitud) {
        this.latitud = latitud;
    }

    public Float getLongitud() {
        return longitud;
    }

    public void setLongitud(Float longitud) {
        this.longitud = longitud;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getGanador() {
        return ganador;
    }

    public void setGanador(Usuario ganador) {
        this.ganador = ganador;
    }

    public MotivoCancelacion getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(MotivoCancelacion motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public String getComentarioCancelacion() {
        return comentarioCancelacion;
    }

    public void setComentarioCancelacion(String comentarioCancelacion) {
        this.comentarioCancelacion = comentarioCancelacion;
    }
}
