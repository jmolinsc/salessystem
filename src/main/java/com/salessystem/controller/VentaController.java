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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.springframework.beans.propertyeditors.CustomDateEditor;


@Controller
@RequestMapping("/ventas")
public class VentaController {

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
    public String mostrarFormularioNuevaVenta(Model model) {
        Venta venta = new Venta();
        // Convertir la fecha actual a LocalDate para compatibilidad con HTML5 date input
        LocalDate fechaActual = LocalDate.now();
        Date fecha = Date.from(fechaActual.atStartOfDay(ZoneId.systemDefault()).toInstant());
        venta.setFecha(fecha);
        venta.setTotal(BigDecimal.ZERO);
        // Asignar SIN_AFECTAR por defecto para nuevas ventas
        venta.setEstatus(EstatusVenta.SIN_AFECTAR);

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));
        // Agregar la fecha como LocalDate para el formulario
        model.addAttribute("fechaActual", fechaActual);

        return "ventas/form";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoVenta(Model model) {
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
        
        System.out.println("Fecha: " + venta.getFecha());
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
            
            // Preparar datos para la vista
            model.addAttribute("venta", ventaGuardada);
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));
            model.addAttribute("esEdicion", true); // Marcar como edición después de guardar
            
            // Convertir fecha para el formulario
            LocalDate fechaVenta = ventaGuardada.getFecha().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            model.addAttribute("fechaActual", fechaVenta);
            
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
            // En caso de error, preparar datos para la vista
            model.addAttribute("venta", venta);
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));
            
            LocalDate fechaActual = LocalDate.now();
            model.addAttribute("fechaActual", fechaActual);
            model.addAttribute("formularioDeshabilitado", false);
            
            // Mensaje de error
            model.addAttribute("toastMessage", "Error al guardar la venta: " + e.getMessage());
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

        // Convertir fecha para compatibilidad con HTML5
        LocalDate fechaVenta = venta.getFecha().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));
        model.addAttribute("fechaActual", fechaVenta);
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
            model.addAttribute("venta", ventaActualizada);
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("tiposDocumento", tipoDocumentoService.findByModulo("VTA"));
            model.addAttribute("esEdicion", true);
            
            // Convertir fecha para el formulario
            LocalDate fechaVenta = ventaActualizada.getFecha().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            model.addAttribute("fechaActual", fechaVenta);
            
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
            
            LocalDate fechaVenta = ventaOriginal.getFecha().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            model.addAttribute("fechaActual", fechaVenta);
            
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

}