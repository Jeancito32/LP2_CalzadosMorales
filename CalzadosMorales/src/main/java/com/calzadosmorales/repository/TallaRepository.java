package com.calzadosmorales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calzadosmorales.entity.Talla;

@Repository
public interface TallaRepository extends JpaRepository<Talla, Integer> {
    Talla findByNombre(String nombre);
    
    List<Talla> findByEstado(Boolean estado);
}