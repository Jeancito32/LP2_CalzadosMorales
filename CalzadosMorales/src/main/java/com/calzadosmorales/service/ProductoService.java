package com.calzadosmorales.service;

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
}