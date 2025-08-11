package com.salessystem.controller;

import com.salessystem.model.TipoDocumento;
import com.salessystem.service.ComportamientoService;
import com.salessystem.service.TipoDocumentoService;
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
@RequestMapping("/tipos-documento")
public class TipoDocumentoController {

    @Autowired
    private TipoDocumentoService tipoDocumentoService;

    @Autowired
    private ComportamientoService comportamientoService;

    // Listar tipos de documento
    @GetMapping
    public String index(Model model) {
        List<TipoDocumento> tiposDocumento = tipoDocumentoService.findAll();
        model.addAttribute("tiposDocumento", tiposDocumento);
        return "tipos-documento/list";
    }

    // Mostrar formulario para nuevo tipo de documento
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("tipoDocumento", new TipoDocumento());
        model.addAttribute("isNew", true);
        model.addAttribute("comportamientos", comportamientoService.findAll());
        return "tipos-documento/form";
    }

    // Mostrar formulario para editar tipo de documento
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<TipoDocumento> tipoDocumentoOpt = tipoDocumentoService.findById(id);
        if (tipoDocumentoOpt.isPresent()) {
            model.addAttribute("tipoDocumento", tipoDocumentoOpt.get());
            model.addAttribute("isNew", false);
            return "tipos-documento/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tipo de documento no encontrado.");
            return "redirect:/tipos-documento";
        }
    }

    // Guardar tipo de documento
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute TipoDocumento tipoDocumento, 
                         BindingResult bindingResult, 
                         Model model, 
                         RedirectAttributes redirectAttributes) {
        
        // Validar mov único
        boolean isNew = tipoDocumento.getId() == null;
        boolean movExists = isNew ? 
            tipoDocumentoService.existsByMov(tipoDocumento.getMov()) :
            tipoDocumentoService.existsByMovAndIdNot(tipoDocumento.getMov(), tipoDocumento.getId());
            
        if (movExists) {
            bindingResult.rejectValue("mov", "error.tipoDocumento", "Ya existe un tipo de documento con este mov.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("tipoDocumento", tipoDocumento);
            model.addAttribute("isNew", isNew);
            return "tipos-documento/form";
        }

        try {
            tipoDocumentoService.save(tipoDocumento);
            String mensaje = isNew ? "Tipo de documento creado exitosamente." : "Tipo de documento actualizado exitosamente.";
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/tipos-documento";
        } catch (Exception e) {
            model.addAttribute("tipoDocumento", tipoDocumento);
            model.addAttribute("isNew", isNew);
            model.addAttribute("error", "Error al guardar el tipo de documento: " + e.getMessage());
            return "tipos-documento/form";
        }
    }

    // Eliminar tipo de documento
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<TipoDocumento> tipoDocumentoOpt = tipoDocumentoService.findById(id);
            if (tipoDocumentoOpt.isPresent()) {
                TipoDocumento tipoDocumento = tipoDocumentoOpt.get();
                
                tipoDocumentoService.deleteById(id);
                redirectAttributes.addFlashAttribute("success", 
                    "Tipo de documento '" + tipoDocumento.getDescripcion() + "' eliminado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tipo de documento no encontrado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el tipo de documento: " + e.getMessage());
        }
        return "redirect:/tipos-documento";
    }

    // Cambiar estatus del tipo de documento
    @PostMapping("/cambiar-estatus/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cambiarEstatus(@PathVariable Long id, 
                                                             @RequestParam String estatus) {
        Map<String, Object> response = new HashMap<>();
        try {
            TipoDocumento tipoDocumento = tipoDocumentoService.cambiarEstatus(id, estatus);
            response.put("success", true);
            response.put("message", "Estatus cambiado exitosamente");
            response.put("nuevoEstatus", tipoDocumento.getEstatus());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estatus: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // Validar mov único (AJAX)
    @GetMapping("/validar-mov")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> validarMov(@RequestParam String mov, 
                                                          @RequestParam(required = false) Long id) {
        Map<String, Boolean> response = new HashMap<>();
        boolean existe = (id == null) ? 
            tipoDocumentoService.existsByMov(mov) :
            tipoDocumentoService.existsByMovAndIdNot(mov, id);
        response.put("existe", existe);
        return ResponseEntity.ok(response);
    }

    // API para obtener tipos de documento por módulo (AJAX)
    @GetMapping("/api/por-modulo/{modulo}")
    @ResponseBody
    public ResponseEntity<List<TipoDocumento>> getTiposPorModulo(@PathVariable String modulo) {
        List<TipoDocumento> tipos = tipoDocumentoService.findByModulo(modulo);
        return ResponseEntity.ok(tipos);
    }

    // Buscar tipos de documento (AJAX)
    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<List<TipoDocumento>> buscar(@RequestParam(required = false) String q) {
        List<TipoDocumento> tiposDocumento;
        if (q != null && !q.trim().isEmpty()) {
            tiposDocumento = tipoDocumentoService.search(q);
        } else {
            tiposDocumento = tipoDocumentoService.findAllActivos();
        }
        return ResponseEntity.ok(tiposDocumento);
    }
}
