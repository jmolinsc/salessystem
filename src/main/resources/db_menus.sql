-- Script para crear las tablas del sistema de menús dinámicos
-- Ejecutar después de las tablas existentes de usuarios y roles

-- Tabla de menús
CREATE TABLE IF NOT EXISTS menus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ruta VARCHAR(255) NOT NULL,
    icono VARCHAR(100),
    orden_menu INT NOT NULL DEFAULT 0,
    es_padre BOOLEAN DEFAULT FALSE,
    menu_padre_id BIGINT,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (menu_padre_id) REFERENCES menus(id) ON DELETE CASCADE
);

-- Tabla de relación roles-menús (muchos a muchos)
CREATE TABLE IF NOT EXISTS rol_menus (
    rol_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (rol_id, menu_id),
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE
);

-- Índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_menus_ruta ON menus(ruta);
CREATE INDEX IF NOT EXISTS idx_menus_activo ON menus(activo);
CREATE INDEX IF NOT EXISTS idx_menus_orden ON menus(orden_menu);
CREATE INDEX IF NOT EXISTS idx_menus_padre ON menus(menu_padre_id, activo, orden_menu);

-- Insertar menús iniciales (se ejecutará automáticamente por DataInitializer)
-- Este script está incluido solo como referencia
