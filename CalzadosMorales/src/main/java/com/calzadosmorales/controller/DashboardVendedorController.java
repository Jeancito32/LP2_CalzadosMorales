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

    @GetMapping("/index")
    public String dashboard(@RequestParam("idUsuario") int idUsuario, Model model){

        model.addAttribute("ventasMes", dashboardService.ventasMes(idUsuario));
        model.addAttribute("comision", dashboardService.comisionMes(idUsuario));
        model.addAttribute("cantidadVentas", dashboardService.cantidadVentas(idUsuario));
        model.addAttribute("paresVendidos", dashboardService.paresVendidos(idUsuario));
        model.addAttribute("productoEstrella", dashboardService.productoEstrella(idUsuario));

        return "index";
    }
}