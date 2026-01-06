package com.salessystem.dto;

public class ClienteDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;

    public ClienteDto() {}

    public ClienteDto(Long id, String nombre, String apellido, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }
}

