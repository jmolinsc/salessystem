package com.salessystem.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String url;
    private String icono;
    private Integer orden;

    @ManyToOne
    @JoinColumn(name = "menu_padre_id")
    private MenuItem menuPadre;

    @OneToMany(mappedBy = "menuPadre")
    private Set<MenuItem> subItems;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolMenuItem> roles = new HashSet<>();

    // Getters, setters, constructores


    public MenuItem() {
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getIcono() {
        return icono;
    }
    public void setIcono(String icono) {
        this.icono = icono;
    }
    public Integer getOrden() {
        return orden;
    }
    public void setOrden(Integer orden) {
        this.orden = orden;
    }
    public MenuItem getMenuPadre() {
        return menuPadre;
    }
    public void setMenuPadre(MenuItem menuPadre) {
        this.menuPadre = menuPadre;
    }
    public Set<MenuItem> getSubItems() {
        return subItems;
    }
    public void setSubItems(Set<MenuItem> subItems) {
        this.subItems = subItems;
    }
    public Set<RolMenuItem> getRoles() {
        return roles;
    }
    public void setRoles(Set<RolMenuItem> roles) {
        this.roles = roles;
    }
    public MenuItem(Long id, String titulo, String url, String icono, Integer orden) {
        this.id = id;
        this.titulo = titulo;
        this.url = url;
        this.icono = icono;
        this.orden = orden;
    }

    public MenuItem(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Método helper para obtener nombre
    public String getNombre() {
        return this.titulo;
    }
}