package com.salessystem.controller;

import com.salessystem.model.MovimientoInventario;
import com.salessystem.model.Producto;
import com.salessystem.model.TipoMovimiento;
import com.salessystem.model.Usuario;
import com.salessystem.service.InventarioService;
import com.salessystem.service.ProductoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/inventario")
public class InventarioController {
    
    private final InventarioService inventarioService;
    private final ProductoService productoService;
    
    public InventarioController(InventarioService inventarioService, ProductoService productoService) {
        this.inventarioService = inventarioService;
        this.productoService = productoService;
    }
    
    @GetMapping
    public String dashboard(Model model) {
        // Estadísticas generales
        List<Producto> productosStockBajo = inventarioService.obtenerProductosStockBajo();
        List<Producto> productosSinStock = inventarioService.obtenerProductosSinStock();
        List<MovimientoInventario> ultimosMovimientos = inventarioService.obtenerUltimosMovimientos();
        
        model.addAttribute("productosStockBajo", productosStockBajo);
        model.addAttribute("productosSinStock", productosSinStock);
        model.addAttribute("ultimosMovimientos", ultimosMovimientos);
        model.addAttribute("totalProductos", productoService.findAll().size());
        
        return "inventario/dashboard";
    }
    
    @GetMapping("/list")
    public String listarInventarioAlternative() {
        return "redirect:/inventario";
    }
    
    @GetMapping("/movimientos")
    public String listarMovimientos(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) TipoMovimiento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {
        
        System.out.println("🔍 DEBUG MOVIMIENTOS: Parámetros - ProductoId: " + productoId + ", Tipo: " + tipo + ", FechaInicio: " + fechaInicio + ", FechaFin: " + fechaFin);
        
        List<MovimientoInventario> movimientos;
        
        try {
            if (productoId != null || tipo != null || fechaInicio != null || fechaFin != null) {
                LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : LocalDateTime.now().minusMonths(1);
                LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : LocalDateTime.now();
                
                movimientos = inventarioService.obtenerMovimientosConFiltros(productoId, tipo, inicio, fin);
                System.out.println("🔍 DEBUG: Movimientos con filtros obtenidos: " + movimientos.size());
            } else {
                movimientos = inventarioService.obtenerUltimosMovimientos();
                System.out.println("🔍 DEBUG: Últimos movimientos obtenidos: " + movimientos.size());
            }
            
            // Debug de los primeros movimientos
            for (int i = 0; i < Math.min(3, movimientos.size()); i++) {
                MovimientoInventario mov = movimientos.get(i);
                System.out.println("🔍 MOVIMIENTO " + i + ": " + mov.getId() + " - " + 
                    (mov.getProducto() != null ? mov.getProducto().getNombre() : "null") + 
                    " - " + mov.getTipo() + " - " + mov.getCantidad());
            }
            
        } catch (Exception e) {
            System.out.println("❌ ERROR obteniendo movimientos: " + e.getMessage());
            e.printStackTrace();
            movimientos = new ArrayList<>();
        }
        
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("tiposMovimiento", TipoMovimiento.values());
        model.addAttribute("filtroProducto", productoId);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroFechaInicio", fechaInicio);
        model.addAttribute("filtroFechaFin", fechaFin);
        
        return "inventario/movimientos";
    }
    
    @GetMapping("/entrada")
    public String mostrarFormularioEntrada(Model model) {
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("tipoOperacion", "entrada");
        return "inventario/form";
    }
    
