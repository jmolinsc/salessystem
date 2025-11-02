package com.salessystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder los 50 caracteres")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)\\.]{7,15}$",
            message = "El teléfono debe tener entre 7 y 15 dígitos (se permiten espacios, guiones, paréntesis y puntos)")
    @Column(nullable = false)
    private String telefono;

    @Size(max = 200, message = "La dirección no puede exceder los 200 caracteres")
    @Column(nullable = false)
    private String direccion;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "codigo", length = 50, unique = true)
    private String codigo;

    @Column(name = "pais", length = 50, nullable = true)
    private String pais;

    @Column(name = "departamento", length = 50, nullable = true)
    private String departamento;

    @Column(name = "municipio", length = 50, nullable = true)
    private String municipio;


    @Column(name = "nrc", length = 50, nullable = true)
    private String nrc;

    @Column(name = "dui", length = 50, nullable = true)
    private String dui;

    @Column(name = "nit", length = 50, nullable = true)
    private String nit;

    @Column(name = "estatus", length = 50, nullable = true)
    private String estatus;

    @Column(name = "zonaimpuestos", length = 50, nullable = true)
    private String zonaimpuestos;

    @Column(name = "condicion", length = 50, nullable = true)
    private String condicion;




    // Constructores
    public Cliente() {}

    public Cliente(String nombre, String apellido, String email, String telefono, String direccion, String pais, String codigo, String nrc,
                   String dui, String nit, String estatus, String zonaimpuestos, String condicion,String departamento, String municipio) {
        this.departamento = departamento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.pais = pais;
        this.codigo = codigo;
        this.nrc = nrc;
        this.dui = dui;
        this.nit = nit;
        this.estatus = estatus;
        this.zonaimpuestos = zonaimpuestos;
        this.condicion = condicion;
        this.municipio = municipio;
        this.departamento=departamento;

    }

    // Getters y Setters


    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getZonaimpuestos() {
        return zonaimpuestos;
    }

    public void setZonaimpuestos(String zonaimpuestos) {
        this.zonaimpuestos = zonaimpuestos;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", codigo='" + codigo + '\'' +
                ", pais='" + pais + '\'' +
                ", nrc='" + nrc + '\'' +
                ", dui='" + dui + '\'' +
                ", nit='" + nit + '\'' +
                ", estatus='" + estatus + '\'' +
                ", zonaimpuestos='" + zonaimpuestos + '\'' +
                ", condicion='" + condicion + '\'' +
                '}';
    }
}
