package com.salessystem.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
public class UsuarioRolId implements Serializable {
    private Long usuarioId;
    private Long rolId;

    public UsuarioRolId() {}
    public UsuarioRolId(Long usuarioId, Long rolId) {
        this.usuarioId = usuarioId;
        this.rolId = rolId;
    }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioRolId that = (UsuarioRolId) o;
        return usuarioId != null ? usuarioId.equals(that.usuarioId) : that.usuarioId == null &&
               rolId != null ? rolId.equals(that.rolId) : that.rolId == null;
    }

    @Override
    public int hashCode() {
        int result = usuarioId != null ? usuarioId.hashCode() : 0;
        result = 31 * result + (rolId != null ? rolId.hashCode() : 0);
        return result;
    }
}
