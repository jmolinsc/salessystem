package com.salessystem.model;

public enum EstatusVenta {
    PENDIENTE("Pendiente"),
    CONCLUIDO("Concluido"),
    SIN_AFECTAR("Sin Afectar"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstatusVenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
