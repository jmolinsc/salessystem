package com.salessystem.config;

import com.salessystem.model.*;
import com.salessystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component // HABILITADO: Ejecutar inicialización automática de datos
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PermisoService permisoService;

    @Autowired
    private RolService rolService;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private FabricanteService fabricanteService;
    
    @Autowired
    private TipoDocumentoService tipoDocumentoService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private ComportamientoService comportamientoService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Inicializar menús del sistema
        initializeMenus();
        
        // Inicializar permisos básicos
        permisoService.initializeDefaultPermissions();
        
        // Inicializar roles con permisos
        rolService.initializeDefaultRoles();
        
        // Asignar todos los menús al rol ADMIN después de crear los roles
        assignAllMenusToAdminRole();
        
        // Inicializar usuario administrador por defecto
        initializeDefaultAdmin();
        
        // Inicializar fabricantes de ejemplo
        initializeDefaultFabricantes();
        
        // Inicializar comportamientos
        initializeDefaultComportamientos();
        
        // Inicializar tipos de documento
        tipoDocumentoService.initializeDefaultTiposDocumento();
        
        // Inicializar categorías de ejemplo
        initializeDefaultCategorias();
        
        // Inicializar clientes de ejemplo
        initializeDefaultClientes();
        
        // Inicializar productos de ejemplo
        initializeDefaultProductos();
        
        System.out.println("✅ Menús, permisos, roles, usuario admin, fabricantes, comportamientos, tipos de documento, categorías, clientes y productos inicializados correctamente en la base de datos");
    }
    
    private void initializeMenus() {
        // Dashboard
        createMenuIfNotExists("Dashboard", "/dashboard", "fas fa-tachometer-alt", 1, false, null);
        
        // Ventas
        Long ventasMenuId = createMenuIfNotExists("Ventas", "/ventas", "fas fa-shopping-cart", 2, true, null);
        createMenuIfNotExists("Lista de Ventas", "/ventas/list", "fas fa-list", 1, false, ventasMenuId);
        createMenuIfNotExists("Nueva Venta", "/ventas/nuevo", "fas fa-plus", 2, false, ventasMenuId);
        createMenuIfNotExists("Detalle Venta", "/ventas/detail", "fas fa-eye", 3, false, ventasMenuId);
        
        // Clientes
        Long clientesMenuId = createMenuIfNotExists("Clientes", "/clientes", "fas fa-users", 3, true, null);
        createMenuIfNotExists("Lista de Clientes", "/clientes/list", "fas fa-list", 1, false, clientesMenuId);
        createMenuIfNotExists("Nuevo Cliente", "/clientes/nuevo", "fas fa-user-plus", 2, false, clientesMenuId);
        
        // Productos
        Long productosMenuId = createMenuIfNotExists("Productos", "/productos", "fas fa-box", 4, true, null);
        createMenuIfNotExists("Lista de Productos", "/productos/list", "fas fa-list", 1, false, productosMenuId);
        createMenuIfNotExists("Nuevo Producto", "/productos/nuevo", "fas fa-plus", 2, false, productosMenuId);
        
        // Categorías
        Long categoriasMenuId = createMenuIfNotExists("Categorías", "/categorias", "fas fa-tags", 5, true, null);
        createMenuIfNotExists("Lista de Categorías", "/categorias/list", "fas fa-list", 1, false, categoriasMenuId);
        createMenuIfNotExists("Nueva Categoría", "/categorias/nuevo", "fas fa-plus", 2, false, categoriasMenuId);
        
        // Fabricantes
        Long fabricantesMenuId = createMenuIfNotExists("Fabricantes", "/fabricantes", "fas fa-industry", 6, true, null);
        createMenuIfNotExists("Lista de Fabricantes", "/fabricantes/list", "fas fa-list", 1, false, fabricantesMenuId);
        createMenuIfNotExists("Nuevo Fabricante", "/fabricantes/nuevo", "fas fa-plus", 2, false, fabricantesMenuId);
        
        // Familias de Productos
        Long prodFamiliasMenuId = createMenuIfNotExists("Familias de Productos", "/prodfamilias", "fas fa-sitemap", 7, true, null);
        createMenuIfNotExists("Lista de Familias", "/prodfamilias/list", "fas fa-list", 1, false, prodFamiliasMenuId);
        createMenuIfNotExists("Nueva Familia", "/prodfamilias/nueva", "fas fa-plus", 2, false, prodFamiliasMenuId);
        
        // Inventario
        Long inventarioMenuId = createMenuIfNotExists("Inventario", "/inventario", "fas fa-warehouse", 8, true, null);
        createMenuIfNotExists("Dashboard Inventario", "/inventario", "fas fa-tachometer-alt", 1, false, inventarioMenuId);
        createMenuIfNotExists("Movimientos", "/inventario/movimientos", "fas fa-exchange-alt", 2, false, inventarioMenuId);
        createMenuIfNotExists("Entrada", "/inventario/entrada", "fas fa-arrow-down", 3, false, inventarioMenuId);
        createMenuIfNotExists("Salida", "/inventario/salida", "fas fa-arrow-up", 4, false, inventarioMenuId);
        createMenuIfNotExists("Ajuste", "/inventario/ajuste", "fas fa-balance-scale", 5, false, inventarioMenuId);
        createMenuIfNotExists("Alertas", "/inventario/alertas", "fas fa-exclamation-triangle", 6, false, inventarioMenuId);
        
        // Reportes
        createMenuIfNotExists("Reportes", "/reportes", "fas fa-chart-bar", 9, false, null);
        
        // Administración
        Long adminMenuId = createMenuIfNotExists("Administración", "/admin", "fas fa-cog", 10, true, null);
        createMenuIfNotExists("Usuarios", "/usuarios/web", "fas fa-users-cog", 1, false, adminMenuId);
        createMenuIfNotExists("Nuevo Usuario", "/usuarios/web/nuevo", "fas fa-plus", 2, false, adminMenuId);
        createMenuIfNotExists("Roles", "/usuarios/web/roles", "fas fa-user-tag", 3, false, adminMenuId);
        createMenuIfNotExists("Permisos", "/usuarios/web/permisos", "fas fa-key", 4, false, adminMenuId);
        createMenuIfNotExists("Menús", "/usuarios/web/menus", "fas fa-sitemap", 5, false, adminMenuId);
        createMenuIfNotExists("Tipos de Documento", "/tipos-documento", "fas fa-file-alt", 6, false, adminMenuId);
        
        // Comportamientos
        Long comportamientosMenuId = createMenuIfNotExists("Comportamientos", "/comportamientos", "fas fa-cogs", 7, true, adminMenuId);
        createMenuIfNotExists("Lista de Comportamientos", "/comportamientos/list", "fas fa-list", 1, false, comportamientosMenuId);
        createMenuIfNotExists("Nuevo Comportamiento", "/comportamientos/nuevo", "fas fa-plus", 2, false, comportamientosMenuId);
    }
    
    private Long createMenuIfNotExists(String nombre, String ruta, String icono, Integer orden, Boolean esPadre, Long menuPadreId) {
        // Buscar si el menú ya existe por nombre y ruta
        List<Menu> existingMenus = menuService.buscarMenus(nombre);
        for (Menu menu : existingMenus) {
            if (menu.getRuta().equals(ruta)) {
                return menu.getId();
            }
        }
        
        // Crear nuevo menú
        Menu menu = menuService.crearMenu(nombre, ruta, icono, orden, esPadre, menuPadreId);
        return menu.getId();
    }
    
    private void initializeDefaultAdmin() {
        Optional<Usuario> existingAdmin = usuarioService.findByUsername("admin");
        
        if (existingAdmin.isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@salessystem.com");
            admin.setNombre("Administrador Sistema");
            
            Usuario savedAdmin = usuarioService.save(admin);
            
            // Asignar rol ADMIN al usuario
            Optional<Rol> adminRole = rolService.findByNombre("ADMIN");
            if (adminRole.isPresent()) {
                usuarioService.addRolToUsuario(savedAdmin.getId(), adminRole.get().getId());
                System.out.println("✅ Rol ADMIN asignado al usuario admin");
            }
        } else {
            // Verificar si ya tiene el rol asignado
            Usuario admin = existingAdmin.get();
            Optional<Rol> adminRole = rolService.findByNombre("ADMIN");
            if (adminRole.isPresent()) {
                boolean hasAdminRole = admin.getRoles().stream()
                    .anyMatch(rol -> rol.getNombre().equals("ADMIN"));
                
                if (!hasAdminRole) {
                    usuarioService.addRolToUsuario(admin.getId(), adminRole.get().getId());
                    System.out.println("✅ Rol ADMIN asignado al usuario admin existente");
                }
            }
        }
    }
    
    private void assignAllMenusToAdminRole() {
        System.out.println("🔍 Verificando asignación de menús al rol ADMIN...");
        Optional<Rol> adminRole = rolService.findByNombre("ADMIN");
        System.out.println("🔍 Rol ADMIN encontrado: " + adminRole.isPresent());
        
        if (adminRole.isPresent()) {
            Rol admin = adminRole.get();
            
            // Solo asignar menús si el rol no tiene menús asignados
            if (admin.getMenus() == null || admin.getMenus().isEmpty()) {
                List<Menu> allMenus = menuService.findAll();
                System.out.println("🔍 Total de menús encontrados: " + allMenus.size());
                
                // Asignar todos los menús al rol ADMIN por primera vez
                admin.getMenus().addAll(allMenus);
                rolService.save(admin);
                System.out.println("✅ " + allMenus.size() + " menús asignados automáticamente al rol ADMIN (primera inicialización)");
            } else {
                System.out.println("🔍 El rol ADMIN ya tiene " + admin.getMenus().size() + " menús asignados. No se modifica.");
            }
        } else {
            System.out.println("❌ Rol ADMIN no encontrado en la base de datos");
        }
    }
    
    private void initializeDefaultFabricantes() {
        // Verificar si ya existen fabricantes
        List<Fabricante> fabricantesExistentes = fabricanteService.findAll();
        if (fabricantesExistentes.isEmpty()) {
            // Crear fabricantes de ejemplo
            createFabricanteIfNotExists("Samsung", "Corea del Sur", "Fabricante de electrónicos y tecnología", "https://www.samsung.com", "contact@samsung.com", "+82-2-2255-0114");
            createFabricanteIfNotExists("Apple", "Estados Unidos", "Fabricante de dispositivos móviles y computadoras", "https://www.apple.com", "contact@apple.com", "+1-408-996-1010");
            createFabricanteIfNotExists("Sony", "Japón", "Fabricante de electrónicos y entretenimiento", "https://www.sony.com", "contact@sony.com", "+81-3-6748-2111");
            createFabricanteIfNotExists("LG", "Corea del Sur", "Fabricante de electrodomésticos y electrónicos", "https://www.lg.com", "contact@lg.com", "+82-2-3777-1114");
            createFabricanteIfNotExists("Genérico", "N/A", "Fabricante genérico para productos sin marca específica", "", "info@generico.com", "");
            
            System.out.println("✅ Fabricantes de ejemplo creados exitosamente");
        } else {
            System.out.println("🔍 Ya existen " + fabricantesExistentes.size() + " fabricantes en la base de datos");
        }
    }
    
    private void createFabricanteIfNotExists(String nombre, String pais, String descripcion, String sitioWeb, String email, String telefono) {
        Optional<Fabricante> existingFabricante = fabricanteService.findByNombre(nombre);
        if (existingFabricante.isEmpty()) {
            Fabricante fabricante = new Fabricante();
            fabricante.setNombre(nombre);
            fabricante.setPais(pais);
            fabricante.setDescripcion(descripcion);
            fabricante.setSitioWeb(sitioWeb);
            fabricante.setEmail(email);
            fabricante.setTelefono(telefono);
            fabricante.setEstatus("ACTIVO");
            
            fabricanteService.save(fabricante);
        }
    }
    
    private void initializeDefaultCategorias() {
        // Verificar si ya existen categorías
        List<Categoria> categoriasExistentes = categoriaService.findAll();
        if (categoriasExistentes.isEmpty()) {
            // Crear categorías de ejemplo
            createCategoriaIfNotExists("Electrónicos", "Dispositivos electrónicos y tecnología");
            createCategoriaIfNotExists("Computadoras", "Computadoras, laptops y accesorios");
            createCategoriaIfNotExists("Móviles", "Teléfonos móviles y accesorios");
            createCategoriaIfNotExists("Audio", "Equipos de audio y sonido");
            createCategoriaIfNotExists("Video", "Equipos de video y entretenimiento");
            createCategoriaIfNotExists("Hogar", "Electrodomésticos para el hogar");
            createCategoriaIfNotExists("Oficina", "Suministros y equipos de oficina");
            createCategoriaIfNotExists("Gaming", "Consolas y videojuegos");
            
            System.out.println("✅ Categorías de ejemplo creadas exitosamente");
        } else {
            System.out.println("🔍 Ya existen " + categoriasExistentes.size() + " categorías en la base de datos");
        }
    }
    
    private void createCategoriaIfNotExists(String nombre, String descripcion) {
        Optional<Categoria> existingCategoria = categoriaService.findByNombre(nombre);
        if (existingCategoria.isEmpty()) {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoria.setDescripcion(descripcion);
            
            categoriaService.save(categoria);
        }
    }
    
    private void initializeDefaultClientes() {
        // Verificar si ya existen clientes
        List<Cliente> clientesExistentes = clienteService.findAll();
        if (clientesExistentes.isEmpty()) {
            // Crear clientes de ejemplo
            createClienteIfNotExists("Juan", "Pérez", "juan.perez@email.com", "555-0101", "123 Calle Principal");
            createClienteIfNotExists("María", "García", "maria.garcia@email.com", "555-0102", "456 Avenida Central");
            createClienteIfNotExists("Carlos", "López", "carlos.lopez@email.com", "555-0103", "789 Boulevard Norte");
            createClienteIfNotExists("Ana", "Martínez", "ana.martinez@email.com", "555-0104", "321 Calle Sur");
            createClienteIfNotExists("Roberto", "Silva", "roberto.silva@email.com", "555-0105", "654 Avenida Este");
            createClienteIfNotExists("Tech", "Solutions", "ventas@techsolutions.com", "555-0200", "1000 Corporate Plaza");
            createClienteIfNotExists("El Buen", "Precio", "compras@elbuenprecio.com", "555-0201", "2000 Centro Comercial");
            
            System.out.println("✅ Clientes de ejemplo creados exitosamente");
        } else {
            System.out.println("🔍 Ya existen " + clientesExistentes.size() + " clientes en la base de datos");
        }
    }
    
    private void createClienteIfNotExists(String nombre, String apellido, String email, String telefono, String direccion) {
        // Buscar por email
        List<Cliente> existingClientes = clienteService.findAll();
        boolean exists = existingClientes.stream().anyMatch(c -> c.getEmail().equals(email));
        
        if (!exists) {
            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setEmail(email);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            
            clienteService.save(cliente);
        }
    }
    
    private void initializeDefaultProductos() {
        // Verificar si ya existen productos
        List<Producto> productosExistentes = productoService.findAll();
        if (productosExistentes.isEmpty()) {
            // Obtener categorías y fabricantes para asignar a productos
            Optional<Categoria> electronicos = categoriaService.findByNombre("Electrónicos");
            Optional<Categoria> computadoras = categoriaService.findByNombre("Computadoras");
            Optional<Categoria> moviles = categoriaService.findByNombre("Móviles");
            Optional<Categoria> audio = categoriaService.findByNombre("Audio");
            Optional<Categoria> gaming = categoriaService.findByNombre("Gaming");
            
            Optional<Fabricante> samsung = fabricanteService.findByNombre("Samsung");
            Optional<Fabricante> apple = fabricanteService.findByNombre("Apple");
            Optional<Fabricante> sony = fabricanteService.findByNombre("Sony");
            Optional<Fabricante> lg = fabricanteService.findByNombre("LG");
            Optional<Fabricante> generico = fabricanteService.findByNombre("Genérico");
            
            // Crear productos de ejemplo
            if (samsung.isPresent() && moviles.isPresent()) {
                createProductoIfNotExists("Galaxy S24", "Smartphone Samsung Galaxy S24 128GB", "SAMGS24128", 
                    new BigDecimal("1199.99"), 25, samsung.get(), moviles.get());
                createProductoIfNotExists("Galaxy Tab A8", "Tablet Samsung Galaxy Tab A8 64GB", "SAMTABA864", 
                    new BigDecimal("299.99"), 15, samsung.get(), electronicos.get());
            }
            
            if (apple.isPresent() && computadoras.isPresent()) {
                createProductoIfNotExists("MacBook Air M2", "MacBook Air con chip M2 256GB", "APPMACAIRM2256", 
                    new BigDecimal("1399.99"), 10, apple.get(), computadoras.get());
                createProductoIfNotExists("iPhone 15", "iPhone 15 128GB", "APPIPH15128", 
                    new BigDecimal("999.99"), 20, apple.get(), moviles.get());
            }
            
            if (sony.isPresent() && audio.isPresent()) {
                createProductoIfNotExists("WH-1000XM5", "Audífonos Sony WH-1000XM5 Noise Cancelling", "SONYWH1000XM5", 
                    new BigDecimal("399.99"), 18, sony.get(), audio.get());
                createProductoIfNotExists("PlayStation 5", "Consola Sony PlayStation 5", "SONYPS5", 
                    new BigDecimal("549.99"), 8, sony.get(), gaming.get());
            }
            
            if (lg.isPresent() && electronicos.isPresent()) {
                createProductoIfNotExists("Smart TV 55\"", "LG Smart TV OLED 55 pulgadas 4K", "LGOLED55", 
                    new BigDecimal("1199.99"), 12, lg.get(), electronicos.get());
            }
            
            if (generico.isPresent() && electronicos.isPresent()) {
                createProductoIfNotExists("Mouse Inalámbrico", "Mouse inalámbrico genérico", "MOUSEWIRELESS", 
                    new BigDecimal("19.99"), 50, generico.get(), electronicos.get());
                createProductoIfNotExists("Teclado USB", "Teclado USB genérico", "KEYBOARDUSB", 
                    new BigDecimal("29.99"), 40, generico.get(), electronicos.get());
                createProductoIfNotExists("Cable HDMI", "Cable HDMI 2.0 - 2 metros", "CABLEHDMI2M", 
                    new BigDecimal("15.99"), 100, generico.get(), electronicos.get());
            }
            
            System.out.println("✅ Productos de ejemplo creados exitosamente");
        } else {
            System.out.println("🔍 Ya existen " + productosExistentes.size() + " productos en la base de datos");
        }
    }
    
    private void createProductoIfNotExists(String nombre, String descripcion, String codigo, 
            BigDecimal precio, Integer stock, Fabricante fabricante, Categoria categoria) {
        Optional<Producto> existingProducto = productoService.findByCodigo(codigo);
        if (existingProducto.isEmpty()) {
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setCodigo(codigo);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setFabricante(fabricante);
            producto.setCategoria(categoria);
            producto.setEstatus("ACTIVO");
            
            productoService.save(producto);
        }
    }
    
    private void initializeDefaultComportamientos() {
        // Comportamientos de Inventario
        createComportamientoIfNotExists("ENTRADA", "Entrada de Inventario", 
            "Aumenta el stock de productos en el inventario", "INVENTARIO", true, 1);
            
        createComportamientoIfNotExists("SALIDA", "Salida de Inventario", 
            "Disminuye el stock de productos en el inventario", "INVENTARIO", true, -1);
            
        createComportamientoIfNotExists("AJUSTE_POSITIVO", "Ajuste Positivo", 
            "Ajuste positivo de inventario por diferencias", "INVENTARIO", true, 1);
            
        createComportamientoIfNotExists("AJUSTE_NEGATIVO", "Ajuste Negativo", 
            "Ajuste negativo de inventario por diferencias", "INVENTARIO", true, -1);
            
        // Comportamientos de Ventas
        createComportamientoIfNotExists("FACTURA", "Factura de Venta", 
            "Documento de venta que reduce el inventario", "VENTAS", true, -1);
            
        createComportamientoIfNotExists("DEVOLUCION", "Devolución de Venta", 
            "Devolución que aumenta el inventario", "VENTAS", true, 1);
            
        createComportamientoIfNotExists("PEDIDO", "Pedido de Cliente", 
            "Pedido pendiente que no afecta inventario", "VENTAS", false, 0);
            
        createComportamientoIfNotExists("COTIZACION", "Cotización", 
            "Cotización que no afecta inventario", "VENTAS", false, 0);
            
        // Comportamientos Generales
        createComportamientoIfNotExists("NEUTRO", "Neutro", 
            "Comportamiento neutro que no afecta inventario", "GENERAL", false, 0);
            
        createComportamientoIfNotExists("TRANSFERENCIA", "Transferencia", 
            "Transferencia entre ubicaciones", "INVENTARIO", false, 0);
            
        System.out.println("✅ Comportamientos de ejemplo creados exitosamente");
    }
    
    private void createComportamientoIfNotExists(String codigo, String nombre, String descripcion, 
            String tipo, Boolean afectaStock, Integer signoMovimiento) {
        Optional<Comportamiento> existingComportamiento = comportamientoService.findByCodigo(codigo);
        if (existingComportamiento.isEmpty()) {
            Comportamiento comportamiento = new Comportamiento();
            comportamiento.setCodigo(codigo);
            comportamiento.setNombre(nombre);
            comportamiento.setDescripcion(descripcion);
            comportamiento.setTipo(tipo);
            comportamiento.setAfectaStock(afectaStock);
            comportamiento.setSignoMovimiento(signoMovimiento);
            comportamiento.setEstatus("ACTIVO");
            
            comportamientoService.save(comportamiento);
        }
    }
}
