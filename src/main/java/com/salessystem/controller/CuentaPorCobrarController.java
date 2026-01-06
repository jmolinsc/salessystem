package com.salessystem.controller;

import com.salessystem.model.CuentaPorCobrar;
import com.salessystem.service.CuentaPorCobrarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/cuentas-por-cobrar")
public class CuentaPorCobrarController {

    private final CuentaPorCobrarService cuentaService;

    public CuentaPorCobrarController(CuentaPorCobrarService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cuentas", cuentaService.findAll());
        return "cuentas/list";
    }

    @GetMapping("/list")
    public String listarAlt() {
        return "redirect:/cuentas-por-cobrar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cuenta", new CuentaPorCobrar());
        return "cuentas/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute CuentaPorCobrar cuenta,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cuentas/form";
        }
        try {
            cuentaService.guardar(cuenta);
            redirectAttributes.addFlashAttribute("mensaje", "Cuenta por cobrar guardada");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/cuentas-por-cobrar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "cuentas/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<CuentaPorCobrar> opt = cuentaService.findById(id);
        CuentaPorCobrar cuenta = opt.orElseGet(CuentaPorCobrar::new);
        model.addAttribute("cuenta", cuenta);
        return "cuentas/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<CuentaPorCobrar> opt = cuentaService.findById(id);
            if (opt.isPresent()) {
                CuentaPorCobrar cp = opt.get();
                // marca como anulada/borrada
                cp.setEstado("ANULADA");
                cuentaService.guardar(cp);
                redirectAttributes.addFlashAttribute("mensaje", "Cuenta anulada");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Cuenta no encontrada");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/cuentas-por-cobrar";
    }
}
