package com.example.demo.modelo;

import java.util.Date;

public class PublicacionRequest {
    private String titulo;
    private String descripcion;
    private Categoria categoria;
    private String condicion;
    private float precioInicial;
    private float incrementoMinimo;
    private Date fechaFin;

    // Getters y setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getCondicion() { return condicion; }
    public void setCondicion(String condicion) { this.condicion = condicion; }

    public float getPrecioInicial() { return precioInicial; }
    public void setPrecioInicial(float precioInicial) { this.precioInicial = precioInicial; }

    public float getIncrementoMinimo() { return incrementoMinimo; }
    public void setIncrementoMinimo(float incrementoMinimo) { this.incrementoMinimo = incrementoMinimo; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
} 