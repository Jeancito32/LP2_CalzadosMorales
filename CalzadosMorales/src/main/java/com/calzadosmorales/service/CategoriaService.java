package com.calzadosmorales.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.calzadosmorales.entity.Categoria;
import com.calzadosmorales.repository.CategoriaRepository;


@Service
public class CategoriaService {

    @Autowired 
    private CategoriaRepository repo;

    public List<Categoria> listarTodas() {
        return repo.findAll();
    }

  
    public void guardar(Categoria categoria) {
        repo.save(categoria);
    }
}