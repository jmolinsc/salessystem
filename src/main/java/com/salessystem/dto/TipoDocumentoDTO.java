package com.salessystem.dto;

import com.salessystem.model.TipoDocumento;

public class TipoDocumentoDTO {
    private Long id;
    private String mov;
    private String descripcion;
    private String modulo;
    private ComportamientoDTO comportamiento; // Usa DTO, no entidad

    // Constructor desde entidad
    public TipoDocumentoDTO(TipoDocumento tipoDocumento) {
        this.id = tipoDocumento.getId();
        this.mov = tipoDocumento.getMov();
        this.descripcion = tipoDocumento.getDescripcion();
        this.modulo = tipoDocumento.getModulo();

        if (tipoDocumento.getComportamiento() != null) {
            this.comportamiento = new ComportamientoDTO(tipoDocumento.getComportamiento());
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getMov() { return mov; }
    public String getDescripcion() { return descripcion; }
    public String getModulo() { return modulo; }
    public ComportamientoDTO getComportamiento() { return comportamiento; }
}