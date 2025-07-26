package com.example.demo.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarjeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String fechaVencimiento; // MM/AA o MM/YYYY

    @Column(nullable = false)
    private String codigoSeguridad; // CVV

    @Column(nullable = false)
    private String dniTitular;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
} 