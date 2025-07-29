package com.example.demo.modelo;

import lombok.Data;

@Data
public class CancelacionRequest {
    private MotivoCancelacion motivo;
    private String comentarioAdicional;
} 