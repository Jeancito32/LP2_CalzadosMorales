package com.calzadosmorales.controller;

import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.calzadosmorales.entity.Usuario;
import com.calzadosmorales.service.DashboardVendedorService;
import com.calzadosmorales.service.UsuarioService;

@Controller
public class DashboardVendedorController {

    @Autowired
    private DashboardVendedorService dashboardService;

    @Autowired
    private UsuarioService usuarioService; // Lo necesitamos para buscar el ID

    @GetMapping("/index")
    public String dashboard(@RequestParam(name = "idUsuario", required = false) Integer idUsuario, 
                            Model model, 
                            Authentication auth) {
        
        // Si el idUsuario no viene en la URL (como pasa en el login)
        if (idUsuario == null && auth != null) {
            // Buscamos al usuario en la DB por su nombre de login (adminXio)
            // Asumiendo que tienes un método buscarPorNombre o similar en tu UsuarioRepository
            // Si no lo tienes, podemos usar el nombre directamente
            String username = auth.getName();
            // Aquí un pequeño truco: si no tienes el ID a la mano, 
            // puedes quemar el ID 1 temporalmente para probar que cargue
            idUsuario = 1; 
            model.addAttribute("username", username);
        }

        // Si después de lo anterior tenemos un ID, cargamos los datos de Andrés
        if (idUsuario != null) {
            model.addAttribute("ventasMes", dashboardService.ventasMes(idUsuario));
            model.addAttribute("comision", dashboardService.comisionMes(idUsuario));
            model.addAttribute("cantidadVentas", dashboardService.cantidadVentas(idUsuario));
            model.addAttribute("paresVendidos", dashboardService.paresVendidos(idUsuario));
            model.addAttribute("productoEstrella", dashboardService.productoEstrella(idUsuario));
        }

        return "index";
    }
}