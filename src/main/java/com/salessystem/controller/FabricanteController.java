package com.salessystem.controller;

import com.salessystem.model.Fabricante;
import com.salessystem.service.FabricanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/fabricantes")
public class FabricanteController {

    @Autowired
    private FabricanteService fabricanteService;

    // Listar fabricantes
    @GetMapping("/list")
    public String listfabricantes(Model model) {
        List<Fabricante> fabricantes = fabricanteService.findAll();
        model.addAttribute("fabricantes", fabricantes);
        return "fabricantes/list";
    }

    @GetMapping
    public String index(Model model) {
        List<Fabricante> fabricantes = fabricanteService.findAll();
        model.addAttribute("fabricantes", fabricantes);
        return "fabricantes/list";
    }

    // Mostrar formulario para nuevo fabricante
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("fabricante", new Fabricante());
        model.addAttribute("isNew", true);
        return "fabricantes/form";
    }

    // Mostrar formulario para editar fabricante
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Fabricante> fabricanteOpt = fabricanteService.findById(id);
        if (fabricanteOpt.isPresent()) {
            model.addAttribute("fabricante", fabricanteOpt.get());
            model.addAttribute("isNew", false);
            return "fabricantes/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Fabricante no encontrado.");
            return "redirect:/fabricantes";
        }
    }

    // Guardar fabricante
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Fabricante fabricante,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validar nombre único
        boolean isNew = fabricante.getId() == null;
        boolean nombreExists = isNew ? fabricanteService.existsByNombre(fabricante.getNombre())
                : fabricanteService.existsByNombreAndIdNot(fabricante.getNombre(), fabricante.getId());

        if (nombreExists) {
            bindingResult.rejectValue("nombre", "error.fabricante", "Ya existe un fabricante con este nombre.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("fabricante", fabricante);
            model.addAttribute("isNew", isNew);
            return "fabricantes/form";
        }

        try {
            fabricanteService.save(fabricante);
            String mensaje = isNew ? "Fabricante creado exitosamente." : "Fabricante actualizado exitosamente.";
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/fabricantes";
        } catch (Exception e) {
            model.addAttribute("fabricante", fabricante);
            model.addAttribute("isNew", isNew);
            model.addAttribute("error", "Error al guardar el fabricante: " + e.getMessage());
            return "fabricantes/form";
        }
    }

    // Eliminar fabricante
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Fabricante> fabricanteOpt = fabricanteService.findById(id);
            if (fabricanteOpt.isPresent()) {
                Fabricante fabricante = fabricanteOpt.get();

                // Verificar si puede ser eliminado
                if (!fabricanteService.canDelete(id)) {
                    long productCount = fabricanteService.countProductosByFabricante(id);
                    redirectAttributes.addFlashAttribute("error",
                            "No se puede eliminar el fabricante '" + fabricante.getNombre() +
                                    "' porque tiene " + productCount + " producto(s) asociado(s).");
                    return "redirect:/fabricantes";
                }

                fabricanteService.deleteById(id);
                redirectAttributes.addFlashAttribute("success",
                        "Fabricante '" + fabricante.getNombre() + "' eliminado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Fabricante no encontrado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el fabricante: " + e.getMessage());
        }
        return "redirect:/fabricantes";
    }

    // Cambiar estatus del fabricante
    @PostMapping("/cambiar-estatus/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cambiarEstatus(@PathVariable Long id,
            @RequestParam String estatus) {
        Map<String, Object> response = new HashMap<>();
        try {
            Fabricante fabricante = fabricanteService.cambiarEstatus(id, estatus);
            response.put("success", true);
            response.put("message", "Estatus cambiado exitosamente");
            response.put("nuevoEstatus", fabricante.getEstatus());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estatus: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // Validar nombre único (AJAX)
    @GetMapping("/validar-nombre")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> validarNombre(@RequestParam String nombre,
            @RequestParam(required = false) Long id) {
        Map<String, Boolean> response = new HashMap<>();
        boolean existe = (id == null) ? fabricanteService.existsByNombre(nombre)
                : fabricanteService.existsByNombreAndIdNot(nombre, id);
        response.put("existe", existe);
        return ResponseEntity.ok(response);
    }

    // Buscar fabricantes (AJAX para select)
    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<List<Fabricante>> buscar(@RequestParam(required = false) String q) {
        List<Fabricante> fabricantes;
        if (q != null && !q.trim().isEmpty()) {
            fabricantes = fabricanteService.search(q);
        } else {
            fabricantes = fabricanteService.findAllActivos();
        }
        return ResponseEntity.ok(fabricantes);
    }
}
