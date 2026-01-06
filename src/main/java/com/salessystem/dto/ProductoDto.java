package com.salessystem.dto;

import java.math.BigDecimal;

public class ProductoDto {
    private Long id;
    private String nombre;
    private BigDecimal precio;

    public ProductoDto() {}

    public ProductoDto(Long id, String nombre, BigDecimal precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}

