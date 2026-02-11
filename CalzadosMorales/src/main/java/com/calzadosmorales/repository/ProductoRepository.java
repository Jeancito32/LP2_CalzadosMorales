package com.calzadosmorales.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.calzadosmorales.entity.Color;
import com.calzadosmorales.entity.Producto;
import com.calzadosmorales.entity.Talla;


public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    

    boolean existsByNombreAndTallaAndColor(String nombre, Talla talla, Color color);
}