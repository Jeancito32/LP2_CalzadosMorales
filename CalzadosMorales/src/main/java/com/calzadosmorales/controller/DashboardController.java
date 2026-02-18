package com.calzadosmorales.controller;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.calzadosmorales.entity.Usuario;
import com.calzadosmorales.repository.UsuarioRepository;
import com.calzadosmorales.service.DashboardVendedorService;
import com.calzadosmorales.service.DashboardAdminService; // Importamos el nuevo
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
        if (auth != null) {
            String username = auth.getName();
            Usuario u = usuarioRepo.findByUsuario(username);
            
            if (u != null) {
             
                model.addAttribute("userNombreCompleto", u.getNombre());
                model.addAttribute("userRol", u.getRol().getNombre());
                model.addAttribute("rolId", u.getRol().getId_rol());
                
          
                if (u.getRol().getId_rol() == 1) {
                    Map<String, Object> stats = adminService.cargarPanelAdministrativo();
                    model.addAttribute("stats", stats);
                }
                
               
                else if (u.getRol().getId_rol() == 2) {
                    int idReal = u.getId_usuario();
                    model.addAttribute("ventasMes", vendedorService.ventasMes(idReal));
                    model.addAttribute("comision", vendedorService.comisionMes(idReal));
                    model.addAttribute("cantidadVentas", vendedorService.cantidadVentas(idReal));
                    model.addAttribute("paresVendidos", vendedorService.paresVendidos(idReal));
                    model.addAttribute("productoEstrella", vendedorService.productoEstrella(idReal));
                    model.addAttribute("categoriasTop", vendedorService.categoriasTop(idReal));
                }
            }
        }
        return "index"; 
    }
}