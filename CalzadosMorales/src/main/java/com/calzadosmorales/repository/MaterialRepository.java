package com.calzadosmorales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calzadosmorales.entity.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
    Material findByNombre(String nombre);
    
    List<Material> findByEstado(Boolean estado);
}