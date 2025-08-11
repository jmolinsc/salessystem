package com.salessystem.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;

@Embeddable
public class RolMenuItemId implements Serializable {
    
    @Column(name = "rol_id")
    private Long rolId;
    
    @Column(name = "menu_item_id")
    private Long menuItemId;

    // Constructores
    public RolMenuItemId() {
    }
    
    public RolMenuItemId(Long rolId, Long menuItemId) {
        this.rolId = rolId;
        this.menuItemId = menuItemId;
    }
    
    // Getters y setters
    public Long getRolId() {
        return rolId;
    }
    
    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }
    
    public Long getMenuItemId() {
        return menuItemId;
    }
    
    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolMenuItemId that = (RolMenuItemId) o;
        return Objects.equals(rolId, that.rolId) && 
               Objects.equals(menuItemId, that.menuItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rolId, menuItemId);
    }
}

