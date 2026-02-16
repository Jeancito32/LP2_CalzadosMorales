package com.calzadosmorales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.calzadosmorales.entity.Usuario;
import com.calzadosmorales.repository.UsuarioRepository;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @ModelAttribute
    public void addAttributes(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            Usuario u = usuarioRepo.findByUsuario(auth.getName());
            if (u != null) {
                
                model.addAttribute("userNombreCompleto", u.getNombre());
                model.addAttribute("userRol", u.getRol().getNombre());
            }
        }
    }
}