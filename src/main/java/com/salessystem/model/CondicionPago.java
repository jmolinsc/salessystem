package com.salessystem.model;

public enum CondicionPago {
    CONTADO("Contado"),
    CREDITO("Crédito"),
    TRANSFERENCIA("Transferencia");

    private final String descripcion;

    CondicionPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
    
    public static CondicionPago fromDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return CONTADO; // Valor por defecto
        }
        
        for (CondicionPago condicion : values()) {
            if (condicion.getDescripcion().equalsIgnoreCase(descripcion.trim())) {
                return condicion;
            }
        }
        
        // Si no se encuentra por descripción, intentar por nombre del enum
        try {
            return valueOf(descripcion.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CONTADO; // Valor por defecto si no se encuentra
        }
    }
}
