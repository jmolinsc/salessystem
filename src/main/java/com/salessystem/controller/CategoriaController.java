package com.salessystem.controller;

import com.salessystem.model.Categoria;
import com.salessystem.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.findAllOrderByNombre());
        return "categorias/list";
    }

    @GetMapping("/list")
    public String listarCategoriasAlternative() {
        return "redirect:/categorias";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/form";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@Valid @ModelAttribute Categoria categoria, 
                                 BindingResult result, 
                                 RedirectAttributes redirectAttributes) {
        
        // Validar nombre único
        if (categoriaService.existsByNombre(categoria.getNombre())) {
            result.rejectValue("nombre", "categoria.nombre.exists", "Ya existe una categoría con este nombre");
        }
        
        if (result.hasErrors()) {
            return "categorias/form";
        }
        
        try {
            categoriaService.save(categoria);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la categoría: " + e.getMessage());
        }
        
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = categoriaService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            model.addAttribute("categoria", categoria);
            return "categorias/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Categoría no encontrada");
            return "redirect:/categorias";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarCategoria(@PathVariable Long id,
                                    @Valid @ModelAttribute Categoria categoria,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        
        categoria.setId(id);
        
        // Validar nombre único (excluyendo la categoría actual)
        categoriaService.findByNombre(categoria.getNombre())
                .ifPresent(existingCategoria -> {
                    if (!existingCategoria.getId().equals(id)) {
                        result.rejectValue("nombre", "categoria.nombre.exists", "Ya existe una categoría con este nombre");
                    }
                });
        
        if (result.hasErrors()) {
            // Recargar los productos asociados para mostrar en el formulario
            categoriaService.findById(id).ifPresent(existingCategoria -> 
                categoria.setProductos(existingCategoria.getProductos()));
            return "categorias/form";
        }
        
        try {
            categoriaService.save(categoria);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la categoría: " + e.getMessage());
        }
        
        return "redirect:/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = categoriaService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            
            // Verificar que no tenga productos asociados
            if (!categoria.getProductos().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "No se puede eliminar la categoría porque tiene " + categoria.getProductos().size() + " productos asociados");
                return "redirect:/categorias";
            }
            
            categoriaService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría eliminada exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la categoría: " + e.getMessage());
        }
        
        return "redirect:/categorias";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<Categoria> buscarCategorias(@RequestParam String query) {
        return categoriaService.search(query);
    }
}