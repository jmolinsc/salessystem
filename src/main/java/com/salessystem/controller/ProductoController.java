package com.salessystem.controller;

import com.salessystem.model.Producto;
import com.salessystem.service.ProductoService;
import com.salessystem.service.CategoriaService;
import com.salessystem.service.FabricanteService;
import com.salessystem.service.ProdFamiliaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final FabricanteService fabricanteService;
    private final ProdFamiliaService prodFamiliaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService, 
                            FabricanteService fabricanteService, ProdFamiliaService prodFamiliaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.fabricanteService = fabricanteService;
        this.prodFamiliaService = prodFamiliaService;
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProducto(Model model, @ModelAttribute("mensaje") String toastMessage, @ModelAttribute("tipoMensaje") String toastType) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.findAllOrderByNombre());
        model.addAttribute("fabricantes", fabricanteService.findAllActivos());
        model.addAttribute("prodFamilias", prodFamiliaService.findAllActivas());
        if (toastMessage != null && !toastMessage.isEmpty()) {
            model.addAttribute("toastMessage", toastMessage);
            model.addAttribute("toastType", toastType);
        }
        return "productos/form";
    }

    @GetMapping
    public String listarProductos(Model model, @ModelAttribute("mensaje") String toastMessage, @ModelAttribute("tipoMensaje") String toastType) {
        model.addAttribute("productos", productoService.findAll());
        if (toastMessage != null && !toastMessage.isEmpty()) {
            model.addAttribute("toastMessage", toastMessage);
            model.addAttribute("toastType", toastType);
        }
        return "productos/list";
    }

    @GetMapping("/list")
    public String listarProductosAlternative() {
        return "redirect:/productos";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto,
                                  @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                                  @RequestParam(value = "fabricanteId", required = false) Long fabricanteId,
                                  @RequestParam(value = "prodFamiliaId", required = false) Long prodFamiliaId,
                                  @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                                  RedirectAttributes redirectAttributes, Model model) {
        try {
            // Validar código único
            boolean codigoExiste = false;
            if (producto.getId() == null) {
                // Nuevo producto
                codigoExiste = productoService.existsByCodigo(producto.getCodigo());
                
                // Establecer fecha de alta para productos nuevos
                if (producto.getFechaAlta() == null) {
                    producto.setFechaAlta(LocalDateTime.now());
                }
                
                // Establecer estatus por defecto si no se especifica
                if (producto.getEstatus() == null || producto.getEstatus().isEmpty()) {
                    producto.setEstatus("ACTIVO");
                }
            } else {
                // Producto existente
                codigoExiste = productoService.existsByCodigoAndIdNot(producto.getCodigo(), producto.getId());
            }
            
            if (codigoExiste) {
                model.addAttribute("producto", producto);
                model.addAttribute("categorias", categoriaService.findAllOrderByNombre());
                model.addAttribute("fabricantes", fabricanteService.findAllActivos());
                model.addAttribute("error", "El código '" + producto.getCodigo() + "' ya existe. Por favor, ingrese un código diferente.");
                return "productos/form";
            }
            
            // Asignar categoría manualmente
            if (categoriaId != null) {
                categoriaService.findById(categoriaId).ifPresent(producto::setCategoria);
            }
            
            // Asignar fabricante manualmente
            if (fabricanteId != null) {
                fabricanteService.findById(fabricanteId).ifPresent(producto::setFabricante);
            }
            // Asignar familia manualmente
            if (prodFamiliaId != null) {
                prodFamiliaService.findById(prodFamiliaId).ifPresent(producto::setProdFamilia);
            }
            // Procesar imagen
            if (imagen != null && !imagen.isEmpty()) {
                String originalName = imagen.getOriginalFilename();
                String extension = originalName != null && originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.') + 1) : "";
                String nombreArchivo = UUID.randomUUID() + "." + extension;
                // Usar ruta absoluta del proyecto para guardar la imagen
                String basePath = System.getProperty("user.dir") + "/uploads/productos";
                Path rutaCarpeta = Paths.get(basePath);
                if (!Files.exists(rutaCarpeta)) {
                    Files.createDirectories(rutaCarpeta);
                }
                Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);
                imagen.transferTo(rutaArchivo.toFile());
                // Eliminar imagen anterior si existe y es diferente
                if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
                    Path rutaAnterior = Paths.get(System.getProperty("user.dir") + producto.getImagenUrl());
                    if (Files.exists(rutaAnterior)) {
                        Files.delete(rutaAnterior);
                    }
                }
                producto.setImagenUrl("/uploads/productos/" + nombreArchivo);
            }

          Producto productoguardado=  productoService.save(producto);
            // Preparar datos para la vista
            model.addAttribute("producto", productoguardado);
            model.addAttribute("categorias", categoriaService.findAllOrderByNombre());
            model.addAttribute("fabricantes", fabricanteService.findAllActivos());
            model.addAttribute("prodFamilias", prodFamiliaService.findAllActivas());
            redirectAttributes.addFlashAttribute("mensaje", "Producto guardado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "productos/form";

    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.findAllOrderByNombre());
        model.addAttribute("fabricantes", fabricanteService.findAllActivos());
        model.addAttribute("prodFamilias", prodFamiliaService.findAllActivas());
        return "productos/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/productos";
    }

    @GetMapping("/buscar-productos")
    public String buscarProductos(@RequestParam(required = false) String term, Model model) {
        List<Producto> productos;
        if (term == null || term.isEmpty()) {
            productos = productoService.findAll();
        } else {
            productos = productoService.search(term);
        }
        model.addAttribute("productos", productos);
        return "ventas/modal :: productosModal";
    }
    
    @GetMapping("/validar-codigo")
    @ResponseBody
    public Map<String, Boolean> validarCodigo(@RequestParam String codigo, @RequestParam(required = false) Long id) {
        boolean existe;
        if (id == null) {
            existe = productoService.existsByCodigo(codigo);
        } else {
            existe = productoService.existsByCodigoAndIdNot(codigo, id);
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("existe", existe);
        return response;
    }

}
