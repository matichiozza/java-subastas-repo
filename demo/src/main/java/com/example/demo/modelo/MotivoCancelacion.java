package com.example.demo.modelo;

public enum MotivoCancelacion {
    PRODUCTO_NO_DISPONIBLE("Producto no disponible"),
    ERROR_EN_PUBLICACION("Error en la publicación"),
    VENTA_PRIVADA("Venta privada"),
    PRODUCTO_DANADO("Producto dañado"),
    CAMBIO_DE_PRECIO("Cambio de precio"),
    OTRO("Otro motivo");

    private final String descripcion;

    MotivoCancelacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
} 