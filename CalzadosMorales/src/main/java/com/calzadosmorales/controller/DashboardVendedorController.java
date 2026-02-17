package com.calzadosmorales.controller;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.calzadosmorales.entity.Usuario;
import com.calzadosmorales.repository.UsuarioRepository;
import com.calzadosmorales.service.DashboardVendedorService;

@Controller
public class DashboardVendedorController {

    @Autowired
    private DashboardVendedorService dashboardService;

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
                
               
                if (u.getRol().getId_rol() == 2) {
                    int idReal = u.getId_usuario();
                    model.addAttribute("ventasMes", dashboardService.ventasMes(idReal));
                    model.addAttribute("comision", dashboardService.comisionMes(idReal));
                    model.addAttribute("cantidadVentas", dashboardService.cantidadVentas(idReal));
                    model.addAttribute("paresVendidos", dashboardService.paresVendidos(idReal));
                    model.addAttribute("productoEstrella", dashboardService.productoEstrella(idReal));
                    model.addAttribute("categoriasTop", dashboardService.categoriasTop(idReal));
                }
            }
        }
        return "index";
    }
}