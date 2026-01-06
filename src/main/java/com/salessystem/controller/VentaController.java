package com.salessystem.controller;

import com.salessystem.model.*;
import com.salessystem.repository.*;
import com.salessystem.service.VentaService;
import com.salessystem.service.TipoDocumentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.WebDataBinder;

import jakarta.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/ventas")
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final TipoDocumentoService tipoDocumentoService;

    public VentaController(VentaService ventaService,
            ProductoRepository productoRepository,
            ClienteRepository clienteRepository,
            TipoDocumentoService tipoDocumentoService) {
        this.ventaService = ventaService;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.tipoDocumentoService = tipoDocumentoService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CondicionPago.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(CondicionPago.fromDescripcion(text));
            }
        });
        
        // Configurar formato de fecha para compatibility HTML5
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping
    public String listarVentas(Model model) {
        model.addAttribute("ventas", ventaService.obtenerTodasVentas());
        return "ventas/list";
    }

    @GetMapping("/list")
    public String listarVentasAlternative() {
        return "redirect:/ventas";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaVenta(Model model) throws ParseException {
        Venta venta = new Venta();
        venta.setFechaEmision(new Date());
        venta.setTotal(BigDecimal.ZERO);
        venta.setEstatus(EstatusVenta.SIN_AFECTAR);

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));

        return "ventas/form";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoVenta(Model model) throws ParseException {
        return mostrarFormularioNuevaVenta(model);
    }

    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute("venta") Venta venta,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        String accion = request.getParameter("accion");
        String comportamiento = request.getParameter("comportamiento");
        
        System.out.println("=== DEBUG VENTA ===");
        System.out.println("Acción recibida: '" + accion + "'");
        System.out.println("Comportamiento recibido: '" + comportamiento + "'");
        System.out.println("=== TODOS LOS PARÁMETROS ===");
        request.getParameterMap().forEach((key, values) -> {
            System.out.println(key + " = " + String.join(", ", values));
        });
        System.out.println("=== FIN PARÁMETROS ===");
        
        // Validar y asignar valores por defecto
        if (accion == null || accion.trim().isEmpty()) {
            accion = "guardar";
            System.out.println("Acción era null/vacía, usando valor por defecto: " + accion);
        }
        
        if (comportamiento == null || comportamiento.trim().isEmpty()) {
            comportamiento = "NEUTRO";
            System.out.println("Comportamiento era null/vacío, usando valor por defecto: " + comportamiento);
        }
        
        System.out.println("Acción final: " + accion);
        System.out.println("Comportamiento final: " + comportamiento);
        System.out.println("Parámetros del request:");
        request.getParameterMap().forEach((key, values) -> {
            System.out.println(key + " = " + String.join(", ", values));
        });
        
        System.out.println("Fecha: " + venta.getUltimocambio());
        System.out.println("Cliente ID: " + (venta.getCliente() != null ? venta.getCliente().getId() : "null"));
        System.out.println("Total: " + venta.getTotal());
        System.out.println("Estatus: " + venta.getEstatus());
        System.out.println("Detalles count: " + (venta.getDetalles() != null ? venta.getDetalles().size() : "null"));
        
        if (venta.getDetalles() != null) {
            for (int i = 0; i < venta.getDetalles().size(); i++) {
                var detalle = venta.getDetalles().get(i);
                System.out.println("Detalle " + i + ": ProductoId=" + 
                    (detalle.getProducto() != null ? detalle.getProducto().getId() : "null") +
                    ", Cantidad=" + detalle.getCantidad() + 
                    ", Precio=" + detalle.getPrecioUnitario() + 
                    ", Subtotal=" + detalle.getSubtotal());
            }
        }
        
        try {
            // Procesar según el comportamiento del tipo de documento
            ventaService.procesarVentaSegunComportamiento(venta, comportamiento, accion);
            
            // Establecer la relación bidireccional entre venta y detalles
            if (venta.getDetalles() != null) {
                venta.getDetalles().forEach(detalle -> {
                    detalle.setVenta(venta);
                });
            }
            
            Venta ventaGuardada = ventaService.guardarVenta(venta);
            
            // Recargar la venta desde la base de datos para obtener todos los detalles
            ventaGuardada = ventaService.obtenerVentaPorId(ventaGuardada.getId())
                    .orElse(ventaGuardada);

            // Forzar inicialización de relaciones para evitar lazy exceptions durante el render
            if (ventaGuardada.getDetalles() != null) {
                for (com.salessystem.model.DetalleVenta d : ventaGuardada.getDetalles()) {
                    if (d.getProducto() != null) {
                        // accesar campos simples para inicializar proxies
                        d.getProducto().getId();
                        d.getProducto().getNombre();
                    }
                    // inicializar subtotal/cantidad
                    d.getCantidad();
                    d.getSubtotal();
                }
            }
            if (ventaGuardada.getCliente() != null) {
                ventaGuardada.getCliente().getId();
                ventaGuardada.getCliente().getNombre();
            }

            // Preparar datos para la vista
            model.addAttribute("venta", toSafeVenta(ventaGuardada));
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));
            model.addAttribute("esEdicion", true); // Marcar como edición después de guardar
            
            // Convertir fecha para el formulario
            if (ventaGuardada.getUltimocambio() == null) {
                ventaGuardada.setUltimocambio(new Date());
            }
            LocalDate fechaVenta = ventaGuardada.getUltimocambio().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            model.addAttribute("fechaActual", fechaVenta);
            // Formatear fecha para mostrar "día/mes/año"
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            model.addAttribute("fechaActualStr", fechaVenta.format(fmt));

            // Determinar si el formulario debe estar deshabilitado
            boolean formularioDeshabilitado = ventaGuardada.getEstatus() == EstatusVenta.CONCLUIDO || 
                                            ventaGuardada.getEstatus() == EstatusVenta.PENDIENTE;
            model.addAttribute("formularioDeshabilitado", formularioDeshabilitado);
            
            System.out.println("=== DEBUG ESTATUS ===");
            System.out.println("Estatus actual: " + ventaGuardada.getEstatus());
            System.out.println("Es CONCLUIDO: " + (ventaGuardada.getEstatus() == EstatusVenta.CONCLUIDO));
            System.out.println("Es PENDIENTE: " + (ventaGuardada.getEstatus() == EstatusVenta.PENDIENTE));
            System.out.println("Formulario deshabilitado: " + formularioDeshabilitado);
            
            // Mensaje de éxito
            String mensaje = "afectar".equals(accion) ? "Venta afectada exitosamente!" : "Venta guardada exitosamente!";
            model.addAttribute("toastMessage", mensaje);
            model.addAttribute("toastType", "success");

            System.out.println("=== DEBUG TOAST ===");
            System.out.println("Toast Message: " + mensaje);
            System.out.println("Toast Type: success");
            System.out.println("Formulario Deshabilitado: " + formularioDeshabilitado);
            System.out.println("Es Edición: true");
            System.out.println("Detalles count: " + (ventaGuardada.getDetalles() != null ? ventaGuardada.getDetalles().size() : "null"));
            
            return "ventas/form";
            
        } catch (Exception e) {
            // Log completo del error para diagnóstico
            logger.error("Error guardando/actualizando venta", e);

            // En caso de error, preparar datos para la vista
            model.addAttribute("venta", venta);
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));

            LocalDate fechaActual = LocalDate.now();
            model.addAttribute("fechaActual", fechaActual);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            model.addAttribute("fechaActualStr", fechaActual.format(fmt));
            model.addAttribute("formularioDeshabilitado", false);

            // Mensaje de error: mostrar genérico y registrar detalle en logs
            model.addAttribute("toastMessage", "Error al guardar la venta. Revise los logs para más detalles.");
            model.addAttribute("toastType", "error");

            return "ventas/form";
        }
    }

    @GetMapping("/{id}")
    public String verDetalleVenta(@PathVariable Long id, Model model) {
        Venta venta = ventaService.obtenerVentaPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada: " + id));

        model.addAttribute("venta", venta);
        return "ventas/detail";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarVenta(@PathVariable Long id, Model model) {
        Venta venta = ventaService.obtenerVentaPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada: " + id));

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));
        model.addAttribute("esEdicion", true);
        
        // Determinar si el formulario debe estar deshabilitado al editar
        boolean formularioDeshabilitado = venta.getEstatus() == EstatusVenta.CONCLUIDO || 
                                        venta.getEstatus() == EstatusVenta.PENDIENTE;
        model.addAttribute("formularioDeshabilitado", formularioDeshabilitado);
        
        System.out.println("=== DEBUG EDITAR VENTA ===");
        System.out.println("Estatus: " + venta.getEstatus());
        System.out.println("Formulario deshabilitado: " + formularioDeshabilitado);

        return "ventas/form";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarVenta(@PathVariable Long id,
            @ModelAttribute("venta") Venta venta,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        String accion = request.getParameter("accion");
        String comportamiento = request.getParameter("comportamiento");
        
        System.out.println("=== DEBUG ACTUALIZAR VENTA ===");
        System.out.println("ID a actualizar: " + id);
        System.out.println("Acción recibida: '" + accion + "'");
        System.out.println("Comportamiento recibido: '" + comportamiento + "'");
        
        // Validar y asignar valores por defecto
        if (accion == null || accion.trim().isEmpty()) {
            accion = "guardar";
            System.out.println("Acción era null/vacía, usando valor por defecto: " + accion);
        }
        
        if (comportamiento == null || comportamiento.trim().isEmpty()) {
            comportamiento = "NEUTRO";
            System.out.println("Comportamiento era null/vacío, usando valor por defecto: " + comportamiento);
        }
        
        System.out.println("Acción final: " + accion);
        System.out.println("Comportamiento final: " + comportamiento);
        
        try {
            // Establecer el ID para la actualización
            venta.setId(id);
            
            // Procesar según el comportamiento del tipo de documento
            ventaService.procesarVentaSegunComportamiento(venta, comportamiento, accion);
            
            // Establecer la relación bidireccional entre venta y detalles
            if (venta.getDetalles() != null) {
                venta.getDetalles().forEach(detalle -> {
                    detalle.setVenta(venta);
                });
            }
            
            Venta ventaActualizada = ventaService.guardarVenta(venta);
            
            // Recargar la venta desde la base de datos para obtener todos los detalles
            ventaActualizada = ventaService.obtenerVentaPorId(ventaActualizada.getId())
                    .orElse(ventaActualizada);
            
            // Preparar datos para la vista
            model.addAttribute("venta", toSafeVenta(ventaActualizada));
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));
            model.addAttribute("esEdicion", true);
            
            // Convertir fecha para el formulario
            if (ventaActualizada.getUltimocambio() == null) {
                ventaActualizada.setUltimocambio(new Date());
            }
            LocalDate fechaVenta = ventaActualizada.getUltimocambio().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            model.addAttribute("fechaActual", fechaVenta);
            // Formatear fecha para mostrar "día/mes/año"
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            model.addAttribute("fechaActualStr", fechaVenta.format(fmt));

            // Determinar si el formulario debe estar deshabilitado
            boolean formularioDeshabilitado = ventaActualizada.getEstatus() == EstatusVenta.CONCLUIDO || 
                                            ventaActualizada.getEstatus() == EstatusVenta.PENDIENTE;
            model.addAttribute("formularioDeshabilitado", formularioDeshabilitado);
            
            // Mensaje de éxito
            String mensaje = "afectar".equals(accion) ? "Venta afectada exitosamente!" : "Venta actualizada exitosamente!";
            model.addAttribute("toastMessage", mensaje);
            model.addAttribute("toastType", "success");
            
            return "ventas/form";
            
        } catch (Exception e) {
            // En caso de error, obtener la venta original
            Venta ventaOriginal = ventaService.obtenerVentaPorId(id).orElse(venta);
            
            model.addAttribute("venta", ventaOriginal);
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));
            model.addAttribute("esEdicion", true);
            
            LocalDate fechaVenta = ventaOriginal.getUltimocambio() == null ? LocalDate.now() :
                    ventaOriginal.getUltimocambio().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            model.addAttribute("fechaActual", fechaVenta);
            // Formatear fecha para mostrar "día/mes/año"
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            model.addAttribute("fechaActualStr", fechaVenta.format(fmt));

             boolean formularioDeshabilitado = ventaOriginal.getEstatus() == EstatusVenta.CONCLUIDO ||
                                             ventaOriginal.getEstatus() == EstatusVenta.PENDIENTE;
             model.addAttribute("formularioDeshabilitado", formularioDeshabilitado);

            // Mensaje de error
            model.addAttribute("toastMessage", "Error al actualizar la venta: " + e.getMessage());
            model.addAttribute("toastType", "error");
            
            return "ventas/form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Verificar que la venta existe antes de eliminar
            ventaService.obtenerVentaPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada: " + id));
            
            ventaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Venta eliminada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la venta: " + e.getMessage());
        }
        return "redirect:/ventas";
    }

    private com.salessystem.model.Venta toSafeVenta(com.salessystem.model.Venta venta) {
        if (venta == null) return null;
        com.salessystem.model.Venta safe = new com.salessystem.model.Venta();
        safe.setId(venta.getId());
        safe.setMovId(venta.getMovId());
        safe.setNumeroFactura(venta.getNumeroFactura());
        safe.setFechaEmision(venta.getFechaEmision());
        safe.setUltimocambio(venta.getUltimocambio());
        safe.setTotal(venta.getTotal());
        safe.setDescuento(venta.getDescuento());
        safe.setCondicion(venta.getCondicion());
        safe.setEstatus(venta.getEstatus());

        // Cliente simplificado
        if (venta.getCliente() != null) {
            com.salessystem.model.Cliente c = new com.salessystem.model.Cliente();
            c.setId(venta.getCliente().getId());
            c.setNombre(venta.getCliente().getNombre());
            c.setApellido(venta.getCliente().getApellido());
            c.setEmail(venta.getCliente().getEmail());
            safe.setCliente(c);
        }

        // Detalles simplificados
        if (venta.getDetalles() != null) {
            java.util.List<com.salessystem.model.DetalleVenta> detalles = new java.util.ArrayList<>();
            for (com.salessystem.model.DetalleVenta d : venta.getDetalles()) {
                com.salessystem.model.DetalleVenta sd = new com.salessystem.model.DetalleVenta();
                sd.setCantidad(d.getCantidad());
                sd.setPrecioUnitario(d.getPrecioUnitario());
                sd.setSubtotal(d.getSubtotal());
                if (d.getProducto() != null) {
                    com.salessystem.model.Producto p = new com.salessystem.model.Producto();
                    p.setId(d.getProducto().getId());
                    p.setNombre(d.getProducto().getNombre());
                    p.setPrecio(d.getProducto().getPrecio());
                    sd.setProducto(p);
                }
                detalles.add(sd);
            }
            safe.setDetalles(detalles);
        }

        return safe;
    }

}
