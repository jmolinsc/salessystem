package com.salessystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tipos_documento")
public class TipoDocumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String mov;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(nullable = false, length = 10)
    private String modulo; // VTA, INV, etc.

     @ManyToOne
    @JoinColumn(name = "comportamiento_id")
    private Comportamiento comportamiento;

    @Column(length = 50)
    private String estatus;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

    // Constructores
    public TipoDocumento() {
        this.fechaAlta = LocalDateTime.now();
        this.estatus = "ACTIVO";
    }

    public TipoDocumento(String mov, String descripcion, String modulo, Comportamiento comportamiento) {
        this();
        this.mov = mov;
        this.descripcion = descripcion;
        this.modulo = modulo;
        this.comportamiento = comportamiento;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMov() {
        return mov;
    }

    public void setMov(String mov) {
        this.mov = mov;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Comportamiento getComportamiento() {
        return comportamiento;
    }

    public void setComportamiento(Comportamiento comportamiento) {
        this.comportamiento = comportamiento;
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

    @Override
    public String toString() {
        return descripcion;
    }
}
