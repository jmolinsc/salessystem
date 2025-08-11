package com.salessystem.model;


import jakarta.persistence.*;



@Entity
@Table(name = "roles_menu_items")
public class RolMenuItem {

    @EmbeddedId
    private RolMenuItemId id;

    @ManyToOne
    @MapsId("rolId")
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToOne
    @MapsId("menuItemId")
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    // Constructores, getters, setters


    public RolMenuItemId getId() {
        return id;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public void setId(RolMenuItemId id) {
        this.id = id;
    }
}