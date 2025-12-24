package com.salessystem.dto;

import com.salessystem.model.Comportamiento;

public class ComportamientoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipo;
    private Boolean afectaStock;
    private Integer signoMovimiento;
    private String estatus;

    // Constructor desde entidad
    public ComportamientoDTO(Comportamiento comportamiento) {
        this.id = comportamiento.getId();
        this.codigo = comportamiento.getCodigo();
        this.nombre = comportamiento.getNombre();
        this.descripcion = comportamiento.getDescripcion();
        this.tipo = comportamiento.getTipo();
        this.afectaStock = comportamiento.getAfectaStock();
        this.signoMovimiento = comportamiento.getSignoMovimiento();
        this.estatus = comportamiento.getEstatus();
    }

    // Getters (no necesitas setters si solo es para lectura)
    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getTipo() { return tipo; }
    public Boolean getAfectaStock() { return afectaStock; }
    public Integer getSignoMovimiento() { return signoMovimiento; }
    public String getEstatus() { return estatus; }
}