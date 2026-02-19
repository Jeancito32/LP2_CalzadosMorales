package com.calzadosmorales.controller;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.calzadosmorales.entity.Usuario;
import com.calzadosmorales.repository.UsuarioRepository;
import com.calzadosmorales.service.DashboardVendedorService;
import com.calzadosmorales.service.DashboardAdminService; 
import java.util.Map;

@Controller
public class DashboardController { 

    @Autowired
    private DashboardVendedorService vendedorService;

    @Autowired
    private DashboardAdminService adminService; 

    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping("/index")
    public String dashboard(Model model, Authentication auth) {
    	
        // Verificacion de seguridad basica
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login"; 
        }

        String username = auth.getName();
        Usuario u = usuarioRepo.findByUsuario(username);
        
        // Verificacion de existencia del usuario
        if (u == null) {
            return "redirect:/login?error=user_not_found";
        }

        int rolId = u.getRol().getId_rol();
        
        // Datos de cabecera comunes
        model.addAttribute("userNombreCompleto", u.getNombre());
        model.addAttribute("userRol", u.getRol().getNombre()); // Útil para mostrar "Admin" o "Vendedor"
        model.addAttribute("rolId", rolId);
        
        // Carga de datos segun Rol
        if (rolId == 1) { // ADMINISTRADOR
            Map<String, Object> statsAdmin = adminService.cargarPanelAdministrativo();
            if (statsAdmin != null) model.addAllAttributes(statsAdmin); 

        } 
        else if (rolId == 2) { // VENDEDOR
            Map<String, Object> datosVendedor = vendedorService.obtenerDatosDashboardVendedor(u.getId_usuario());
            if (datosVendedor != null) model.addAllAttributes(datosVendedor); 

        }

        return "index"; 
    }
}