    @PostMapping("/entrada")
    public String procesarEntrada(
            @RequestParam Long productoId,
            @RequestParam int cantidad,
            @RequestParam(required = false) BigDecimal costo,
            @RequestParam String motivo,
            @RequestParam(required = false) String observaciones,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("=== DEBUG ENTRADA INVENTARIO ===");
        System.out.println("ProductoId: " + productoId);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Costo: " + costo);
        System.out.println("Motivo: " + motivo);
        System.out.println("Observaciones: " + observaciones);
        
        try {
            Producto producto = productoService.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            Usuario usuario = obtenerUsuarioActual();
            
            MovimientoInventario movimiento = inventarioService.entradaInventario(
                producto, cantidad, costo, motivo, observaciones, usuario
            );
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Entrada registrada exitosamente. Nuevo stock: " + movimiento.getStockNuevo());
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar entrada: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        
        return "redirect:/inventario";
    }
    
    @GetMapping("/salida")
    public String mostrarFormularioSalida(Model model) {
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("tipoOperacion", "salida");
        return "inventario/form";
    }
    
    @PostMapping("/salida")
    public String procesarSalida(
            @RequestParam Long productoId,
            @RequestParam int cantidad,
            @RequestParam String motivo,
            @RequestParam(required = false) String observaciones,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("=== DEBUG SALIDA INVENTARIO ===");
        System.out.println("ProductoId: " + productoId);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Motivo: " + motivo);
        System.out.println("Observaciones: " + observaciones);
        
        try {
            Producto producto = productoService.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            Usuario usuario = obtenerUsuarioActual();
            
            MovimientoInventario movimiento = inventarioService.salidaInventario(
                producto, cantidad, motivo, observaciones, usuario
            );
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Salida registrada exitosamente. Nuevo stock: " + movimiento.getStockNuevo());
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar salida: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        
        return "redirect:/inventario";
    }
    
    @GetMapping("/ajuste")
    public String mostrarFormularioAjuste(Model model) {
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("tipoOperacion", "ajuste");
        return "inventario/form";
    }
    
    @PostMapping("/ajuste")
    public String procesarAjuste(
            @RequestParam Long productoId,
            @RequestParam int stockNuevo,
            @RequestParam String motivo,
            @RequestParam(required = false) String observaciones,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("=== DEBUG AJUSTE INVENTARIO ===");
        System.out.println("ProductoId: " + productoId);
        System.out.println("StockNuevo: " + stockNuevo);
        System.out.println("Motivo: " + motivo);
        System.out.println("Observaciones: " + observaciones);
        
        try {
            Producto producto = productoService.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            Usuario usuario = obtenerUsuarioActual();
            
            MovimientoInventario movimiento = inventarioService.ajusteInventario(
                producto, stockNuevo, motivo, observaciones, usuario
            );
            
            if (movimiento != null) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Ajuste registrado exitosamente. Nuevo stock: " + movimiento.getStockNuevo());
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "No se requiere ajuste, el stock es el mismo.");
                redirectAttributes.addFlashAttribute("tipoMensaje", "info");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar ajuste: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        
        return "redirect:/inventario";
    }
    
    @GetMapping("/producto/{id}/historial")
    public String verHistorialProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        
        List<MovimientoInventario> movimientos = inventarioService.obtenerMovimientosPorProducto(id);
        
        model.addAttribute("producto", producto);
        model.addAttribute("movimientos", movimientos);
        
        return "inventario/historial";
    }
    
    @GetMapping("/alertas")
    public String verAlertas(Model model) {
        List<Producto> productosStockBajo = inventarioService.obtenerProductosStockBajo();
        List<Producto> productosSinStock = inventarioService.obtenerProductosSinStock();
        
        model.addAttribute("productosStockBajo", productosStockBajo);
        model.addAttribute("productosSinStock", productosSinStock);
        
        return "inventario/alertas";
    }
    
    @GetMapping("/api/stock/{productoId}")
    @ResponseBody
    public int obtenerStock(@PathVariable Long productoId) {
        return productoService.findById(productoId)
            .map(Producto::getStock)
            .orElse(0);
    }
    
    // Método auxiliar para obtener el usuario actual
    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Aquí necesitarías un servicio de usuario para obtener el objeto Usuario completo
        // Por ahora, creamos un usuario temporal
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setId(1L); // Temporal - en producción debería obtenerse de la base de datos
        
        return usuario;
    }
}
