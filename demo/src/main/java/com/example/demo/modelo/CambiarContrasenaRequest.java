package com.example.demo.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CambiarContrasenaRequest {
    private String contrasenaActual;
    private String nuevaContrasena;
} 