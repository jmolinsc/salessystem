package com.salessystem.model;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;

@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroFactura;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha;
    
    private BigDecimal total;
    
    // Nuevos campos agregados
    @ManyToOne
    @JoinColumn(name = "tipo_documento_id")
    private TipoDocumento mov;
    
    @Column(name = "mov_id")
    private Long movId;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal descuento;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CondicionPago condicion;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstatusVenta estatus;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DetalleVenta> detalles = new ArrayList<>();

    // Constructores
    public Venta() {
        this.fechaEmision = LocalDate.now();
        this.estatus = EstatusVenta.PENDIENTE;
        this.condicion = CondicionPago.CONTADO;
        this.descuento = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    // Getters y setters para nuevos campos
    public TipoDocumento getMov() {
        return mov;
    }

    public void setMov(TipoDocumento mov) {
        this.mov = mov;
    }

    public Long getMovId() {
        return movId;
    }

    public void setMovId(Long movId) {
        this.movId = movId;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public CondicionPago getCondicion() {
        return condicion;
    }

    public void setCondicion(CondicionPago condicion) {
        this.condicion = condicion;
    }

    public EstatusVenta getEstatus() {
        return estatus;
    }

    public void setEstatus(EstatusVenta estatus) {
        this.estatus = estatus;
    }

    // Getters, setters, constructores
}