package com.salessystem.controller;

import com.salessystem.model.ProdFamilia;
import com.salessystem.service.ProdFamiliaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/prodfamilias")
public class ProdFamiliaController {
    
    private final ProdFamiliaService prodFamiliaService;
    
    public ProdFamiliaController(ProdFamiliaService prodFamiliaService) {
        this.prodFamiliaService = prodFamiliaService;
    }
    
    @GetMapping
    public String listarProdFamilias(Model model) {
        model.addAttribute("prodFamilias", prodFamiliaService.findAll());
        return "prodfamilias/list";
    }
    
    @GetMapping("/list")
    public String listarProdFamiliasAlternative() {
        return "redirect:/prodfamilias";
    }
    
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("prodFamilia", new ProdFamilia());
        model.addAttribute("esEdicion", false);
        return "prodfamilias/form";
    }
    
    @PostMapping("/guardar")
    public String guardarProdFamilia(@Valid @ModelAttribute ProdFamilia prodFamilia,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        
        System.out.println("=== DEBUG GUARDAR PROD FAMILIA ===");
        System.out.println("Nombre: " + prodFamilia.getNombre());
        System.out.println("Código: " + prodFamilia.getCodigo());
        System.out.println("Descripción: " + prodFamilia.getDescripcion());
        
        // Validaciones personalizadas
        if (prodFamilia.getCodigo() != null && !prodFamilia.getCodigo().trim().isEmpty()) {
            if (prodFamiliaService.existsByCodigo(prodFamilia.getCodigo())) {
                result.rejectValue("codigo", "duplicate", "Ya existe una familia con este código");
            }
        }
        
        if (result.hasErrors()) {
            model.addAttribute("esEdicion", false);
            return "prodfamilias/form";
        }
        
        try {
            ProdFamilia prodFamiliaGuardada = prodFamiliaService.save(prodFamilia);
            redirectAttributes.addFlashAttribute("success", 
                "Familia de producto '" + prodFamiliaGuardada.getNombre() + "' guardada exitosamente!");
            return "redirect:/prodfamilias";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al guardar la familia de producto: " + e.getMessage());
            return "redirect:/prodfamilias";
        }
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        ProdFamilia prodFamilia = prodFamiliaService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Familia de producto no encontrada: " + id));
        
        model.addAttribute("prodFamilia", prodFamilia);
        model.addAttribute("esEdicion", true);
        return "prodfamilias/form";
    }
    
    @PostMapping("/actualizar")
    public String actualizarProdFamilia(@Valid @ModelAttribute ProdFamilia prodFamilia,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        
        System.out.println("=== DEBUG ACTUALIZAR PROD FAMILIA ===");
        System.out.println("ID: " + prodFamilia.getId());
        System.out.println("Nombre: " + prodFamilia.getNombre());
        System.out.println("Código: " + prodFamilia.getCodigo());
        
        // Validaciones personalizadas
        if (prodFamilia.getCodigo() != null && !prodFamilia.getCodigo().trim().isEmpty()) {
            if (prodFamiliaService.existsByCodigoAndIdNot(prodFamilia.getCodigo(), prodFamilia.getId())) {
                result.rejectValue("codigo", "duplicate", "Ya existe una familia con este código");
            }
        }
        
        if (result.hasErrors()) {
            model.addAttribute("esEdicion", true);
            return "prodfamilias/form";
        }
        
        try {
            ProdFamilia prodFamiliaActualizada = prodFamiliaService.save(prodFamilia);
            redirectAttributes.addFlashAttribute("success", 
                "Familia de producto '" + prodFamiliaActualizada.getNombre() + "' actualizada exitosamente!");
            return "redirect:/prodfamilias";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar la familia de producto: " + e.getMessage());
            return "redirect:/prodfamilias";
        }
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarProdFamilia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ProdFamilia prodFamilia = prodFamiliaService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Familia de producto no encontrada: " + id));
            
            prodFamiliaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", 
                "Familia de producto '" + prodFamilia.getNombre() + "' eliminada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar la familia de producto: " + e.getMessage());
        }
        return "redirect:/prodfamilias";
    }
    
    @PostMapping("/desactivar/{id}")
    public String desactivarProdFamilia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ProdFamilia prodFamilia = prodFamiliaService.desactivar(id);
            redirectAttributes.addFlashAttribute("success", 
                "Familia de producto '" + prodFamilia.getNombre() + "' desactivada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al desactivar la familia de producto: " + e.getMessage());
        }
        return "redirect:/prodfamilias";
    }
    
    @PostMapping("/activar/{id}")
    public String activarProdFamilia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ProdFamilia prodFamilia = prodFamiliaService.activar(id);
            redirectAttributes.addFlashAttribute("success", 
                "Familia de producto '" + prodFamilia.getNombre() + "' activada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al activar la familia de producto: " + e.getMessage());
        }
        return "redirect:/prodfamilias";
    }
}
