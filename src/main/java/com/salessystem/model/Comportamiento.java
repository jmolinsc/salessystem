package com.salessystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "comportamientos")
public class Comportamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo; // ENTRADA, SALIDA, NEUTRO, FACTURA, DEVOLUCION, PEDIDO

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(length = 50)
    private String tipo; // INVENTARIO, VENTAS, GENERAL

    @Column(name = "afecta_stock")
    private Boolean afectaStock; // true = afecta inventario, false = no afecta

    @Column(name = "signo_movimiento")
    private Integer signoMovimiento; // 1 = aumenta stock, -1 = disminuye stock, 0 = neutro

    @Column(length = 50)
    private String estatus;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "comportamiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TipoDocumento> tiposDocumento;

    public Set<TipoDocumento> getTiposDocumento() {
        return tiposDocumento;
    }

    public void setTiposDocumento(Set<TipoDocumento> tiposDocumento) {
        this.tiposDocumento = tiposDocumento;
    }

    // Constructores
    public Comportamiento() {
        this.fechaAlta = LocalDateTime.now();
        this.estatus = "ACTIVO";
        this.afectaStock = false;
        this.signoMovimiento = 0;
    }

    public Comportamiento(String codigo, String nombre, String tipo, Boolean afectaStock, Integer signoMovimiento) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.afectaStock = afectaStock;
        this.signoMovimiento = signoMovimiento;
    }

    public Comportamiento(String codigo, String nombre, String descripcion, String tipo, Boolean afectaStock,
            Integer signoMovimiento) {
        this(codigo, nombre, tipo, afectaStock, signoMovimiento);
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getAfectaStock() {
        return afectaStock;
    }

    public void setAfectaStock(Boolean afectaStock) {
        this.afectaStock = afectaStock;
    }

    public Integer getSignoMovimiento() {
        return signoMovimiento;
    }

    public void setSignoMovimiento(Integer signoMovimiento) {
        this.signoMovimiento = signoMovimiento;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDateTime fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    /*
     * public Set<TipoDocumento> getTiposDocumento() {
     * return tiposDocumento;
     * }
     * 
     * public void setTiposDocumento(Set<TipoDocumento> tiposDocumento) {
     * this.tiposDocumento = tiposDocumento;
     * }
     */

    // Métodos de utilidad
    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getSignoTexto() {
        if (signoMovimiento == null)
            return "Neutro";
        return switch (signoMovimiento) {
            case 1 -> "Positivo (+)";
            case -1 -> "Negativo (-)";
            default -> "Neutro (0)";
        };
    }

    public String getAfectaStockTexto() {
        return Boolean.TRUE.equals(afectaStock) ? "Sí" : "No";
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Comportamiento that))
            return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
