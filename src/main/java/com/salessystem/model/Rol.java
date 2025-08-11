package com.salessystem.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
    
    @Column(length = 255)
    private String descripcion;
    
    @Column(nullable = false)
    private Boolean activo = true;

    // Relación con permisos
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rol_permisos",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();

    // Relación con menús
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rol_menus",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    private Set<Menu> menus = new HashSet<>();

    // Relación con usuarios
    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioRol> usuarios = new HashSet<>();

    // Constructors
    public Rol() {}
    
    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters and Setters
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
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public Set<Permiso> getPermisos() {
        return permisos;
    }
    
    public void setPermisos(Set<Permiso> permisos) {
        this.permisos = permisos;
    }

    public Set<Menu> getMenus() {
        return menus;
    }

    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
    }

    public Set<UsuarioRol> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<UsuarioRol> usuarios) {
        this.usuarios = usuarios;
    }
    
    // Helper methods
    public void addPermiso(Permiso permiso) {
        this.permisos.add(permiso);
    }
    
    public void removePermiso(Permiso permiso) {
        this.permisos.remove(permiso);
    }
    
    public void addMenu(Menu menu) {
        this.menus.add(menu);
    }
    
    public void removeMenu(Menu menu) {
        this.menus.remove(menu);
    }
}