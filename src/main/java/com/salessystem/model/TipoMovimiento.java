package com.salessystem.model;

public enum TipoMovimiento {
    ENTRADA("Entrada"),
    SALIDA("Salida"),
    AJUSTE_POSITIVO("Ajuste Positivo"),
    AJUSTE_NEGATIVO("Ajuste Negativo"),
    VENTA("Venta"),
    DEVOLUCION("Devolución");
    
    private final String descripcion;
    
    TipoMovimiento(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
