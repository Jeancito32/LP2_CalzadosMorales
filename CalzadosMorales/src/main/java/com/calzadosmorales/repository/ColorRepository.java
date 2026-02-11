package com.calzadosmorales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calzadosmorales.entity.Color;



@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {

    Color findByNombre(String nombre);
    
    List<Color> findByEstado(Boolean estado);
}