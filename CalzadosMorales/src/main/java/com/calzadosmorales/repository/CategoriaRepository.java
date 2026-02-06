package com.calzadosmorales.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calzadosmorales.entity.Categoria;



@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    Categoria findByNombre(String nombre);
}