package com.salessystem.dto;

import java.math.BigDecimal;

public class DetalleVentaDto {
    private Long productoId;
    private String productoNombre;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetalleVentaDto() {}

    public DetalleVentaDto(Long productoId, String productoNombre, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public Long getProductoId() {
        return productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}

