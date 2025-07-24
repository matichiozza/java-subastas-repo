package com.example.demo.modelo;

import java.util.List;

public class OfertaResponse {
    private Publicacion publicacionActualizada;
    private List<Oferta> ofertas;

    public OfertaResponse(Publicacion publicacionActualizada, List<Oferta> ofertas) {
        this.publicacionActualizada = publicacionActualizada;
        this.ofertas = ofertas;
    }

    public Publicacion getPublicacionActualizada() {
        return publicacionActualizada;
    }
    public void setPublicacionActualizada(Publicacion publicacionActualizada) {
        this.publicacionActualizada = publicacionActualizada;
    }
    public List<Oferta> getOfertas() {
        return ofertas;
    }
    public void setOfertas(List<Oferta> ofertas) {
        this.ofertas = ofertas;
    }
} 