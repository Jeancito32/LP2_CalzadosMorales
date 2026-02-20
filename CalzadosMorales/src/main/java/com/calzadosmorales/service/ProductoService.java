package com.calzadosmorales.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.calzadosmorales.entity.*;
import com.calzadosmorales.repository.*;

@Service
public class ProductoService {

    @Autowired private ProductoRepository productoRepo;
    @Autowired private CategoriaRepository categoriaRepo;
    @Autowired private TallaRepository tallaRepo;
    @Autowired private ColorRepository colorRepo;
    @Autowired private MaterialRepository materialRepo;

    public List<Producto> listarProductos() {
        return productoRepo.findAll();
    }

    public void guardarProducto(Producto p) {
        productoRepo.save(p);
    }

    public Producto buscarProducto(Integer id) {
        return productoRepo.findById(id).orElse(null);
    }

    public boolean existeProductoIgual(String nombre, Talla talla, Color color) {
        return productoRepo.existsByNombreAndTallaAndColor(nombre, talla, color);
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepo.findByEstado(true);
    }

    public List<Talla> listarTallas() {
        return tallaRepo.findByEstado(true);
    }

    public List<Color> listarColores() {
        return colorRepo.findByEstado(true);
    }

    public List<Material> listarMateriales() {
        return materialRepo.findByEstado(true);
    }
    
    // --- CONSULTA DE STOCK FILTRADA 
    public List<Producto> consultarStockConFiltros(int idCat, String talla) {
        List<Object[]> resultados = productoRepo.consultaStockFiltros(idCat, talla);
        List<Producto> listaCorregida = new ArrayList<>();
        
        try {
            for (Object[] fila : resultados) {
                Producto p = new Producto();
             

                if (fila[0] != null) p.setId_producto(((Number) fila[0]).intValue());
                if (fila[1] != null) p.setNombre(fila[1].toString());
                

                if (fila[5] != null) {
                    p.setPrecio(new BigDecimal(fila[5].toString()));
                } else {
                    p.setPrecio(BigDecimal.ZERO);
                }
                
                if (fila[6] != null) p.setStock(((Number) fila[6]).intValue());
                
                // RELACIONES (Evitar NullPointerException en el HTML)
                p.setCategoria(new Categoria()); 
                if (fila[2] != null) p.getCategoria().setNombre(fila[2].toString());

                p.setTalla(new Talla());
                if (fila[3] != null) p.getTalla().setNombre(fila[3].toString());

                p.setColor(new Color());
                if (fila[4] != null) p.getColor().setNombre(fila[4].toString());

                listaCorregida.add(p);
            }
        } catch (Exception e) {
            System.err.println("Error procesando fila de producto en Service: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listaCorregida;
    }

    public List<Object[]> obtenerProductosEstancados() {
        return productoRepo.productosEstancados();
    }
}