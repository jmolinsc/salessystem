package com.salessystem.controller;

import com.salessystem.model.Menu;
import com.salessystem.model.Permiso;
import com.salessystem.model.Rol;
import com.salessystem.model.Usuario;
import com.salessystem.service.MenuService;
import com.salessystem.service.PermisoService;
import com.salessystem.service.RolService;
import com.salessystem.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/usuarios/web")
public class UsuarioWebController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private PermisoService permisoService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // === GESTIÓN DE USUARIOS ===

    @GetMapping
    public String listUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/list";
    }

    @GetMapping("/nuevo")
    public String showFormUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.findAllActive());
        return "usuarios/form";
    }

    @GetMapping("/editar/{id}")
    public String editUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            model.addAttribute("roles", rolService.findAllActive());
            return "usuarios/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuarios/web";
        }
    }

    @PostMapping("/guardar")
    public String saveUsuario(@ModelAttribute Usuario usuario,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                             RedirectAttributes redirectAttributes) {
        try {
            // Si es nuevo usuario, encriptar password
            if (usuario.getId() == null) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            } else {
                // Si está editando y no cambió password, mantener el anterior
                Optional<Usuario> existingUser = usuarioService.findById(usuario.getId());
                if (existingUser.isPresent() && (usuario.getPassword() == null || usuario.getPassword().isEmpty())) {
                    usuario.setPassword(existingUser.get().getPassword());
                } else if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                }
            }

            // Asignar roles
            Set<Rol> roles = new HashSet<>();
            if (roleIds != null) {
                for (Long roleId : roleIds) {
                    Optional<Rol> rol = rolService.findById(roleId);
                    if (rol.isPresent()) {
                        roles.add(rol.get());
                    }
                }
            }
            usuario.setRoles(roles);

            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar usuario: " + e.getMessage());
        }
        return "redirect:/usuarios/web";
    }

    @GetMapping("/eliminar/{id}")
    public String deleteUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/usuarios/web";
    }

    // === GESTIÓN DE ROLES ===

    @GetMapping("/roles")
    public String listRoles(Model model) {
        model.addAttribute("roles", rolService.findAll());
        return "usuarios/roles";
    }

    @GetMapping("/roles/nuevo")
    public String showFormRol(Model model) {
        model.addAttribute("rol", new Rol());
        model.addAttribute("permisos", permisoService.findAll());
        model.addAttribute("menusPadre", menuService.getMenusPadreActivos());
        model.addAttribute("menusIndependientes", menuService.getMenusIndependientes());
        return "usuarios/rol-form";
    }

    @GetMapping("/roles/editar/{id}")
    public String editRol(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Rol> rol = rolService.findByIdWithPermisos(id);
        if (rol.isPresent()) {
            model.addAttribute("rol", rol.get());
            model.addAttribute("permisos", permisoService.findAll());
            model.addAttribute("menusPadre", menuService.getMenusPadreActivos());
            model.addAttribute("menusIndependientes", menuService.getMenusIndependientes());
            return "usuarios/rol-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Rol no encontrado");
            return "redirect:/usuarios/web/roles";
        }
    }

    @PostMapping("/roles/guardar")
    public String saveRol(@ModelAttribute Rol rol,
                         @RequestParam(value = "permisoIds", required = false) List<Long> permisoIds,
                         @RequestParam(value = "menuIds", required = false) List<Long> menuIds,
                         RedirectAttributes redirectAttributes) {
        try {
            // Asignar permisos
            Set<Permiso> permisos = new HashSet<>();
            if (permisoIds != null) {
                for (Long permisoId : permisoIds) {
                    Optional<Permiso> permiso = permisoService.findById(permisoId);
                    if (permiso.isPresent()) {
                        permisos.add(permiso.get());
                    }
                }
            }
            rol.setPermisos(permisos);

            // Asignar menús
            Set<Menu> menus = new HashSet<>();
            if (menuIds != null) {
                for (Long menuId : menuIds) {
                    Optional<Menu> menu = menuService.findById(menuId);
                    if (menu.isPresent()) {
                        menus.add(menu.get());
                    }
                }
            }
            rol.setMenus(menus);

            rolService.save(rol);
            redirectAttributes.addFlashAttribute("success", 
                "Rol guardado exitosamente con " + permisos.size() + " permisos y " + menus.size() + " menús");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar rol: " + e.getMessage());
        }
        return "redirect:/usuarios/web/roles";
    }

    @GetMapping("/roles/eliminar/{id}")
    public String deleteRol(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            rolService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Rol eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar rol: " + e.getMessage());
        }
        return "redirect:/usuarios/web/roles";
    }

    // === GESTIÓN DE PERMISOS ===

    @GetMapping("/permisos")
    public String listPermisos(Model model) {
        model.addAttribute("permisos", permisoService.findAll());
        return "usuarios/permisos";
    }

    @GetMapping("/permisos/nuevo")
    public String showFormPermiso(Model model) {
        model.addAttribute("permiso", new Permiso());
        return "usuarios/permiso-form";
    }

    @GetMapping("/permisos/editar/{id}")
    public String editPermiso(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Permiso> permiso = permisoService.findById(id);
        if (permiso.isPresent()) {
            model.addAttribute("permiso", permiso.get());
            return "usuarios/permiso-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Permiso no encontrado");
            return "redirect:/usuarios/web/permisos";
        }
    }

    @PostMapping("/permisos/guardar")
    public String savePermiso(@ModelAttribute Permiso permiso, RedirectAttributes redirectAttributes) {
        try {
            permisoService.save(permiso);
            redirectAttributes.addFlashAttribute("success", "Permiso guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar permiso: " + e.getMessage());
        }
        return "redirect:/usuarios/web/permisos";
    }

    @GetMapping("/permisos/eliminar/{id}")
    public String deletePermiso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            permisoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Permiso eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar permiso: " + e.getMessage());
        }
        return "redirect:/usuarios/web/permisos";
    }

    // === GESTIÓN DE MENÚS ===

    @GetMapping("/menus")
    public String listMenus(Model model) {
        model.addAttribute("menus", menuService.findAll());
        return "usuarios/menus";
    }

    @GetMapping("/menus/nuevo")
    public String showFormMenu(Model model) {
        model.addAttribute("menu", new Menu());
        model.addAttribute("menusPadre", menuService.getMenusPadreActivos());
        return "usuarios/menu-form";
    }

    @GetMapping("/menus/editar/{id}")
    public String editMenu(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Menu> menu = menuService.findById(id);
        if (menu.isPresent()) {
            model.addAttribute("menu", menu.get());
            model.addAttribute("menusPadre", menuService.getMenusPadreActivos());
            return "usuarios/menu-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Menú no encontrado");
            return "redirect:/usuarios/web/menus";
        }
    }

    @PostMapping("/menus/guardar")
    public String saveMenu(@ModelAttribute Menu menu, RedirectAttributes redirectAttributes) {
        try {
            menuService.save(menu);
            redirectAttributes.addFlashAttribute("success", "Menú guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar menú: " + e.getMessage());
        }
        return "redirect:/usuarios/web/menus";
    }

    @GetMapping("/menus/eliminar/{id}")
    public String deleteMenu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            menuService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Menú eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar menú: " + e.getMessage());
        }
        return "redirect:/usuarios/web/menus";
    }
}
