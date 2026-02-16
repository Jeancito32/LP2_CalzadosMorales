package com.calzadosmorales.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.calzadosmorales.entity.Rol;
import com.calzadosmorales.entity.Usuario;
import com.calzadosmorales.repository.RolRepository;
import com.calzadosmorales.repository.UsuarioRepository;



@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;
    
    @Autowired
    private RolRepository rolRepo;


    public List<Usuario> listarUsuarios() {
        return usuarioRepo.findAll();
    }

    public void guardarUsuario(Usuario usuario) {
        usuarioRepo.save(usuario);
    }

    public Usuario buscarUsuarioPorId(Integer id) {
        return usuarioRepo.findById(id).orElse(null);
    }
    

    public List<Rol> listarRoles() {
        return rolRepo.findAll();
    }
    
    public Usuario buscarPorUsuario(String usuario){
        return usuarioRepo.findByUsuario(usuario);
    }

    
    
}