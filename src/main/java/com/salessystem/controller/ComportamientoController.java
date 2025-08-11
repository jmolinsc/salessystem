package com.salessystem.controller;

import com.salessystem.model.Comportamiento;
import com.salessystem.service.ComportamientoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/comportamientos")
public class ComportamientoController {
    
    private final ComportamientoService comportamientoService;
    
    public ComportamientoController(ComportamientoService comportamientoService) {
        this.comportamientoService = comportamientoService;
    }
    
    @GetMapping
    public String listarComportamientos(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("comportamientos", comportamientoService.search(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("comportamientos", comportamientoService.findActivos());
        }
        return "comportamientos/list";
    }
    
    @GetMapping("/list")
    public String listarComportamientosAlternative(@RequestParam(required = false) String search, Model model) {
        return listarComportamientos(search, model);
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("comportamiento", new Comportamiento());
        return "comportamientos/form";
    }
    
    @PostMapping("/guardar")
    public String guardarComportamiento(@Valid @ModelAttribute Comportamiento comportamiento,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        
        System.out.println("=== DEBUG GUARDAR COMPORTAMIENTO ===");
        System.out.println("Código: " + comportamiento.getCodigo());
        System.out.println("Nombre: " + comportamiento.getNombre());
        System.out.println("Tipo: " + comportamiento.getTipo());
        System.out.println("Afecta Stock: " + comportamiento.getAfectaStock());
        System.out.println("Signo Movimiento: " + comportamiento.getSignoMovimiento());
        
        // Validaciones personalizadas
        if (comportamiento.getCodigo() != null && !comportamiento.getCodigo().trim().isEmpty()) {
            if (comportamientoService.existsByCodigo(comportamiento.getCodigo())) {
                result.rejectValue("codigo", "duplicate", "Ya existe un comportamiento con este código");
            }
        }
        
        if (result.hasErrors()) {
            return "comportamientos/form";
        }
        
        try {
            Comportamiento comportamientoGuardado = comportamientoService.save(comportamiento);
            redirectAttributes.addFlashAttribute("success", 
                "Comportamiento '" + comportamientoGuardado.getNombre() + "' guardado exitosamente!");
            return "redirect:/comportamientos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al guardar el comportamiento: " + e.getMessage());
            return "redirect:/comportamientos";
        }
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Comportamiento comportamiento = comportamientoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comportamiento no encontrado: " + id));
        
        model.addAttribute("comportamiento", comportamiento);
        return "comportamientos/form";
    }
    
    @PostMapping("/actualizar")
    public String actualizarComportamiento(@Valid @ModelAttribute Comportamiento comportamiento,
                                         BindingResult result,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        
        System.out.println("=== DEBUG ACTUALIZAR COMPORTAMIENTO ===");
        System.out.println("ID: " + comportamiento.getId());
        System.out.println("Código: " + comportamiento.getCodigo());
        System.out.println("Nombre: " + comportamiento.getNombre());
        
        // Validaciones personalizadas
        if (comportamiento.getCodigo() != null && !comportamiento.getCodigo().trim().isEmpty()) {
            if (comportamientoService.existsByCodigoAndIdNot(comportamiento.getCodigo(), comportamiento.getId())) {
                result.rejectValue("codigo", "duplicate", "Ya existe un comportamiento con este código");
            }
        }
        
        if (result.hasErrors()) {
            return "comportamientos/form";
        }
        
        try {
            Comportamiento comportamientoActualizado = comportamientoService.save(comportamiento);
            redirectAttributes.addFlashAttribute("success", 
                "Comportamiento '" + comportamientoActualizado.getNombre() + "' actualizado exitosamente!");
            return "redirect:/comportamientos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar el comportamiento: " + e.getMessage());
            return "redirect:/comportamientos";
        }
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarComportamiento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Verificar si el comportamiento está siendo usado
            Long tiposDocumentoCount = comportamientoService.countTiposDocumentoByComportamiento(id);
            if (tiposDocumentoCount > 0) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar el comportamiento porque está siendo usado por " + tiposDocumentoCount + " tipo(s) de documento");
                return "redirect:/comportamientos";
            }
            
            Comportamiento comportamiento = comportamientoService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Comportamiento no encontrado: " + id));
            
            comportamientoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", 
                "Comportamiento '" + comportamiento.getNombre() + "' eliminado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar el comportamiento: " + e.getMessage());
        }
        return "redirect:/comportamientos";
    }
    
    @PostMapping("/desactivar/{id}")
    public String desactivarComportamiento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Comportamiento comportamiento = comportamientoService.desactivar(id);
            redirectAttributes.addFlashAttribute("success", 
                "Comportamiento '" + comportamiento.getNombre() + "' desactivado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al desactivar el comportamiento: " + e.getMessage());
        }
        return "redirect:/comportamientos";
    }
    
    @PostMapping("/activar/{id}")
    public String activarComportamiento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Comportamiento comportamiento = comportamientoService.activar(id);
            redirectAttributes.addFlashAttribute("success", 
                "Comportamiento '" + comportamiento.getNombre() + "' activado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al activar el comportamiento: " + e.getMessage());
        }
        return "redirect:/comportamientos";
    }
    
    @GetMapping("/validar-codigo")
    @ResponseBody
    public Map<String, Boolean> validarCodigo(@RequestParam String codigo, @RequestParam(required = false) Long id) {
        boolean existe = false;
        
        if (id != null) {
            // Para edición: verificar si existe el código en otro comportamiento diferente al actual
            existe = comportamientoService.existsByCodigoAndIdNot(codigo, id);
        } else {
            // Para nuevo comportamiento: verificar si existe el código
            existe = comportamientoService.existsByCodigo(codigo);
        }
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("existe", existe);
        return response;
    }
    
    // API endpoints para obtener comportamientos por tipo
    @GetMapping("/api/por-tipo/{tipo}")
    @ResponseBody
    public java.util.List<Comportamiento> getComportamientosPorTipo(@PathVariable String tipo) {
        return comportamientoService.findByTipo(tipo);
    }
    
    @GetMapping("/api/que-afectan-stock")
    @ResponseBody
    public java.util.List<Comportamiento> getComportamientosQueAfectanStock() {
        return comportamientoService.findQueAfectanStock();
    }
}
