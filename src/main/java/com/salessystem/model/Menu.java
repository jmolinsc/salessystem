package com.salessystem.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menus")
public class Menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String ruta;
    
    @Column
    private String icono;
    
    @Column(name = "orden_menu", nullable = false)
    private Integer orden = 0;
    
    @Column(name = "es_padre")
    private Boolean esPadre = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_padre_id")
    private Menu menuPadre;
    
    @OneToMany(mappedBy = "menuPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Menu> subMenus = new ArrayList<>();
    
    @ManyToMany(mappedBy = "menus")
    private List<Rol> roles = new ArrayList<>();
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Constructores
    public Menu() {}
    
    public Menu(String nombre, String ruta, String icono, Integer orden, Boolean esPadre) {
        this.nombre = nombre;
        this.ruta = ruta;
        this.icono = icono;
        this.orden = orden;
        this.esPadre = esPadre;
    }
    
    // Getters y Setters
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
    
    public String getRuta() {
        return ruta;
    }
    
    public void setRuta(String ruta) {
        this.ruta = ruta;
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
    
    public Boolean getEsPadre() {
        return esPadre;
    }
    
    public void setEsPadre(Boolean esPadre) {
        this.esPadre = esPadre;
    }
    
    public Menu getMenuPadre() {
        return menuPadre;
    }
    
    public void setMenuPadre(Menu menuPadre) {
        this.menuPadre = menuPadre;
    }
    
    public List<Menu> getSubMenus() {
        return subMenus;
    }
    
    public void setSubMenus(List<Menu> subMenus) {
        this.subMenus = subMenus;
    }
    
    public List<Rol> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    // Métodos de conveniencia
    public void addSubMenu(Menu subMenu) {
        subMenus.add(subMenu);
        subMenu.setMenuPadre(this);
    }
    
    public void removeSubMenu(Menu subMenu) {
        subMenus.remove(subMenu);
        subMenu.setMenuPadre(null);
    }
    
    public boolean hasSubMenus() {
        return !subMenus.isEmpty();
    }
    
    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", ruta='" + ruta + '\'' +
                ", orden=" + orden +
                ", esPadre=" + esPadre +
                '}';
    }
}
